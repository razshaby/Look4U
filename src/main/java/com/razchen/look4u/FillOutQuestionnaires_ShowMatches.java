package com.razchen.look4u;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.java_classes.MyDate;
import com.razchen.look4u.server.Boundaries.userBoundary;
import com.razchen.look4u.server.Service;
import com.razchen.look4u.util.EnglishStrings;
import com.razchen.look4u.util.Keys;
import com.razchen.look4u.util.UserMessages;
import com.razchen.look4u.util.UtilFunctions;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;

import static com.razchen.look4u.util.Keys.CATEGORY_USER_ID;
import static com.razchen.look4u.util.Keys.FROM_AGE;
import static com.razchen.look4u.util.Keys.QUESTIONNAIRE_CATEGORY;
import static com.razchen.look4u.util.Keys.TO_AGE;
import static com.razchen.look4u.util.Keys.USER_ID;

//credit for https://www.youtube.com/watch?v=5Tm--PHhbJo&t=541s
public class FillOutQuestionnaires_ShowMatches extends UserMenu {
    private ListView listView;
    private ArrayList<String> mTitle = new ArrayList<>();
    private ArrayList<String> advertiserUserIdArratList = new ArrayList<>();
    private ArrayList<String> mDescription = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private String currentCategoryPath;
    private static final String QUESTIONNAIRES_PATH = "/" + Firebase.QUESTIONNAIRES_TABLE_NAME + "/";
    private String seekerUserId = "";
    private String advertiserUserId = "";
    private ArrayList<String> categoryChoicesArr = new ArrayList<>();
    private int fromAge = 0;
    private int toAge = 0;
    private String seekerFavorGender = "";
    private String advertiserFavorGender = "";
    private Button back_Button;


    private int countCurrentQuesionaire = 0;
    private int countfinalQuesionaire = 0;
    private long numOfDataSnapshotChildren = 0;
    private TextView userMessageTextView;
    private TextView pathForUserTextView;
    private Button changeCategoryButton;
    private Button changeFilterDetailsButton;

