//package com.razchen.look4u;
//
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.ValueEventListener;
//import com.razchen.look4u.dl.Firebase;
//import com.razchen.look4u.java_classes.Question;
//import com.razchen.look4u.java_classes.Question_With_Color;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//import java.util.Stack;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//// credit for https://www.youtube.com/watch?v=caUfVkjlI7I
//public class QuestionRowAdapter extends RecyclerView.Adapter<QuestionRowAdapter.Viewholder> {
//    private List<CategoryRowModel> categoryRowModelList;
//    private List<Question_With_Color>  questionList;
//    private Firebase firebase;
//    private String currentCategoryPath ="";
//    private ArrayList<String> currentCategoryPathArr = new ArrayList<>();
//
//    private String title = "";
//    private int lastItemClicked;
//    private  Viewholder lastHolder;
//    private boolean backButtonPressed = false;
//    private static final String SUB_PATH = "/"+Firebase.CATEGORIES_TABLE_NAME+"/";
//    private Stack<String> stack = new Stack<>();
//
//    private AdvertisingQuestionnairesActivity advertisingQuestionnairesActivity;
//    private DatabaseReference databaseReference;
//
//
//    public QuestionRowAdapter(AdvertisingQuestionnairesActivity advertisingQuestionnairesActivity) {
//        this.categoryRowModelList = new ArrayList<>();
//        this.questionList= new ArrayList<>();
//        this.advertisingQuestionnairesActivity = advertisingQuestionnairesActivity;
//        firebase = new Firebase();
//        databaseReference = firebase.getQuestionsTable();
//        readFromFirebase();
//    }
//
//    public void setAllRowModelToBlackColor(){
//        for(int i = 0; i < categoryRowModelList.size() ; i++)
//            categoryRowModelList.get(i).setChosen(false);
//    }
//
//    public int getLastItemClicked() {
//        return lastItemClicked;
//    }
//
//    public void  updateAnswer(int index)
//    {
//        questionList.get(lastItemClicked).setSelectedAnswer(index+"");
//    }
//    public Boolean addOrRemoveQuestion()
//    {
//        if(questionList.get(lastItemClicked).isSelected())
//        {
//            questionList.get(lastItemClicked).setSelected(false);
//            lastHolder.setColor(Color.BLUE);
//            questionList.get(lastItemClicked).setColor(Color.BLUE);
//
//            return false;
//        }
//        else
//        { questionList.get(lastItemClicked).setSelected(true);
//            lastHolder.setColor(Color.RED);
//            questionList.get(lastItemClicked).setColor(Color.RED);
//            return true;
//        }
//
//
//    }
//
//    public Question getLastQuestion()
//    {
//        return   questionList.get(lastItemClicked);
//    }
//
//    public void readFromFirebase(){
//        categoryRowModelList.clear();
//        questionList.clear();
//        Stack<String> stack =advertisingQuestionnairesActivity.getCategoriesStack();
////        while(!stack.isEmpty())
////        {
////
////        }
//        String s;
//        String lastcategoryName = advertisingQuestionnairesActivity.getNameOfLastCategory();
//
//        if(!stack.isEmpty()) {
//            s = stack.toString();
//            s = s.substring(2, s.length() - 1);
//            s = s.replace(", >", "/");
//            s = s + "/" + lastcategoryName;
//        }
//        else{
//            s = lastcategoryName;
//        }
//
//
//
//        databaseReference.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String dataSnapshotPath = dataSnapshot.getRef().toString();
//                String databaseRefPath = databaseReference.child(currentCategoryPath).toString();
//                /*if(!backButtonPressed&&!databaseRefPath.equals(dataSnapshotPath)*//*!checkUrlIdentify(databaseRefPath,dataSnapshotPath)*//*)
//                    return;*/
//                backButtonPressed = false;
//                boolean foundChosenCategory = false;
//
//                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//
//
//                    Question_With_Color q = new Question_With_Color();
//                    q.setQuestion(postSnapshot.child("question").getValue().toString());
//                    int numOfAnswers=postSnapshot.child("numOfAnswers").getValue(int.class);
//                    q.setIdDB(postSnapshot.getKey());
//                    for(int i=0;i<numOfAnswers;i++)
//                    {
//                        q.addAnswer(postSnapshot.child("answers").child(i+"").getValue().toString());
//
//                    }
//                    questionList.add(q);
//                }
//
//
//                Collections.sort(questionList, new Comparator<Question>() {
//                    @Override
//                    public int compare(Question categoryRowModel, Question t1) {
//                        return categoryRowModel.getQuestion().compareTo(t1.getQuestion());
//                    }
//                });
//                notifyDataSetChanged();
//                if(questionList.size()==0)
//                    advertisingQuestionnairesActivity.start_immediately_create_Questionnaires();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
////    public void onDatabaseChanged(){
////
////
////        childEventListener = databaseReference.child(currentCategoryPath).addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                readFromFirebase();
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////
////            }
////        });
////
////    }
//
//    @NonNull
//    @Override
//    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_from_db_row,parent,false);
//        return new Viewholder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {
//        String localTitle = questionList.get(position).getQuestion();
//
//
//        holder.setData(localTitle,questionList.get(position).getColor());
//        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(lastHolder!=null)
//                {
//                    if(!questionList.get(lastItemClicked).isSelected())
//                    {
//                    lastHolder.setColor(Color.BLACK);
//                    questionList.get(lastItemClicked).setColor(Color.BLACK);
//                    }
//
//                }
//
//                if(!questionList.get(position).isSelected())
//                {
//                    holder.setColor(Color.BLUE);
//                questionList.get(position).setColor(Color.BLUE);
//                }
//
//
//                lastHolder=holder;
//                lastItemClicked=lastHolder.getAdapterPosition();
////                if (canClick!=true)
////                    return;
////                canClick=false;
////                if(categoryRowModelList.size() == 0)
////                    return;
////              //  fillOutQuestionnairesActivity.getBackButton().setVisibility(View.VISIBLE);
////                setAllRowModelToBlackColor();
////                title = categoryRowModelList.get(position).getTitle();
////                CategoryRowModel chosenCategoryRowModel = categoryRowModelList.get(position);
////                chosenCategoryRowModel.setChosen(true);
////                chosenCategory = chosenCategoryRowModel.getTitle();
////                //fillOutQuestionnairesActivity.setChosenHasSubCategories(true);
////
////                if(!chosenCategoryRowModel.isHasSubcategories()) {
////                    timesToRemove = 2;
////                 //   fillOutQuestionnairesActivity.setChosenCategoryPath(currentCategoryPath+"/"+title);
////                //    fillOutQuestionnairesActivity.setChosenHasSubCategories(false);
////                   currentCategoryPathArr.add(title);
////                    updateThePathInOuterClass();
////                    notifyDataSetChanged();
////                    canClick=true;
////                    return;
////                }
////               fixTheCurrentPath();
////
////
//                updateOuterClass(questionList.get(position).getAnswers(),questionList.get(position).getNumOfAnswers(),questionList.get(position).getSelectedAnswer(),questionList.get(position).isSelected());
//
//
//            }
//        });
//
//    }
//
//
//
//
//    public Stack<String> getStack() {
//        return stack;
//    }
//
//    private void updateOuterClass(Map<String,String> answers,int size, String selectedAnswer,Boolean isSelected){
//      //  this.fillOutQuestionnairesActivity.showUserPath(stack.toString());
//        this.advertisingQuestionnairesActivity.showAnswersFromQuestionRowAdapter(answers,size,selectedAnswer,isSelected);
//    }
//    private void updateThePathInOuterClass(){
//       // fillOutQuestionnairesActivity.setCategoryChoicesArr(currentCategoryPathArr);
//    }
//
//
//
//
//    public boolean getStackIsEmpty(){
//        return stack.isEmpty();
//    }
//    @Override
//    public int getItemCount() {
//        return questionList.size();
//    }
//
//    public void setBackButtonPressed(boolean backButtonPressed) {
//        this.backButtonPressed = backButtonPressed;
//    }
//
//    class Viewholder extends RecyclerView.ViewHolder{
//        private TextView title;
//        private int color;
//        private LinearLayout linearLayout;
//        public Viewholder(@NonNull View itemView) {
//            super(itemView);
//            title = itemView.findViewById(R.id.textView1);
//
//            linearLayout = itemView.findViewById(R.id.innerLayout);
//
//        }
//        private void setData(String titleText, int color){
//            title.setText(titleText);
//            title.setTextColor(color);
//
//        }
//
//        public void setColor(int color) {
//            title.setTextColor(color);
//        }
//    }
//
//}
