package com.razchen.look4u;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kimkevin.cachepot.CachePot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.java_classes.Question;
import com.razchen.look4u.java_classes.Questionnaire;
import com.razchen.look4u.server.Boundaries.QuestionBoundary;
import com.razchen.look4u.server.Service;
import com.razchen.look4u.util.UserMessages;
import com.razchen.look4u.util.UtilFunctions;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static com.razchen.look4u.util.Keys.CATEGORIES_STACK;
import static com.razchen.look4u.util.Keys.CATEGORY_LIST;
import static com.razchen.look4u.util.Keys.CATEGORY_PATH_FOR_USER;
import static com.razchen.look4u.util.Keys.DATABASE_REFERENCE;
import static com.razchen.look4u.util.Keys.FROM_AGE;
import static com.razchen.look4u.util.Keys.GENDER;
import static com.razchen.look4u.util.Keys.NAME_OF_LAST_CATEGORY;
import static com.razchen.look4u.util.Keys.OK;
import static com.razchen.look4u.util.Keys.TO_AGE;
import static com.razchen.look4u.util.Keys.USER_ID;


/**
 * A simple {@link Fragment} subclass.
 */
public class Create_questions_fragment extends Fragment {

    private Button add_new_questions_Button;
    private Button select_questions_Button;
    private Stack<String> categoriesStack;
    private String nameOfLastCategory;
    private TextView pathTextView;
    private int ageFrom;
    private int ageTo;
    private String gender;
    private Button back_Button;
    private View rootView;
    private ViewPager viewPager;
    private Firebase firebase;
    private String userID;
    private RelativeLayout select_questions_RelativeLayout;
    private RelativeLayout add_new_questions_RelativeLayout;
    private RecyclerView fromDB_questions_recyclerViewAtBottom;
    private RecyclerView newQuestions_recylerView;
    private RecyclerView questions_recylerViewAtTop;
    private SimilarQuestionRowAdapter_fragment similarQuestionRowAdapter;
    private RecyclerView similarQuestionRecylerView;
    private QuestionRowAdapter_UpperQuestions_forFragment DB_questionRowAdapter_Questions_forFragmentAtTop;
    private New_questions_adapter new_questions_adapterAtBottom;
    private DB_questions_adapter DB_questions_adapterAtBottom;
    private AlertDialog.Builder similarQuestionsAlertDialog;
    private ArrayList<Question> questions_from_similar_questions;
    private Questionnaire questionnaire;
    private EditText answerEditText;
    private EditText questionEditText;
    private DatabaseReference databaseReference;
    private Map<String, String> ids_of_selected_questions;
    private ArrayList<QuestionBoundary> newQuestionsFromUserListAtBottom;
    private ArrayList<QuestionBoundary> DBQuestionListAtBottom;
    private List<QuestionBoundary> questionBoundaryListAtTop;
    private ToggleButtonGroupTableLayout toggleButtonGroupTableLayout;