    private String pathForUser;
    private SpinKitView spinKit;
    private Service myService;
    private ObjectMapper mapper = new ObjectMapper();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_out_questionnaires__show_matches);
        config();

        myService = new Service();
        spinKit = findViewById(R.id.spinKit);

        userMessageTextView = findViewById(R.id.userMessage);
        pathForUserTextView = findViewById(R.id.pathForUserTextView);
        changeCategoryButton = findViewById(R.id.changeCategoryButton);
        changeFilterDetailsButton = findViewById(R.id.changeFilterDetailsButton);
        back_Button = findViewById(R.id.back_Button);
        back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Bundle iBundle = getIntent().getExtras();
        seekerUserId = iBundle.getString(USER_ID);
        pathForUser = iBundle.getString(Keys.PATH_FOR_USER);
        categoryChoicesArr = iBundle.getStringArrayList(Keys.CATEGORY_CHOICES_ARR);
        fromAge = iBundle.getInt(FROM_AGE);
        toAge = iBundle.getInt(TO_AGE);
        seekerFavorGender = iBundle.getString(Keys.GENDER);
        assignCategoryPath();
        findMatchesOnserverSide(currentCategoryPath);
        listView = findViewById(R.id.showQuestionnairesListView);
        if (pathForUser != null)
            pathForUserTextView.setText(pathForUser);


        spinKit = findViewById(R.id.spinKit);

    }

    private void assignCategoryPath() {


        currentCategoryPath = categoryChoicesArr.toString();
        currentCategoryPath = currentCategoryPath.substring(1, currentCategoryPath.length() - 1).trim();
        currentCategoryPath = currentCategoryPath.replace(", ", "/" + Firebase.CATEGORIES_TABLE_NAME + "/");
    }

    private void findMatchesOnserverSide(final String categoryPath) {


        myService.findMatches(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("ShowMatches", "onResponse: onFailure");
                findMatchesOnClientSide(categoryPath);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    try {
                        userBoundary[] users = mapper.readValue(responseStr, userBoundary[].class);
                        if (users.length == 0)
                            notFoundResults();
                        else {
                            for (int i = 0; i < users.length; i++) {
                                mTitle.add(users[i].getDescription());
                                images.add(users[i].getImage());
                                mDescription.add(users[i].getFullName());
                                advertiserUserIdArratList.add(users[i].getUserId());
                            }
                            Log.d("TAG", "onResponse: rraa  " + users.toString());
                            showResults();
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    Log.d("ShowMatches", "onResponse: " + responseStr);
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                    Log.d("ShowMatches", "onResponse: server not successful running in client side");
                    findMatchesOnClientSide(categoryPath);
                }
            }
        }, categoryPath, seekerFavorGender, seekerUserId, fromAge, toAge, categoryChoicesArr.toString());


    }


    private void findMatchesOnClientSide(final String categoryPath) {
        DatabaseReference categoriesDBRef = new Firebase().getCategoriesTable().child(categoryPath);

        categoriesDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(QUESTIONNAIRES_PATH)) {
                    notFoundResults();
                    return;
                }
                dataSnapshot = dataSnapshot.child(QUESTIONNAIRES_PATH);
                numOfDataSnapshotChildren = dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    DatabaseReference questionnairesDBRef = new Firebase().getQuestionnairesTable().child(snapshot.getKey());
                    questionnairesDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            advertiserUserId = dataSnapshot.child(Firebase.CREATOR_OF_THE_QUESTIONNAIRE).getValue().toString();
                            if (!advertiserUserId.equals(seekerUserId))
                                checkIfuseralreadyAnswered(advertiserUserId);
                            else
                                updateCountCurrentQuesionaire();
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

    private void checkIfuseralreadyAnswered(final String advertiserUserId) {

        DatabaseReference userDBRef = new Firebase().getUsersTable().child(advertiserUserId).child(Firebase.CANDIDATES);
        for (int i = 0; i < categoryChoicesArr.size(); i++) {
            userDBRef = userDBRef.child(categoryChoicesArr.get(i));
        }
        userDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Firebase.SEPARATPOR + seekerUserId)) {
                    updateCountCurrentQuesionaire();

                } else {
                    checkThePrerequisite(advertiserUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private boolean checkThePrerequisite(final String advertiserUserId) {
        DatabaseReference userDBRef = new Firebase().getUsersTable().child(advertiserUserId).child(Firebase.QUESTIONNAIRES_TABLE_NAME);
        for (int i = 0; i < categoryChoicesArr.size(); i++) {
            userDBRef = userDBRef.child(categoryChoicesArr.get(i));
        }
        userDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {




                /*old version*/
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    final DatabaseReference databaseReferenceToQuestionnairesTable = new Firebase().getQuestionnairesTable().child(snapshot.getKey());
                    databaseReferenceToQuestionnairesTable.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int ageFrom = Integer.parseInt(snapshot.child(Firebase.AGE_FROM).getValue().toString());
                            int ageTo = Integer.parseInt(snapshot.child(Firebase.AGE_TO).getValue().toString());
                            advertiserFavorGender = snapshot.child(Firebase.GENDER).getValue().toString();
                            checkISeekerAgeIsOkForAdvertiser(ageFrom, ageTo, seekerUserId, advertiserUserId);

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

        return true;
    }

    private void checkISeekerAgeIsOkForAdvertiser(final int ageFrom, final int ageTo, String seekerUserId, final String advertiserUserId) {
        DatabaseReference userDBRefForAge = new Firebase().getUsersTable().child(seekerUserId).child(Firebase.BIRTHDAY);
        userDBRefForAge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                MyDate seekerDate = dataSnapshot.getValue(MyDate.class);
                if (seekerDate != null) {
                    int seekerAge = Functions.getAge(seekerDate.getYear(), seekerDate.getMonth(), seekerDate.getDay());
                    if (seekerAge >= ageFrom && seekerAge <= ageTo) {
                        checkIfAdvertiserUserAgeIsOkForSeeker(advertiserUserId);
                    } else
                        updateCountCurrentQuesionaire();


                } else
                    updateCountCurrentQuesionaire();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkIfAdvertiserUserAgeIsOkForSeeker(final String advertiserUserId) {
        DatabaseReference userDBRefForAge = new Firebase().getUsersTable().child(advertiserUserId).child(Firebase.BIRTHDAY);
        userDBRefForAge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyDate advertiserDate = dataSnapshot.getValue(MyDate.class);
                if (advertiserDate != null) {
                    int advertiserAge = Functions.getAge(advertiserDate.getYear(), advertiserDate.getMonth(), advertiserDate.getDay());
                    if (advertiserAge >= fromAge && advertiserAge <= toAge) {
                        checkIfGenderIsOkForBoth(advertiserUserId, advertiserAge);

                    } else
                        updateCountCurrentQuesionaire();


                } else updateCountCurrentQuesionaire();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkIfGenderIsOkForBoth(final String advertiserUserId, final int advertiserAge) {
        DatabaseReference userDBRef = new Firebase().getUsersTable();
        userDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String advertiserGender = dataSnapshot.child(advertiserUserId).child(Firebase.GENDER).getValue().toString();
                if (!advertiserGender.equals(seekerFavorGender) && !seekerFavorGender.equals(EnglishStrings.not_relevant)) {
                    updateCountCurrentQuesionaire();
                    return;
                }

                String seekerGender = dataSnapshot.child(seekerUserId).child(Firebase.GENDER).getValue().toString();
                if (!advertiserFavorGender.equals(seekerGender) && !advertiserFavorGender.equals(EnglishStrings.not_relevant)) {
                    updateCountCurrentQuesionaire();
                    return;
                }
                String advertiserFullName = dataSnapshot.child(advertiserUserId).child(Firebase.FIRST_NAME).getValue().toString() + " " +
                        dataSnapshot.child(advertiserUserId).child(Firebase.LAST_NAME).getValue().toString();
                ;
                String advertiserImage = dataSnapshot.child(advertiserUserId).child(Firebase.IMAGE).getValue().toString();
                mTitle.add(advertiserFullName);
                images.add(advertiserImage);
                mDescription.add(advertiserAge + " " + UtilFunctions.convertGenderChoiceToHebrew(advertiserGender));
                advertiserUserIdArratList.add(advertiserUserId);
                countfinalQuesionaire++;
                updateCountCurrentQuesionaire();
                if (countCurrentQuesionaire == numOfDataSnapshotChildren && numOfDataSnapshotChildren != 0) {
                    showResults();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showResults() {

        runOnUiThread(new Runnable() {
            public void run() {
                // Update UI elements
                MyAdapter adapter = new MyAdapter(FillOutQuestionnaires_ShowMatches.this, mTitle, mDescription, images, categoryChoicesArr);
                listView.setAdapter(adapter);
                setNumOfResultsTextView();
                spinKit.setVisibility(View.GONE);
            }
        });
    }

    private void setNumOfResultsTextView() {
        TextView numOfResultsTextView = findViewById(R.id.numOfResultsTextView);
        String text;
        if (mTitle.size() == 1)
            text = UserMessages.One_result_found;
        else {
            text = UserMessages.Found;
            text += " ";
            text += mTitle.size();
            text += " ";
            text += UserMessages.Results;
        }
        numOfResultsTextView.setText(text);

    }

    private void updateCountCurrentQuesionaire() {
        countCurrentQuesionaire++;
        if (countCurrentQuesionaire == numOfDataSnapshotChildren)
            if (countfinalQuesionaire == 0) {
                notFoundResults();

            }
    }

    private void notFoundResults() {


        runOnUiThread(new Runnable() {
            public void run() {
                // Update UI elements
                listView.setVisibility(View.GONE);
                spinKit.setVisibility(View.GONE);

                userMessageTextView.setText(UserMessages.No_results_found_click_on_the_button_below_to_select_another_category);
                userMessageTextView.setVisibility(View.VISIBLE);
                changeCategoryButton.setVisibility(View.VISIBLE);
                changeFilterDetailsButton.setVisibility(View.VISIBLE);
                changeFilterDetailsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();

                    }
                });
                changeCategoryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fill_Out_Questionnaires();
                        finish();
                    }
                });
            }
        });


    }


    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        private ArrayList<String> rTitle;
        private ArrayList<String> rDescription;
        private ArrayList<String> rImgs;
        private ArrayList<String> categoryChoicesArr;

        MyAdapter(Context c, ArrayList<String> title, ArrayList<String> description, ArrayList<String> imgs, ArrayList<String> categoryChoicesArr) {
            super(c, R.layout.matches_row, R.id.matches_Row_textView1, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs = imgs;
            this.categoryChoicesArr = categoryChoicesArr;

        }

        @NonNull
        @Override
        public View getView(final int position, @androidx.annotation.Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.matches_row, parent, false);
            Button fillQuestionnaireButtonn = row.findViewById(R.id.fillQuestionnaireButtonn);


            fillQuestionnaireButtonn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(FillOutQuestionnaires_ShowMatches.this, FillOutQuestionnaires_FillingOutAQuestionnaireActivity.class);
                    Bundle inBundle = getIntent().getExtras();

                    intent.putExtra(USER_ID, inBundle.get(USER_ID).toString());
                    intent.putStringArrayListExtra(QUESTIONNAIRE_CATEGORY, categoryChoicesArr);
                    intent.putExtra(CATEGORY_USER_ID, advertiserUserIdArratList.get(position));

                    startActivity(intent);


                }
            });
            ImageView images = row.findViewById(R.id.matches_imageView);
            TextView myTitle = row.findViewById(R.id.matches_Row_textView1);
            TextView myDescription = row.findViewById(R.id.matches_Row_textView2);

            Picasso.with(FillOutQuestionnaires_ShowMatches.this).load(rImgs.get(position)).into(images);
            myTitle.setText(rTitle.get(position));
            myDescription.setText(rDescription.get(position));


            return row;
        }


    }
}
