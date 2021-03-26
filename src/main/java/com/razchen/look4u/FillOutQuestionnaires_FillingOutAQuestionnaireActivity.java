package com.razchen.look4u;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.java_classes.Notification;
import com.razchen.look4u.java_classes.Question;
import com.razchen.look4u.java_classes.Questionnaire;
import com.razchen.look4u.java_classes.User;
import com.razchen.look4u.util.Keys;
import com.razchen.look4u.util.UserMessages;
import com.razchen.look4u.util.UtilFunctions;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;

import static com.razchen.look4u.util.Keys.CATEGORY_USER_ID;
import static com.razchen.look4u.util.Keys.QUESTIONNAIRE_CATEGORY;
import static com.razchen.look4u.util.Keys.USER_ID;

public class FillOutQuestionnaires_FillingOutAQuestionnaireActivity extends UserMenu {

    //DB
    Firebase firebase;
    DatabaseReference databaseReference;

    //Previos Activity
    private String userID;
    private String categoryUserID;
    private RadioGroup radioGroup;
    private TextView theQuestion;
    private TextView numOfQuestionTextView;
    private Button previousQuestionButton;
    private Button nextQuestionButton;
    private Button sendCandidacy;
    private ArrayList<String> categories;
    private Questionnaire questionnaire;
    private int indexCurrentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_out_questionnaires__filling_out_a_questionnaire);


        previousQuestionButton = findViewById(R.id.previousQuestionButton);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        sendCandidacy = findViewById(R.id.sendCandidacy);
        radioGroup = findViewById(R.id.radioGroup);
        theQuestion = findViewById(R.id.theQuestion);
        numOfQuestionTextView = findViewById(R.id.numOfQuestionTextView);
        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (indexCurrentQuestion < questionnaire.getNumOfQuestions() - 1) {
                    indexCurrentQuestion++;
                    showQuestion();
                    updateNextAndPreviousButtons();

                }


            }
        });


        sendCandidacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < questionnaire.getNumOfQuestions(); i++) {
                    if (questionnaire.getQuestions().get(i + "").getSelectedAnswer() == null) {
                        indexCurrentQuestion = i;
                        updateNextAndPreviousButtons();
                        showQuestion();
                        return;
                    }
                }
                UtilFunctions.showUpperToast(UserMessages.Application_submitted_successfully, Toast.LENGTH_LONG);
                save_questionnaire_to_user_data_base();
                saveQuestionsToAnsweredQuestionsTable();


                firebase.getUsersTable().child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String categoryString = categories.toString();
                        categoryString = categoryString.substring(1, categoryString.length() - 1);
                        categoryString = categoryString.replace(",", "->");
                        User user = dataSnapshot.getValue(User.class);
                        String message = UserMessages.NEW_MATCH_IN_CATEGORY + " " + categoryString + " " + UserMessages.WITH + " " + user.getFirstName() + " " + user.getLastName();
                        Notification notification = new Notification(categoryUserID, message);
                        notification.sendNotification();
                        help();

                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                View radioButton = radioGroup.findViewById(radioButtonID);

                int selectedAnswer = radioGroup.indexOfChild(radioButton);

                questionnaire.getQuestions().get(indexCurrentQuestion + "").setSelectedAnswer(selectedAnswer + "");
                setSendCandidacyText();
            }
        });


        previousQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (indexCurrentQuestion != 0) {
                    indexCurrentQuestion--;
                    showQuestion();
                }
                updateNextAndPreviousButtons();


            }
        });

        getDataFromPreviousActivity();


        questionnaire = new Questionnaire();

        firebase = new Firebase();

        readDataFromDataBase();

        indexCurrentQuestion = 0;


    }


    private void updateNextAndPreviousButtons() {

        if (indexCurrentQuestion != 0) {
            previousQuestionButton.setEnabled(true);
            previousQuestionButton.setBackgroundColor(Color.parseColor(Keys.ORANGE_HEX));
        } else {
            previousQuestionButton.setEnabled(false);
            previousQuestionButton.setBackgroundColor(Color.parseColor(Keys.GREY_HEX));
        }


        if (indexCurrentQuestion == questionnaire.getQuestions().size() - 1) {
            nextQuestionButton.setEnabled(false);
            nextQuestionButton.setBackgroundColor(Color.parseColor(Keys.GREY_HEX));
        } else {
            nextQuestionButton.setEnabled(true);
            nextQuestionButton.setBackgroundColor(Color.parseColor(Keys.ORANGE_HEX));
        }

    }

    private void setSendCandidacyText() {
        if (checkAllQuestionsAnswered()) {
            sendCandidacy.setText(R.string.Send_candidacy);
        } else {
            sendCandidacy.setText(R.string.Go_to_the_next_unanswered_question);

        }
    }

    private boolean checkAllQuestionsAnswered() {
        int number = 0;
        while (number < questionnaire.getQuestions().size()) {
            Question question = questionnaire.getQuestions().get("" + number);

            if (question.getSelectedAnswer() == null)
                return false;
            number++;
        }
        return true;
    }

    private void loadAnswersFromDB() {
        databaseReference = firebase.getAnsweredQustionsTable().child(userID);

        int number = 0;
        while (number < categories.size()) {
            databaseReference = databaseReference.child(categories.get(number));
            number++;
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int number = 0;
                while (number < questionnaire.getQuestions().size()) {
                    Question question = questionnaire.getQuestions().get("" + number);

                    if (dataSnapshot.hasChild(question.getIdDB())) {
                        question.setSelectedAnswer(dataSnapshot.child(question.getIdDB()).getValue().toString());

                    }
                    number++;
                }

                setSendCandidacyText();
                sendCandidacy.setVisibility(View.VISIBLE);
                showQuestion();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void saveQuestionsToAnsweredQuestionsTable() {
        databaseReference = firebase.getAnsweredQustionsTable().child(userID);
        int i = 0;
        while (i < categories.size()) {
            databaseReference = databaseReference.child(categories.get(i++));
        }
        for (i = 0; i < questionnaire.getQuestions().size(); i++) {
            Question question = questionnaire.getQuestions().get("" + i);
            databaseReference.child(question.getIdDB()).setValue(question.getSelectedAnswer());
        }


    }

    private void readDataFromDataBase() {
        databaseReference = firebase.getUsersTable().child(categoryUserID).child(firebase.QUESTIONNAIRES_TABLE_NAME);
        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        int i = 0;
                        while (i < categories.size()) {
                            dataSnapshot = dataSnapshot.child(categories.get(i++));
                        }

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            final DatabaseReference databaseReferenceToQuestionnairesTable = firebase.getQuestionnairesTable().child(postSnapshot.getKey());
                            databaseReferenceToQuestionnairesTable.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot questionnaireDataSnapshot) {
                                    questionnaireDataSnapshot = questionnaireDataSnapshot.child(Firebase.QUESTIONS);
                                    for (DataSnapshot question : questionnaireDataSnapshot.getChildren()) {
                                        final Question questionObj = new Question();
                                        questionObj.setQuestion(question.child(Firebase.QUESTION).getValue(String.class));
                                        questionObj.setIdDB(question.child(Firebase.ID_DB).getValue(String.class));

                                        question = question.child(Firebase.ANSWERS);
                                        for (DataSnapshot answer : question.getChildren()) {
                                            questionObj.addAnswer(answer.getValue(String.class));

                                        }
                                        questionnaire.addQuestion(questionObj);

                                    }
                                    loadAnswersFromDB();
                                    updateNextAndPreviousButtons();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }


    private void showQuestion() {
        Map<String, Question> mapQuestions = questionnaire.getQuestions();
        Question question = mapQuestions.get(indexCurrentQuestion + "");


        radioGroup.removeAllViews();
        numOfQuestionTextView.setText(UserMessages.QUESTION + (indexCurrentQuestion + 1) + UserMessages.OF + questionnaire.getNumOfQuestions());
        numOfQuestionTextView.setVisibility(View.VISIBLE);
        theQuestion.setText(question.getQuestion());
        for (int i = 0; i < question.getNumOfAnswers(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(question.getAnswers().get(i + ""));
            radioGroup.addView(radioButton);
        }
        if (question.getSelectedAnswer() != null) {
            ((RadioButton) radioGroup.getChildAt(Integer.parseInt(question.getSelectedAnswer()))).setChecked(true);
//Credit https://stackoverflow.com/questions/8289033/android-ui-shows-radiobutton-as-selected-although-it-is-not
            ((RadioButton) radioGroup.getChildAt(Integer.parseInt(question.getSelectedAnswer()))).post(
                    new Runnable() {
                        @Override
                        public void run() {

                            Map<String, Question> mapQuestions = questionnaire.getQuestions();
                            Question question = mapQuestions.get(indexCurrentQuestion + "");
                            ((RadioButton) radioGroup.getChildAt(Integer.parseInt(question.getSelectedAnswer()))).setChecked(true);
                        }
                    }
            );


        }
    }

    private void getDataFromPreviousActivity() {


        Bundle inBundle = getIntent().getExtras();
        userID = inBundle.get(USER_ID).toString();
        categoryUserID = inBundle.get(CATEGORY_USER_ID).toString();
        categories = inBundle.getStringArrayList(QUESTIONNAIRE_CATEGORY);

    }

    private void save_questionnaire_to_user_data_base() {
        databaseReference = firebase.getUsersTable().child(categoryUserID).child(Firebase.CANDIDATES);
        for (int i = 0; i < categories.size(); i++) {

            databaseReference = databaseReference.child(categories.get(i));
        }
        databaseReference.child(Firebase.SEPARATPOR + userID).setValue(questionnaire);


    }

}
