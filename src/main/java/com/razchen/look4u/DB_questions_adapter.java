package com.razchen.look4u;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.razchen.look4u.server.Boundaries.QuestionBoundary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


// credit for https://www.youtube.com/watch?v=caUfVkjlI7I
public class DB_questions_adapter extends RecyclerView.Adapter<DB_questions_adapter.Viewholder> {
    private List<QuestionBoundary> questionBoundaryList;
    private Stack<String> stack = new Stack<>();
    private Context context;
    private Create_questions_fragment create_questions_fragment;

    public DB_questions_adapter(Create_questions_fragment create_questions_fragment, List<QuestionBoundary> questionBoundaryList, Context context) {
        this.questionBoundaryList = new ArrayList<>();
        this.create_questions_fragment = create_questions_fragment;
        this.context = context;
        this.questionBoundaryList = questionBoundaryList;

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_questions_from_user_fragment_row, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {
        holder.setData(questionBoundaryList.get(position), context, position);
    }

    public Stack<String> getStack() {
        return stack;
    }


    @Override
    public int getItemCount() {
        return questionBoundaryList.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {

        private TextView questionTextView;
        private RadioGroup radioGroup;
        private ImageView removeImageView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.TextViewQuestion);

            radioGroup = itemView.findViewById(R.id.answers_RadioGroup);
            removeImageView = itemView.findViewById(R.id.removeImageView);

        }

        private void setData(final QuestionBoundary question, Context context, final int position) {

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    String IdDB = questionBoundaryList.get(position).getIdDB();

                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    View radioButton = radioGroup.findViewById(radioButtonID);
                    int selectedAnswer = radioGroup.indexOfChild(radioButton);
                    question.setSelectedAnswer(selectedAnswer);
                    create_questions_fragment.updateQuestionFromUpperDB(IdDB, selectedAnswer);

                }
            });


            removeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String IdDB = questionBoundaryList.get(position).getIdDB();
                    questionBoundaryList.remove(position);
                    notifyItemRemoved(position - 1);
                    notifyDataSetChanged();
                    create_questions_fragment.update_DB_questions_adapter();
                    create_questions_fragment.removeQuestionFromUpperDB(IdDB);
                    create_questions_fragment.remove_id_to_ids_of_selected_questions(IdDB);

                }
            });
            if (questionTextView.getText().toString().equals("")) {
                questionTextView.setText(question.getQuestion());

                for (int i = 0; i < question.getNumOfAnswers(); i++) {
                    RadioButton radioButton = new RadioButton(context);
                    radioButton.setText(question.getAnswers()[i]);
                    radioGroup.addView(radioButton);
                }
                if (question.getSelectedAnswer() != -1)
                    ((RadioButton) radioGroup.getChildAt(question.getSelectedAnswer())).setChecked(true);

            }
        }


    }

}














