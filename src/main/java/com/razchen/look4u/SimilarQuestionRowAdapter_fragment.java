package com.razchen.look4u;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.razchen.look4u.server.Boundaries.QuestionBoundary;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// credit for https://www.youtube.com/watch?v=caUfVkjlI7I
public class SimilarQuestionRowAdapter_fragment extends RecyclerView.Adapter<SimilarQuestionRowAdapter_fragment.Viewholder> {
    private List<QuestionBoundary> questionBoundaryList;
    private Stack<String> stack = new Stack<>();
    private Context context;
    private int resultsFromSimilarQuestionDialog[];

    public SimilarQuestionRowAdapter_fragment(Create_questions_fragment create_questions_fragment, List<QuestionBoundary> questionBoundaryList, Context context, int resultsFromSimilarQuestionDialog[]) {
        this.questionBoundaryList = new ArrayList<>();
        this.context = context;
        this.questionBoundaryList = questionBoundaryList;
        this.resultsFromSimilarQuestionDialog = resultsFromSimilarQuestionDialog;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_question_row, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {
        holder.setData(questionBoundaryList.get(position), context);
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

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxIDSimilarQuestion);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (radioGroup.getVisibility() == View.GONE) {
                        radioGroup.setVisibility(View.VISIBLE);
                        radioGroup.clearCheck();//change to -1 (onCheckedChanged)
                    } else {
                        radioGroup.setVisibility(View.GONE);
                        resultsFromSimilarQuestionDialog[getAdapterPosition()] = -2;
                    }
                }
            });
            radioGroup = itemView.findViewById(R.id.radioGroupIDSimilarQuestion);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    View radioButton = radioGroup.findViewById(radioButtonID);
                    int selectedAnswer = radioGroup.indexOfChild(radioButton);
                    resultsFromSimilarQuestionDialog[getAdapterPosition()] = selectedAnswer;
                }
            });

        }

        private void setData(QuestionBoundary question, Context context) {
            checkBox.setText(question.getQuestion());

            for (int i = 0; i < question.getNumOfAnswers(); i++) {
                RadioButton radioButton = new RadioButton(context);
                radioButton.setText(question.getAnswers()[i]);
                radioGroup.addView(radioButton);
                radioGroup.setVisibility(View.GONE);
            }
        }


    }

}
