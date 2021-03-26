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
    import com.razchen.look4u.dl.Firebase;
    import com.razchen.look4u.server.Boundaries.QuestionBoundary;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;
    import java.util.Stack;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;
// credit for https://www.youtube.com/watch?v=caUfVkjlI7I
public class New_questions_adapter extends RecyclerView.Adapter<New_questions_adapter.Viewholder> {
    private List<QuestionBoundary> questionBoundaryList;
    private Stack<String> stack = new Stack<>();
    private  Context context;
    private DatabaseReference databaseReference;
    private Map<String,Integer> results;
    private Create_questions_fragment create_questions_fragment;

    public New_questions_adapter(Create_questions_fragment create_questions_fragment, List<QuestionBoundary> questionBoundaryList, Context context) {
        this.questionBoundaryList = new ArrayList<>();
        this.create_questions_fragment=create_questions_fragment;
        this.context=context;
        this.questionBoundaryList=questionBoundaryList;
        //this.results = results;
        //firebase = new Firebase();

        //databaseReference = firebase.getQuestionsTable();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_questions_from_user_fragment_row,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {
        // String question = questionBoundaryList.get(position).getQuestion();
        holder.setData(questionBoundaryList.get(position),context,position);
    }

    public Stack<String> getStack() {
        return stack;
    }

    private void updateOuterClass(Map<String,String> answers,int size, String selectedAnswer,Boolean isSelected){
        // this.advertisingQuestionnairesActivity.showAnswersFromQuestionRowAdapter(answers,size,selectedAnswer,isSelected);
    }
    private void updateThePathInOuterClass(){
        // fillOutQuestionnairesActivity.setCategoryChoicesArr(currentCategoryPathArr);
    }

    public boolean getStackIsEmpty(){
        return stack.isEmpty();
    }
    @Override
    public int getItemCount() {
        return questionBoundaryList.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{
        // private TextView title; //remove!
        private TextView questionTextView;
        private RadioGroup radioGroup;
        private ImageView removeImageView;
        //private LinearLayout linearLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            //  title = itemView.findViewById(R.id.textView1); //remove!
            questionTextView =itemView.findViewById(R.id.TextViewQuestion);
//            questionTextView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(radioGroup.getVisibility()==View.GONE) {
//                        radioGroup.setVisibility(View.VISIBLE);
//                        radioGroup.clearCheck();//change to -1 (onCheckedChanged)
//                        //resultsFromSimilarQuestionDialog[getAdapterPosition()]=-2;
//
//
//                    }
//                    else {
//                        radioGroup.setVisibility(View.GONE);
//                        // results.set(getAdapterPosition(),-2);
//                        results.put(getAdapterPosition()+"",-2);
//                    }
//                }
//            });
            radioGroup=itemView.findViewById(R.id.answers_RadioGroup);
            removeImageView=itemView.findViewById(R.id.removeImageView);
//            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup group, int checkedId) {
////                    selectedAnswerGlobal=true;
////                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
////                    View radioButton = radioGroup.findViewById(radioButtonID);
////
////                    int selectedAnswer = radioGroup.indexOfChild(radioButton);
////
////                    questionRowAdapter.updateAnswer(selectedAnswer);
//
//                    // questionnaire.getQuestions().get(indexCurrentQuestion+"").setSelectedAnswer(selectedAnswer + "");
//
//                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
//                    View radioButton = radioGroup.findViewById(radioButtonID);
//
//                    int selectedAnswer = radioGroup.indexOfChild(radioButton);
//                    //  results.set(getAdapterPosition(),selectedAnswer);
//                //    results.put(getAdapterPosition()+"",selectedAnswer);
//
//
//                }
//            });
            //linearLayout = itemView.findViewById(R.id.simi);

        }
        private void setData(final QuestionBoundary question, Context context,final int position) {

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
//                    selectedAnswerGlobal=true;
//                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
//                    View radioButton = radioGroup.findViewById(radioButtonID);
//
//                    int selectedAnswer = radioGroup.indexOfChild(radioButton);
//
//                    questionRowAdapter.updateAnswer(selectedAnswer);

                    // questionnaire.getQuestions().get(indexCurrentQuestion+"").setSelectedAnswer(selectedAnswer + "");

                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    View radioButton = radioGroup.findViewById(radioButtonID);

                    int selectedAnswer = radioGroup.indexOfChild(radioButton);
                    //  results.set(getAdapterPosition(),selectedAnswer);
                    //    results.put(getAdapterPosition()+"",selectedAnswer);

                question.setSelectedAnswer(selectedAnswer);
                }
            });


            removeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    questionBoundaryList.remove(position);
                    notifyItemRemoved(position-1);
                    notifyDataSetChanged();
                    create_questions_fragment.update_New_questions_adapter();
                    //notifyItemRangeChanged(position,questionBoundaryList.size());
                }
            });

            if (questionTextView.getText().toString().equals("")) {
                questionTextView.setText(question.getQuestion());

                for (int i = 0; i < question.getNumOfAnswers(); i++) {
                    RadioButton radioButton = new RadioButton(context);
//                    if(i==question.getSelectedAnswer())
//                        radioButton.setChecked(true);
                    radioButton.setText(question.getAnswers()[i]);
                    radioGroup.addView(radioButton);
                }
                ((RadioButton)radioGroup.getChildAt(question.getSelectedAnswer())).setChecked(true);

            //  title.setText(titleText); //remove!
            }
        }


    }

}














