package com.razchen.look4u;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.java_classes.Candidate;
import com.razchen.look4u.util.UserMessages;
import com.razchen.look4u.util.UtilFunctions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import androidx.annotation.NonNull;

import static com.razchen.look4u.util.Keys.USER_CHAT_ID;
import static com.razchen.look4u.util.Keys.USER_ID;

//credit for https://www.youtube.com/watch?v=5Tm--PHhbJo&t=541s
public class MyMatchesActivity extends UserMenu {

    private final int numberOfresults = 2;
    private Firebase firebase;
    private ListView listView;
    private long numberOfTotalCandidats;
    private String userID = "";
    private Stack<String> categoriesStack = new Stack<>();
    private ArrayList<String> myPath = new ArrayList<>();
    private static final char SIGN_OF_QUESTIONNAIRE = '_';
    private DatabaseReference candidatesDBRef;
    private ArrayList<Candidate> candidateArrayList = new ArrayList<>();
    private ArrayAdapter<String> myCategoriesAdapter;
    private boolean listClickable = true;
    private Button candidates_backButton;
    private Button showAllResultsButton;
    private TextView myMatchesPathTextView;
    private SpinKitView spinKit;
    private Boolean showFirstCandidats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_matches);

        showFirstCandidats=false;
        firebase = new Firebase();
        numberOfTotalCandidats = 0;
        Bundle inBundle = getIntent().getExtras();
        userID = inBundle.get(USER_ID).toString();
        listView = findViewById(R.id.MyMatchesListView);

        candidatesDBRef = firebase.getUsersTable().child(userID);
        candidates_backButton = findViewById(R.id.candidates_backButton);
        showAllResultsButton = findViewById(R.id.showAllResultsButton);
        myMatchesPathTextView = findViewById(R.id.myMatchesPathTextView);
        spinKit=findViewById(R.id.spinKit);


        showAllResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showFirstCandidats==false)
                {
                    showAllResultsButton.setText(R.string.View_previous_results);
                }
                 else
                     showAllResultsButton.setText(R.string.View_the_rest_of_the_results);
             showCandidates(showFirstCandidats);
                showFirstCandidats=!showFirstCandidats;
            }
        });

        candidates_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backOneCategory();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listClickable) {
                    listClickable = false;
                    String chosenCategory = myCategoriesAdapter.getItem(position);
                    categoriesStack.push(chosenCategory);
                    myPath.add(chosenCategory);
                    myMatchesPathTextView.setText(convertMyPathArray(myPath));
                    candidatesDBRef = candidatesDBRef.child(chosenCategory);
                    readCategoriesFromFB();
                    candidates_backButton.setVisibility(View.VISIBLE);
                    candidates_backButton.setAlpha(1);
                    candidates_backButton.setEnabled(true);
                }
            }
        });

        myCategoriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        //myCategoriesAdapter1 = new MycategoriesAdapter(MyMatchesActivity.this, categoriesArraytList);
        listView.setAdapter(myCategoriesAdapter);

        candidatesDBRef = candidatesDBRef.child(Firebase.CANDIDATES);
        readCategoriesFromFB();

    }
    private String convertMyPathArray(ArrayList<String> arrayList) {
        if (arrayList.size() == 0)
            return "";
        String categoriesString = arrayList.toString();
        return categoriesString.substring(1, categoriesString.length() - 1).replace(",", "->");

    }
    private void readCategoriesFromFB() {
        spinKit.setVisibility(View.VISIBLE);

        if (!listView.getAdapter().equals(myCategoriesAdapter)) {
            listView.setAdapter(myCategoriesAdapter);
        }
        candidatesDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        myCategoriesAdapter.clear();

                    }
                });

                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().charAt(0) == SIGN_OF_QUESTIONNAIRE) {
                        if(numberOfTotalCandidats==0)
                        numberOfTotalCandidats=dataSnapshot.getChildrenCount();
                        String candidateUserId = snapshot.getKey().substring(1);
                        readQuestionnaire(candidateUserId);

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myCategoriesAdapter.add(snapshot.getKey());
                                spinKit.setVisibility(View.GONE);
                                showAllResultsButton.setVisibility(View.GONE);

                            }
                        });

                    }

                }
                //listView.setClickable(true);
                listClickable = true;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readQuestionnaire(final String candidateUserId) {
        DatabaseReference databaseReference = firebase.getUsersTable().child(userID).child(Firebase.CANDIDATES);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < myPath.size(); i++)
                    dataSnapshot = dataSnapshot.child(myPath.get(i));
                dataSnapshot = dataSnapshot.child("_" + candidateUserId);
                ArrayList<String> candidateAnswers = new ArrayList<>();
                getAnswersFromFB(dataSnapshot, candidateAnswers);
                compareTheAnswers(candidateUserId, candidateAnswers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void getAnswersFromFB(DataSnapshot dataSnapshot, ArrayList<String> answersArr) {
        dataSnapshot = dataSnapshot.child("questions");
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

            answersArr.add(snapshot.child("selectedAnswer").getValue().toString());
        }

    }
    private void compareTheAnswers(final String candidateUserId, final ArrayList<String> candidateAnswers) {
        DatabaseReference usersDBRef = firebase.getUsersTable().child(userID);
        usersDBRef = usersDBRef.child(Firebase.QUESTIONNAIRES_TABLE_NAME);
        final ArrayList<String> myAnswers = new ArrayList<>();
        for (int i = 0; i < myPath.size(); i++)
            usersDBRef = usersDBRef.child(myPath.get(i));
        usersDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatabaseReference questionnaireRef = firebase.getQuestionnairesTable().child(snapshot.getKey());
                    questionnaireRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           // for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                getAnswersFromFB(dataSnapshot, myAnswers);
                                calculateCompatibility(candidateUserId, candidateAnswers, myAnswers);
                            //}
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void calculateCompatibility(String candidateUserId, ArrayList<String> candidateAnswers, ArrayList<String> myAnswers) {
        int numOfQuestions = myAnswers.size();
        int rightAnswers = 0;
        for (int i = 0; i < numOfQuestions; i++) {
            if (myAnswers.get(i).equals(candidateAnswers.get(i)))
                rightAnswers++;
        }
        int compatibility = Math.round(rightAnswers * 100.0f / numOfQuestions);
        Candidate candidate = new Candidate(candidateUserId, compatibility);

        setCandidateAttributs(candidate);


    }
    private void setCandidateAttributs(final Candidate candidate) {

        DatabaseReference userDBRef = firebase.getUsersTable().child(candidate.getCandidateId());

        userDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                candidate.setMail(dataSnapshot.child("email").getValue().toString());

                int day = Integer.parseInt(dataSnapshot.child("birthday/day").getValue().toString());
                int month = Integer.parseInt(dataSnapshot.child("birthday/month").getValue().toString());
                int year = Integer.parseInt(dataSnapshot.child("birthday/year").getValue().toString());
                candidate.setAge(Functions.getAge(year, month, day));

                String fname = dataSnapshot.child("firstName").getValue().toString();
                String lname = dataSnapshot.child("lastName").getValue().toString();
                candidate.setFullName(fname + " " + lname);
                candidate.setMail(dataSnapshot.child("email").getValue().toString());
                candidate.setImageUrl(dataSnapshot.child("image").getValue().toString());
                candidate.setGender(dataSnapshot.child(Firebase.GENDER).getValue().toString());


                candidateArrayList.add(candidate);
                //numberOfTotalCandidats++;
                if (candidateArrayList.size() == numberOfTotalCandidats) {
                    showAllResultsButton.setVisibility(View.VISIBLE);
                    showCandidates(true);
                    listClickable = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showCandidates(Boolean showFirstResults) {

        if(numberOfresults>=candidateArrayList.size())
        {
            showAllResultsButton.setVisibility(View.GONE);
        }

        int numberOfresultsTo ;
        int numberOfreults_From;
        if((showFirstResults) || (numberOfresults>candidateArrayList.size()))
        {
            numberOfreults_From=0;
            if(candidateArrayList.size()<numberOfresults)
            numberOfresultsTo= candidateArrayList.size();
            else
                numberOfresultsTo=numberOfresults;
        }
        else
        {
            numberOfreults_From=numberOfresults;
            numberOfresultsTo=candidateArrayList.size();
        }

        Collections.sort(candidateArrayList);
        CandidatesAdapter adapter = new CandidatesAdapter(MyMatchesActivity.this, candidateArrayList.subList(numberOfreults_From,numberOfresultsTo));
        spinKit.setVisibility(View.GONE);

        listView.setAdapter(adapter);


    }
    class CandidatesAdapter extends ArrayAdapter<Candidate> {

        Context context;

        private List<Candidate> candidateList;

        CandidatesAdapter(Context c, List<Candidate> candidateArrayList) {
            super(c, R.layout.candidates_row, candidateArrayList);
            this.candidateList = candidateArrayList;
        }

        @NonNull
        @Override
        public View getView(final int position, @androidx.annotation.Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.candidates_row, parent, false);

            TextView candidates_mainTitleTextView = row.findViewById(R.id.userName_TextView);
            candidates_mainTitleTextView.setText(candidateList.get(position).getFullName());

            TextView candidates_subTitle = row.findViewById(R.id.userDescription_TextView);
            candidates_subTitle.setText(candidateList.get(position).getAge() + " " + UtilFunctions.convertGenderChoiceToHebrew(candidateList.get(position).getGender()));

            TextView candidates_matchesTextView = row.findViewById(R.id.candidates_matchesTextView);
            candidates_matchesTextView.setText(UserMessages.Match_percentage + candidateList.get(position).getCompatibility() + "%");


            Button candidates_contactButton = row.findViewById(R.id.candidates_contactButton);
            //candidates_contactTextView.setText(UserMessages.Ways_to_Contact+"\n"+candidateArrayList.get(position).getMail());
            candidates_contactButton.setText(UserMessages.pressToStartAChat);
            candidates_contactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String candidateId = candidateList.get(position).getCandidateId();
                    openChatWithUser(candidateId);
                    saveIDChatInbothSidesInFireBase(candidateId);
                }
            });


            final ImageView user_ImageView = row.findViewById(R.id.user_ImageView);
           final SpinKitView spinKit= row.findViewById(R.id.spinKit);

            Picasso.with(MyMatchesActivity.this).load(candidateList.get(position).getImageUrl()).into(user_ImageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    spinKit.setVisibility(View.GONE);
                    user_ImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {

                }
            });


            return row;
        }

        private void saveIDChatInbothSidesInFireBase(String candidateId) {
            DatabaseReference databaseReferenceUserChatsIDS = firebase.getUserchatsids();
            DatabaseReference databaseReferenceUserChats = firebase.getChatsReference();
            String chatID;
            if (userID.compareTo(candidateId) < 0)
                chatID = userID + "_" + candidateId;
            else
                chatID = candidateId + "_" + userID;

            databaseReferenceUserChatsIDS.child(userID).child(chatID).setValue("null");
            databaseReferenceUserChatsIDS.child(candidateId).child(chatID).setValue("null");

        }

        protected void openChatWithUser(String id) {
            Intent intent = new Intent(MyMatchesActivity.this, ChatMainActivity.class);
            intent.putExtra(USER_CHAT_ID, id);
            intent.putExtra(USER_ID, userID);

            startActivity(intent);
            finish();
        }


    }
    @Override
    public void onBackPressed() {
        if(myPath.size()>0) {
            backOneCategory();

        }
        else
            finish();
    }

    private void backOneCategory() {
        myPath.remove(myPath.size() - 1);
        myMatchesPathTextView.setText(convertMyPathArray(myPath));
        candidatesDBRef = candidatesDBRef.getParent();
        readCategoriesFromFB();
        if (myPath.size() == 0) {
            candidates_backButton.setAlpha(0);
            candidates_backButton.setEnabled(false);
        }
        if (listClickable == false) {
            candidateArrayList.clear();
            numberOfTotalCandidats = 0;

        }
        listClickable = true;
    }
}