    public Create_questions_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_create_questions, container, false);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);
        String value = CachePot.getInstance().pop(3);
        if (value == null) {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(1);
                }
            });
            return rootView;
        }
        onCreateTasks();
        return rootView;
    }

    private void onCreateTasks() {
        readDatafromPreviousFragment();
        initializeMenuButtons();
        pathTextView = rootView.findViewById(R.id.pathTextView);
        firebase = new Firebase();
        select_questions_RelativeLayout = rootView.findViewById(R.id.select_questions_RelativeLayout);
        add_new_questions_RelativeLayout = rootView.findViewById(R.id.add_new_questions_RelativeLayout);
        //choose questions
        questions_recylerViewAtTop = rootView.findViewById(R.id.questions_recyclerView);
        //https://stackoverflow.com/questions/36724898/notifyitemchanged-make-the-recyclerview-scroll-and-jump-to-up
        questions_recylerViewAtTop.setHasFixedSize(true);
        newQuestions_recylerView = rootView.findViewById(R.id.new_questions_recyclerView);
        fromDB_questions_recyclerViewAtBottom = rootView.findViewById(R.id.fromDB_questions_recyclerView);
        questions_from_similar_questions = new ArrayList<>();
        ids_of_selected_questions = new HashMap<String, String>();
        Bundle inBundle = getActivity().getIntent().getExtras();
        userID = inBundle.get(USER_ID).toString();
        firebase = new Firebase();
        databaseReference = firebase.getCategoriesTable();
        questionBoundaryListAtTop = new ArrayList<>();
        choose_questions_from_database(questionBoundaryListAtTop);
        showNewQuestionsFromUser();
        showQuestionsFromDBAtTheBottom();
        set_back_button();
        handel_back_press();
        start_immediately_create_Questionnaires();
        final PopUpInfo popUpInfo = new PopUpInfo();


        rootView.findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpInfo.showPopupWindow(v, getString(R.string.help), getString(R.string.help_add_question));
                closeKeyBoard(v);
            }
        });

        rootView.findViewById(R.id.help_yourQuestion_ImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpInfo.showPopupWindow(v, getString(R.string.help), UserMessages.help_edit_delete_your_questions);
                closeKeyBoard(v);

            }
        });
        toggleButtonGroupTableLayout = rootView.findViewById(R.id.toggleButtonGroupTableLayout);
    }

    private void showNewQuestionsFromUser() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        newQuestions_recylerView.setLayoutManager(layoutManager);
        newQuestionsFromUserListAtBottom = new ArrayList<>();
        new_questions_adapterAtBottom = new New_questions_adapter(this, newQuestionsFromUserListAtBottom, rootView.getContext());
        newQuestions_recylerView.setAdapter(new_questions_adapterAtBottom);
    }

    private void showQuestionsFromDBAtTheBottom() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fromDB_questions_recyclerViewAtBottom.setLayoutManager(layoutManager);
        DBQuestionListAtBottom = new ArrayList<>();
        DB_questions_adapterAtBottom = new DB_questions_adapter(this, DBQuestionListAtBottom, rootView.getContext());
        fromDB_questions_recyclerViewAtBottom.setAdapter(DB_questions_adapterAtBottom);
    }

    private void closeKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) rootView.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    //Credit https://stackoverflow.com/questions/7992216/android-fragment-handle-back-button-press
    private void handel_back_press() {
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        viewPager.setCurrentItem(1);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void initializeMenuButtons() {
        select_questions_Button = rootView.findViewById(R.id.select_questions_Button);
        add_new_questions_Button = rootView.findViewById(R.id.add_new_questions_Button);


        select_questions_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionBoundaryListAtTop.size() == 0) {
                    UtilFunctions.showUpperToast(UserMessages.No_questions_have_been_added_to_the_repository, Toast.LENGTH_LONG);
                    return;
                }
                showOrHideRelativeLayout(select_questions_RelativeLayout, view, select_questions_Button, add_new_questions_Button);
                hideRelativeLayout(add_new_questions_RelativeLayout);

            }
        });


        add_new_questions_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrHideRelativeLayout(add_new_questions_RelativeLayout, view, add_new_questions_Button, select_questions_Button);
                hideRelativeLayout(select_questions_RelativeLayout);
            }
        });

    }

    public void showOrHideRelativeLayout(RelativeLayout relativeLayout, View v, Button button1, Button button2) {
        closeKeyBoard(v);
        if ((relativeLayout.getVisibility() == View.GONE) || (relativeLayout.getHeight() == 0)) {
            relativeLayout.setVisibility(View.VISIBLE);
            relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Drawable background = button1.getBackground();
            background.setTint(ContextCompat.getColor(rootView.getContext(), R.color.orange));
            button1.setBackgroundDrawable(background);
            background = button2.getBackground();
            background.setTint(ContextCompat.getColor(rootView.getContext(), R.color.gray));
            button2.setBackgroundDrawable(background);
        } else {
            relativeLayout.setVisibility(View.GONE);
            Drawable background = button1.getBackground();
            background.setTint(ContextCompat.getColor(rootView.getContext(), R.color.gray));
            button1.setBackgroundDrawable(background);
        }


    }

    public void hideRelativeLayout(RelativeLayout relativeLayout) {
        relativeLayout.setVisibility(View.GONE);

    }

    public void sendGetRequest() {
        String value = CachePot.getInstance().pop(3);
        if (value == null) {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(1);
                }
            });
            return;
        }
        if (value.equals(OK)) ;
        {
            CachePot.getInstance().push(3, OK);

        }
        onCreateTasks();
    }

    public void readDatafromPreviousFragment() {
        categoriesStack = CachePot.getInstance().pop(CATEGORIES_STACK);
        nameOfLastCategory = CachePot.getInstance().pop(NAME_OF_LAST_CATEGORY);
        ageFrom = CachePot.getInstance().pop(FROM_AGE);
        ageTo = CachePot.getInstance().pop(TO_AGE);
        gender = CachePot.getInstance().pop(GENDER);
        readPathText();

        saveDataToCachePot();
    }

    private void saveDataToCachePot() {
        CachePot.getInstance().push(FROM_AGE, ageFrom);
        CachePot.getInstance().push(TO_AGE, ageTo);
        CachePot.getInstance().push(GENDER, gender);
        CachePot.getInstance().push(NAME_OF_LAST_CATEGORY, nameOfLastCategory);
        CachePot.getInstance().push(CATEGORIES_STACK, categoriesStack);
    }

    private void askIfWantTo_advertising_Questionnaire() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        createQuestionnaire();
                        saveQuestionnaireToDataBase();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        builder.setMessage(R.string.Are_you_sure_you_want_to_publish_the_questionnaire).setPositiveButton(R.string.Yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    private void createQuestionnaire() {
        questionnaire = new Questionnaire(userID, categoriesStack.toString().substring(1, categoriesStack.toString().length() - 1), ageFrom, ageTo, gender);
        for (int i = 0; i < DBQuestionListAtBottom.size(); i++) {
            questionnaire.addQuestion(DBQuestionListAtBottom.get(i).toEntityQuestion());
        }

        for (int i = 0; i < newQuestionsFromUserListAtBottom.size(); i++) {
            questionnaire.addQuestion(newQuestionsFromUserListAtBottom.get(i).toEntityQuestion());

        }


    }

    private void saveQuestionnaireToDataBase() {
        Stack<String> tempStack = new Stack<>();
        while (!categoriesStack.isEmpty()) {
            tempStack.push(categoriesStack.pop());
        }
        while (!tempStack.isEmpty()) {
            categoriesStack.push(tempStack.pop());
            databaseReference = databaseReference.child(categoriesStack.peek().substring(1));
            databaseReference = databaseReference.child(firebase.CATEGORIES_TABLE_NAME);
        }

        databaseReference = databaseReference.child(nameOfLastCategory);
        databaseReference = databaseReference.child(Firebase.QUESTIONNAIRES_TABLE_NAME);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new Create_questions_fragment.MyTask().execute();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void update_New_questions_adapter() {
        new_questions_adapterAtBottom = new New_questions_adapter(this, newQuestionsFromUserListAtBottom, rootView.getContext());
        newQuestions_recylerView.setAdapter(new_questions_adapterAtBottom);

    }

    public void update_DB_questions_adapter() {
        DB_questions_adapterAtBottom = new DB_questions_adapter(this, DBQuestionListAtBottom, rootView.getContext());
        fromDB_questions_recyclerViewAtBottom.setAdapter(DB_questions_adapterAtBottom);
    }

    public void updateQuestionsFromDBAtTheBottom(int position) {
        QuestionBoundary questionBoundary = questionBoundaryListAtTop.get(position);
        for (int i = 0; i < DBQuestionListAtBottom.size(); i++) {
            if (questionBoundary.getIdDB().equals(DBQuestionListAtBottom.get(i).getIdDB())) {
                update_DB_questions_adapter();
                return;
            }
        }
        DBQuestionListAtBottom.add(questionBoundary);
        update_DB_questions_adapter();

    }

    public void deleteQuestionFromDBAtTheBottom(int position) {
        QuestionBoundary questionBoundary = questionBoundaryListAtTop.get(position);
        for (int i = 0; i < DBQuestionListAtBottom.size(); i++) {
            if (questionBoundary.getIdDB().equals(DBQuestionListAtBottom.get(i).getIdDB())) {
                DBQuestionListAtBottom.remove(i);
                update_DB_questions_adapter();
                return;
            }
        }

    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        public MyTask() {
            super();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String key = databaseReference.push().getKey();
                    //save questionnaire to data base
                    questionnaire.setId(key);
                    questionnaire.setCategory(categoriesStack.toString() + "," + nameOfLastCategory);
                    databaseReference.child(key).setValue(false);
                    firebase.getQuestionnairesTable().child(key).setValue(questionnaire);
                    save_questionnaire_to_user_data_base(key);
                    UtilFunctions.showUpperToast(UserMessages.Questionnaire_added_successfully, Toast.LENGTH_LONG);


                }
            });

            saveQuestionsToQuestionsTable();


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

    private void save_questionnaire_to_user_data_base(String key) {
        databaseReference = firebase.getUsersTable().child(userID).child(Firebase.QUESTIONNAIRES_TABLE_NAME);
        Stack<String> tempStack = new Stack<>();
        while (!categoriesStack.isEmpty()) {
            tempStack.push(categoriesStack.pop());
        }
        while (!tempStack.isEmpty()) {
            categoriesStack.push(tempStack.pop());
            databaseReference = databaseReference.child(categoriesStack.peek().substring(1));
        }
        databaseReference = databaseReference.child(nameOfLastCategory);
        databaseReference.child(key).setValue(false);

        ask_create_another_questionnaire();

    }

    private void saveQuestionsToQuestionsTable() {
        DatabaseReference questionsDatabaseReference = firebase.getQuestionsTable();
        Stack<String> tempStack = new Stack<>();
        while (!categoriesStack.isEmpty()) {
            tempStack.push(categoriesStack.pop());
        }
        while (!tempStack.isEmpty()) {
            categoriesStack.push(tempStack.pop());
            questionsDatabaseReference = questionsDatabaseReference.child(categoriesStack.peek().substring(1));
        }
        questionsDatabaseReference = questionsDatabaseReference.child(nameOfLastCategory);
        for (int i = 0; i < newQuestionsFromUserListAtBottom.size(); i++) {

            Question question = (Question) deepCopy(newQuestionsFromUserListAtBottom.get(i).toEntityQuestion());
            question.setSelectedAnswer(null);
            question.setEnglishQuestion(convertJSONtoString(translateToEnglish(question.getQuestion())));
            questionsDatabaseReference.child(question.getIdDB()).setValue(question);
        }
    }

    private void set_addQuestionButton() {
        Button addQuestionButton = rootView.findViewById(R.id.addQuestionButton);
        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (canAddQuestion()) {
                    if (addQuestionFromToggleButtonGroupTableLayout()) {
                        closeKeyBoard(view);
                        new_questions_adapterAtBottom.notifyDataSetChanged();
                        cleanToggleButtonGroupTableLayout();
                        cleanQuestionFieldAndAnswersFields();
                        cleanCheckBoxSelection();
                        UtilFunctions.showUpperToast(UserMessages.The_question_was_successfully_saved, Toast.LENGTH_SHORT);
                    }
                }

            }
        });

    }

    private void cleanQuestionFieldAndAnswersFields() {
        questionEditText.setText("");
        getEditTextByIndex(0).setText("");
        getEditTextByIndex(1).setText("");
    }

    private void cleanCheckBoxSelection() {
        getRadioButtonByIndex(0).setChecked(false);
        getRadioButtonByIndex(1).setChecked(false);

    }

    private RadioButton getRadioButtonByIndex(int index) {
        TableRow tableRow = (TableRow) toggleButtonGroupTableLayout.getChildAt(index);
        RadioButton radioButton = (RadioButton) tableRow.getChildAt(0);
        return radioButton;
    }

    private EditText getEditTextByIndex(int index) {
        TableRow tableRow = (TableRow) toggleButtonGroupTableLayout.getChildAt(index);
        EditText editText = (EditText) tableRow.getChildAt(1);
        return editText;
    }

    private void cleanToggleButtonGroupTableLayout() {
        int numOfAnswers = toggleButtonGroupTableLayout.getChildCount();
        if (numOfAnswers >= 2) {
            for (int i = 2; i < numOfAnswers; i++)
                toggleButtonGroupTableLayout.removeView(toggleButtonGroupTableLayout.getChildAt(2));
        }

    }

    private boolean canAddQuestion() {

        if (questionEditText.getText().toString().length() == 0) {
            UtilFunctions.showUpperToast(UserMessages.A_question_must_be_entered, Toast.LENGTH_SHORT);
            return false;
        }

        return true;

    }

    private Boolean addQuestionFromToggleButtonGroupTableLayout() {
        QuestionBoundary questionBoundary = new QuestionBoundary();
        String[] answers = new String[toggleButtonGroupTableLayout.getChildCount()];
        int selectedAnswer = -1;
        for (int i = 0; i < toggleButtonGroupTableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) toggleButtonGroupTableLayout.getChildAt(i);
            if (tableRow instanceof TableRow) {

                for (int j = 0; j < tableRow.getChildCount(); j++) {
                    if (j == 0) {
                        RadioButton radioButton = (RadioButton) tableRow.getChildAt(j);
                        if (radioButton instanceof RadioButton) {
                            if (radioButton.isChecked()) {
                                questionBoundary.setSelectedAnswer(i);
                                selectedAnswer = i;
                            }
                        }
                    }
                    if (j == 1) {
                        EditText editText = (EditText) tableRow.getChildAt(j);
                        if (editText instanceof EditText) {
                            String answer = editText.getText().toString().trim();
                            if (answer.equals("")) {
                                UtilFunctions.showUpperToast(UserMessages.all_answers_must_be_filled_in_or_blank_answers_deleted, Toast.LENGTH_SHORT);
                                return false;
                            }
                            answers[i] = answer;
                        }
                    }
                }
            }
        }
        questionBoundary.setAnswers(answers);
        questionBoundary.setIdDB(databaseReference.push().getKey());
        questionBoundary.setNumOfAnswers(answers.length);
        questionBoundary.setQuestion(questionEditText.getText().toString());
        if (selectedAnswer == -1) {
            UtilFunctions.showUpperToast(UserMessages.Please_select_answer, Toast.LENGTH_SHORT);
            return false;
        }
        newQuestionsFromUserListAtBottom.add(questionBoundary);
        return true;
    }

    public void clean_create_questionnaire_fields() {

        new_questions_adapterAtBottom.notifyDataSetChanged();
        cleanToggleButtonGroupTableLayout();
        cleanQuestionFieldAndAnswersFields();
        cleanCheckBoxSelection();
        UtilFunctions.showUpperToast(UserMessages.The_question_was_successfully_saved, Toast.LENGTH_SHORT);

    }

    private void ask_create_another_questionnaire() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        getActivity().finish();
                        start_activity(CreateQuestionnaire_main.class);
                        deleteCategorySelected();


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        getActivity().finish();
                        start_activity(MainActivity.class);


                        break;


                }
            }
        };


        final AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        builder.setMessage(R.string.Do_you_want_to_add_another_questionnaire).setPositiveButton(R.string.Yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                builder.show();
            }

        }).show();

    }

    public void deleteCategorySelected() {
        CachePot.getInstance().pop(CATEGORY_LIST);
        CachePot.getInstance().pop(NAME_OF_LAST_CATEGORY);
        CachePot.getInstance().pop(DATABASE_REFERENCE);
        CachePot.getInstance().pop(CATEGORIES_STACK);
        CachePot.getInstance().pop(CATEGORY_PATH_FOR_USER);
    }

    private void create_Publication_of_a_questionnaire_Button(final int startSaveQuestionsfrom) {
        Button next_button = rootView.findViewById(R.id.next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkIfCanAddQuestionnaireToDataBase())
                    return;
                askIfWantTo_advertising_Questionnaire();

            }
        });
    }

    private boolean checkIfCanAddQuestionnaireToDataBase() {
        if (newQuestionsFromUserListAtBottom.size() + DBQuestionListAtBottom.size() == 0) {
            UtilFunctions.showUpperToast(UserMessages.Please_enter_save_the_question, Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }

    private void showSimilarQuestionSuggestion(final QuestionBoundary[] questionsBoundary, View v) {

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                //-2 means that the question didn't selected
                //-1 means that the question selected but the answer not
                final int[] resultsFromSimilarQuestionDialog = new int[questionsBoundary.length];
                defineSimilarQuestionPopUp(Arrays.asList(questionsBoundary), resultsFromSimilarQuestionDialog);
                final AlertDialog dialog = similarQuestionsAlertDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean exit = false;
                        int counterTheQuestionThatDidntDelected = 0;
                        for (int i = 0; i < questionsBoundary.length; i++) {
                            if (resultsFromSimilarQuestionDialog[i] == -1) {
                                UtilFunctions.showUpperToast(UserMessages.select_a_answer_to_selected_answers, Toast.LENGTH_LONG);
                                exit = true;
                                break;
                            }
                            if (resultsFromSimilarQuestionDialog[i] == -2)
                                counterTheQuestionThatDidntDelected++;

                        }
                        if (exit == true)
                            return;
                        if (counterTheQuestionThatDidntDelected == questionsBoundary.length) {
                            UtilFunctions.showUpperToast(UserMessages.If_you_do_not_want_to_choose_the_question_click_Cancel, Toast.LENGTH_LONG);
                            return;
                        }
                        int count = 0;
                        for (int i = 0; i < questionsBoundary.length; i++) {

                            if (resultsFromSimilarQuestionDialog[i] != -2) {
                                Question questionToadd = questionsBoundary[i].toEntityQuestion();
                                questionToadd.setSelectedAnswer(resultsFromSimilarQuestionDialog[i] + "");
                                addQuestionToDBQuestionsList(questionToadd);
                                addQuestionToSimilarQuestions(questionToadd);
                                clean_create_questionnaire_fields();
                                add_id_to_ids_of_selected_questions(questionsBoundary[i].getIdDB());
                                count++;
                            }
                        }
                        if (count == 1)
                            UtilFunctions.showUpperToast(UserMessages.one_question_added_successfully_to_questionnaire, Toast.LENGTH_LONG);
                        else
                            UtilFunctions.showUpperToast(UserMessages.added + UserMessages.space + count + UserMessages.space + UserMessages.question_successfully_to_questionnaire, Toast.LENGTH_LONG);

                        dialog.dismiss();
                    }
                });


            }
        });

    }

    private void addQuestionToDBQuestionsList(Question questionToadd) {
        int pos = 0;
        for (int i = 0; i < questionBoundaryListAtTop.size(); i++) {
            if (questionBoundaryListAtTop.get(i).getIdDB().equals(questionToadd.getIdDB()))
                pos = i;
        }

        try {
            View view = questions_recylerViewAtTop.findViewHolderForAdapterPosition(pos).itemView;
            CheckBox checkBox = view.findViewById(R.id.checkBoxIDSimilarQuestion);
            checkBox.setChecked(true);


            int selectedAnswer = Integer.parseInt(questionToadd.getSelectedAnswer());
            RadioGroup radioGroup = view.findViewById(R.id.radioGroupIDSimilarQuestion);
            radioGroup.setVisibility(View.VISIBLE);
            ((RadioButton) radioGroup.getChildAt(selectedAnswer)).setChecked(true);
        } catch (Exception e) {

        } finally {
            questionBoundaryListAtTop.get(pos).setSelectedAnswer(Integer.parseInt(questionToadd.getSelectedAnswer()));

        }
    }

    private void start_activity(Class c) {
        Intent intent = new Intent(rootView.getContext(), c);
        Bundle inBundle = getActivity().getIntent().getExtras();

        intent.putExtra(USER_ID, inBundle.get(USER_ID).toString());

        startActivity(intent);
    }

    public void addQuestionToSimilarQuestions(Question question) {
        questions_from_similar_questions.add(question);
    }

    public void add_id_to_ids_of_selected_questions(String idDB) {
        ids_of_selected_questions.put(idDB, idDB);
    }

    public void remove_id_to_ids_of_selected_questions(String idDB) {
        ids_of_selected_questions.remove(idDB);
    }

    private void defineSimilarQuestionPopUp(List<QuestionBoundary> questionBoundaryList, final int resultsFromSimilarQuestionDialog[]) {
        similarQuestionsAlertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_similar_question_layout, null);

        similarQuestionRecylerView = dialogView.findViewById(R.id.similarQuestionRecyclerView);

        //-2 means that the question didn't selected
        //-1 means that the question selected but the answer not
        Arrays.fill(resultsFromSimilarQuestionDialog, -2);
        show_similar_question(questionBoundaryList, resultsFromSimilarQuestionDialog);

        similarQuestionsAlertDialog.setView(dialogView);


        similarQuestionsAlertDialog.setPositiveButton(UserMessages.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                //overwrite
            }
        });

        similarQuestionsAlertDialog.setNegativeButton(UserMessages.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });


        similarQuestionsAlertDialog.setCancelable(false);

    }

    private String mapToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();

        for (String key : map.keySet()) {
            sb.append(key);
            sb.append(",");
        }
        return sb.toString();
    }

    private void show_similar_question(List<QuestionBoundary> questionBoundaryList, int results[]) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        similarQuestionRecylerView.setLayoutManager(layoutManager);
        similarQuestionRowAdapter = new SimilarQuestionRowAdapter_fragment(this, questionBoundaryList, rootView.getContext(), results);
        similarQuestionRecylerView.setAdapter(similarQuestionRowAdapter);

    }

    private void choose_questions_from_database(List<QuestionBoundary> questionBoundaryList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        questions_recylerViewAtTop.setLayoutManager(layoutManager);
        DB_questionRowAdapter_Questions_forFragmentAtTop = new QuestionRowAdapter_UpperQuestions_forFragment(this, questionBoundaryList, rootView.getContext());
        questions_recylerViewAtTop.setAdapter(DB_questionRowAdapter_Questions_forFragmentAtTop);
    }

    public void show_Select_questions_RelativeLayout() {

        if (select_questions_RelativeLayout.getVisibility() == View.GONE) {
            select_questions_RelativeLayout.setVisibility(View.VISIBLE);
            add_new_questions_RelativeLayout.setVisibility(View.GONE);
            set_button_to_color(select_questions_Button, R.color.orange);
            set_button_to_color(add_new_questions_Button, R.color.gray);
        }
    }

    public void set_button_to_color(Button button, int color) {
        Drawable background = button.getBackground();
        background.setTint(ContextCompat.getColor(rootView.getContext(), color));
        button.setBackgroundDrawable(background);
    }

    public void removeQuestionFromUpperDB(String idDB) {
        int pos = 0;
        for (int i = 0; i < questionBoundaryListAtTop.size(); i++) {
            if (questionBoundaryListAtTop.get(i).getIdDB().equals(idDB))
                pos = i;
        }
        if (questions_recylerViewAtTop.findViewHolderForAdapterPosition(pos) != null) {
            View view = questions_recylerViewAtTop.findViewHolderForAdapterPosition(pos).itemView;
            CheckBox checkBox = view.findViewById(R.id.checkBoxIDSimilarQuestion);
            checkBox.setChecked(false);


            RadioGroup radioGroup = view.findViewById(R.id.radioGroupIDSimilarQuestion);
            radioGroup.setVisibility(View.GONE);
        } else
            questions_recylerViewAtTop.getAdapter().notifyItemChanged(pos);
        questionBoundaryListAtTop.get(pos).setSelectedAnswer(-1);
    }

    public void updateQuestionFromUpperDB(String idDB, int selectedAnswer) {


        int pos = 0;
        for (int i = 0; i < questionBoundaryListAtTop.size(); i++) {
            if (questionBoundaryListAtTop.get(i).getIdDB().equals(idDB))
                pos = i;
        }

        if (questions_recylerViewAtTop.findViewHolderForAdapterPosition(pos) != null) {
            View view = questions_recylerViewAtTop.findViewHolderForAdapterPosition(pos).itemView;
            RadioGroup radioGroup = view.findViewById(R.id.radioGroupIDSimilarQuestion);
            ((RadioButton) radioGroup.getChildAt(selectedAnswer)).setChecked(true);
        } else
            questions_recylerViewAtTop.getAdapter().notifyItemChanged(pos);

        questionBoundaryListAtTop.get(pos).setSelectedAnswer(selectedAnswer);

    }

    public Stack<String> getCategoriesStack() {
        return categoriesStack;
    }

    public String getNameOfLastCategory() {
        return nameOfLastCategory;
    }

    private void create_Questionnaires(int startSaveQuestionsfrom) {
        categoriesStack.push(nameOfLastCategory);
        categoriesStack.pop();
        set_questionEditText();
        set_answerEditText();
        set_addAnswerButton();
        set_addQuestionButton();
        create_Publication_of_a_questionnaire_Button(startSaveQuestionsfrom);
    }

    private void set_addAnswerButton() {
        Button addAnswerButton = rootView.findViewById(R.id.addAnswerButton);


        addAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canAddAnswer())
                    addAnswerToToggleButtonGroupTableLayout();
            }
        });
    }

    private boolean canAddAnswer() {
        for (int i = 0; i < toggleButtonGroupTableLayout.getChildCount(); i++) {
            if (getEditTextByIndex(i).getText().toString().trim().equals("")) {
                UtilFunctions.showUpperToast(UserMessages.fill_all_answers, Toast.LENGTH_LONG);
                return false;
            }
        }
        return true;
    }

    private void addAnswerToToggleButtonGroupTableLayout() {
        TableRow tableRow = new TableRow(rootView.getContext());
        RadioButton radioButton = new RadioButton(rootView.getContext());
        EditText editText = new EditText(rootView.getContext());
        editText.setHint(UserMessages.Type_an_answer);
        editText.requestFocus();
        final ImageView imageView = new ImageView(rootView.getContext());
        imageView.setImageResource(R.drawable.x);


        //Credit https://stackoverflow.com/questions/11963465/android-layoutparams-for-textview-makes-the-view-disappear-programmatically
        TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        TableRow.LayoutParams EditTextParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);

        imageView.setLayoutParams(params);
        editText.setLayoutParams(EditTextParams);
        editText.getLayoutParams().width = 300;
        imageView.getLayoutParams().width = 100;
        imageView.getLayoutParams().height = 100;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow tableRow = (TableRow) imageView.getParent();
                toggleButtonGroupTableLayout.removeView(tableRow);

            }
        });

        tableRow.addView(radioButton);
        tableRow.addView(editText);
        tableRow.addView(imageView);
        toggleButtonGroupTableLayout.addView(tableRow);
    }

    private void set_questionEditText() {

        questionEditText = rootView.findViewById(R.id.questionEditText);
        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void set_answerEditText() {
        answerEditText = new EditText(rootView.getContext());
        answerEditText = rootView.findViewById(R.id.answerEditText);
        answerEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                if (hasFocus) {

                    if (!questionEditText.getText().toString().trim().equalsIgnoreCase("")) {
                        Service myService = new Service();
                        myService.getSimilarQuestionFromDB(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {

                            }

                            @Override
                            public void onResponse(Response response) throws IOException {

                                if (response.isSuccessful()) {
                                    String responseStr = response.body().string();
                                    try {
                                        ObjectMapper mapper = new ObjectMapper();
                                        QuestionBoundary[] questions = mapper.readValue(responseStr, QuestionBoundary[].class);

                                        if (questions.length > 0) {
                                            showSimilarQuestionSuggestion(questions, v);
                                        }
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }
                                }


                            }
                        }, categoriesStack.toString() + "," + nameOfLastCategory, questionEditText.getText().toString(), mapToString(ids_of_selected_questions), userID);
                    }

                }
            }
        });

    }

    public void start_immediately_create_Questionnaires() {
        create_Questionnaires(0);
    }

    private void set_back_button() {
        back_Button = rootView.findViewById(R.id.back_Button);
        back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_questions_RelativeLayout.setVisibility(View.GONE);

                viewPager.setCurrentItem(1);
            }
        });
    }

    private void readPathText() {
        String categoryPathForUser = CachePot.getInstance().pop(CATEGORY_PATH_FOR_USER);
        pathTextView = rootView.findViewById(R.id.pathTextView);
        pathTextView.setText(categoryPathForUser);
        CachePot.getInstance().push(CATEGORY_PATH_FOR_USER, categoryPathForUser);

    }

    private String convertJSONtoString(String json) {
        try {

            JSONObject jsonObject = new JSONObject(json);
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = jsonObject.getJSONArray("translations");
            JSONObject jsonObject1 = (JSONObject) jsonArray.get(0);
            String translatedSTR = jsonObject1.getString("translatedText");
            Log.d("TAG", "convertJSONtoString: " + translatedSTR);
            return translatedSTR;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String translateToEnglish(String text) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://translation.googleapis.com/language/translate/v2/?source=he&q=" + text
                        + "&target=en&format=text&format=text&model=base&key={removed}")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object deepCopy(Object object) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
            outputStrm.writeObject(object);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
            return objInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}