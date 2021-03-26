package com.razchen.look4u;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.server.Boundaries.QuestionBoundary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
// credit for https://www.youtube.com/watch?v=caUfVkjlI7I
public class QuestionRowAdapter_UpperQuestions_forFragment extends RecyclerView.Adapter<QuestionRowAdapter_UpperQuestions_forFragment.Viewholder> {
    private List<QuestionBoundary> questionBoundaryList;
    private Firebase firebase;
    private Stack<String> stack = new Stack<>();
    private Context context;
    private DatabaseReference databaseReference;
    Create_questions_fragment create_questions_fragment;

    public QuestionRowAdapter_UpperQuestions_forFragment(Create_questions_fragment create_questions_fragment, List<QuestionBoundary> questionBoundaryList, Context context) {
        this.questionBoundaryList = new ArrayList<>();
        this.create_questions_fragment = create_questions_fragment;
        this.context = context;
        this.questionBoundaryList = questionBoundaryList;
        firebase = new Firebase();

        databaseReference = firebase.getQuestionsTable();
        setHasStableIds(true);
        readFromFirebase();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void readFromFirebase() {
        questionBoundaryList.clear();
        Stack<String> stack = create_questions_fragment.getCategoriesStack();
        String s;
        String lastcategoryName = create_questions_fragment.getNameOfLastCategory();

        if (!stack.isEmpty()) {
            s = stack.toString();
            s = s.substring(2, s.length() - 1);
            s = s.replace(", >", "/");
            s = s + "/" + lastcategoryName;
        } else {
            s = lastcategoryName;
        }


        databaseReference.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {


                    QuestionBoundary q = new QuestionBoundary();
                    q.setQuestion(postSnapshot.child(Firebase.QUESTION).getValue().toString());
                    int numOfAnswers = postSnapshot.child(Firebase.NUM_OF_ANSWERS).getValue(int.class);
                    q.setIdDB(postSnapshot.getKey());
                    String[] answers = new String[numOfAnswers];
                    for (int i = 0; i < numOfAnswers; i++) {
                        answers[i] = postSnapshot.child(Firebase.ANSWERS).child(i + "").getValue().toString();
                    }
                    q.setAnswers(answers);
                    q.setNumOfAnswers(answers.length);
                    q.setSelectedAnswer(-1);
                    questionBoundaryList.add(q);
                }
                if(questionBoundaryList.size()!=0)
                {
                    sortQuestions();
                    create_questions_fragment.show_Select_questions_RelativeLayout();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sortQuestions() {
        Collections.sort(questionBoundaryList, new Comparator<QuestionBoundary>() {
            @Override
            public int compare(QuestionBoundary o1, QuestionBoundary o2) {
                return o1.compareTo(o2);                        }
        });
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_question_row, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {
        holder.setData(questionBoundaryList.get(position), context,position);
    }


    public Stack<String> getStack() {
        return stack;
    }


    @Override
    public int getItemCount() {
        return questionBoundaryList.size();
    }



    class Viewholder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private RadioGroup radioGroup;
        public Viewholder(@NonNull final View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBoxIDSimilarQuestion);

            radioGroup = itemView.findViewById(R.id.radioGroupIDSimilarQuestion);

        }

        private void setData(QuestionBoundary question, Context context, final int position) {
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (radioGroup.getVisibility() == View.GONE) {
                        radioGroup.setVisibility(View.VISIBLE);
                        radioGroup.clearCheck();
                        questionBoundaryList.get(position).setSelectedAnswer(-1);
                        create_questions_fragment.deleteQuestionFromDBAtTheBottom(position);
                    } else {
                        radioGroup.setVisibility(View.GONE);
                        questionBoundaryList.get(position).setSelectedAnswer(-1);
                        radioGroup.clearCheck();

                        create_questions_fragment.deleteQuestionFromDBAtTheBottom(position);
                        create_questions_fragment.remove_id_to_ids_of_selected_questions(questionBoundaryList.get(position).getIdDB());

                    }
                }
            });
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    View radioButton = radioGroup.findViewById(radioButtonID);

                    int selectedAnswer = radioGroup.indexOfChild(radioButton);
                    if(selectedAnswer!=-1) {
                        questionBoundaryList.get(position).setSelectedAnswer(selectedAnswer);
                        create_questions_fragment.updateQuestionsFromDBAtTheBottom(position);
                        create_questions_fragment.add_id_to_ids_of_selected_questions(questionBoundaryList.get(position).getIdDB());

                    }

                }
            });


            if (checkBox.getText().toString().equals("")) {
                checkBox.setText(question.getQuestion());
                radioGroup.setVisibility(View.GONE);

                for (int i = 0; i < question.getNumOfAnswers(); i++) {

                    RadioButton radioButton = new RadioButton(context);
                    radioGroup.addView(radioButton);
                    radioButton.setText(question.getAnswers()[i]);
                    if(question.getSelectedAnswer()==i)
                    {
                        radioButton.setChecked(true);
                        checkBox.setChecked(true);
                        radioGroup.setVisibility(View.VISIBLE);
                    }

                }


            }
            else
            {
                if(question.getSelectedAnswer()!=-1)
                        ((RadioButton)radioGroup.getChildAt(question.getSelectedAnswer())).setChecked(true);
                else
                {
                    radioGroup.setVisibility(View.GONE);
                    checkBox.setChecked(false);
                }
            }
        }
    }


}
















