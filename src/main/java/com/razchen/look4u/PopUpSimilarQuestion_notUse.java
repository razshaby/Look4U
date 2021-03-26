//package com.razchen.look4u;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.razchen.look4u.java_classes.Question;
//import com.razchen.look4u.server.Boundaries.QuestionBoundary;
//import com.razchen.look4u.util.UserMessages;
//
//
//
////Credit for https://medium.com/@evanbishop/popupwindow-in-android-tutorial-6e5a18f49cc7
//public class PopUpSimilarQuestion_notUse {
//
//
//    private RadioGroup radioGroup;
//    private Context context;
//    AdvertisingQuestionnairesActivity advertisingQuestionnairesActivity;
//
//
//
//
//    public PopUpSimilarQuestion_notUse(Context context, AdvertisingQuestionnairesActivity advertisingQuestionnairesActivity) {
//        this.context=context;
//        this.advertisingQuestionnairesActivity=advertisingQuestionnairesActivity;
//
//
//    }
////PopupWindow display method
//
//    public void showPopupWindow(final View view, final QuestionBoundary[] questionsBoundary) {
//
//
//        //Create a View object yourself through inflater
//        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
//        View popupView = inflater.inflate(R.layout.pop_up_similar_question_layout, null);
//        radioGroup = popupView.findViewById(R.id.radioGroup);
//
//        //Specify the length and width through constants
//        int width = LinearLayout.LayoutParams.MATCH_PARENT;
//        int height = LinearLayout.LayoutParams.MATCH_PARENT;
//
//        //Make Inactive Items Outside Of PopupWindow
//        boolean focusable = true;
//
//        //Create a window with our parameters
//        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
//
//        //Set the location of the window on the screen
//        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
//
//        //Initialize the elements of our window, install the handler
//
////        TextView questionText = popupView.findViewById(R.id.questionText);
////        questionText.setText(questionBoundary.getQuestion());
//        //CheckBox[] checkBoxs=new CheckBox[5];
//        CheckBox checkBox;
//        for(int i=0;i<questionsBoundary.length;i++)
//        {
//
////            String checkBoxID = "checkBox" + i;
////
////            int resID =     popupView.getResources().getIdentifier(checkBoxID, "id", popupView.getContext().getPackageName());
////
////            checkBoxs[i] = ((CheckBox) popupView.findViewById(resID));
////            checkBoxs[i].setText(questionsBoundary[i].getQuestion());
//
//
//            //checkBoxs[i].setOnClickListener(this);
//
//            switch (i)
//            {
//                case 1:
//                    checkBox=popupView.findViewById(R.id.checkBox1);
//
//                    break;
//
//                case 2:
//                    checkBox=popupView.findViewById(R.id.checkBox2);
//
//                    break;
//                case 3:
//                    checkBox=popupView.findViewById(R.id.checkBox3);
//
//                    break;
//                case 4:
//                    checkBox=popupView.findViewById(R.id.checkBox4);
//
//                    break;
//                case 5:
//                    checkBox=popupView.findViewById(R.id.checkBox5);
//
//                    break;
//                default:
//                    checkBox=popupView.findViewById(R.id.checkBox1);
//
//            }
//            checkBox.setText(questionsBoundary[i].getQuestion());
//
//
//
//        }
////        for(int i=0;i<questionsBoundary.length;i++)
////        {
////            RadioGroup radioGroup;
////            switch (i) {
////
////
////                case 1:
////                    radioGroup = popupView.findViewById(R.id.radioGroup1);
////
////                    break;
////
////                case 2:
////                    radioGroup = popupView.findViewById(R.id.radioGroup1);
////
////                    break;
////                case 3:
////                    checkBox = popupView.findViewById(R.id.checkBox3);
////
////                    break;
////                case 4:
////                    checkBox = popupView.findViewById(R.id.checkBox4);
////
////                    break;
////                case 5:
////                    checkBox = popupView.findViewById(R.id.checkBox5);
////
////                    break;
////            }
////        }
//
//
//        Button add_question_Button = popupView.findViewById(R.id.add_question_Button);
//        add_question_Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                if (radioGroup.getCheckedRadioButtonId() == -1) {
//                    showUpperToast(UserMessages.Please_select_answer,Toast.LENGTH_SHORT);
//
//                }
//                else
//                {
//                    ask_if_sure_to_add_question(popupWindow,questionsBoundary);
//                }
//            }
//        });
//
//        Button cancel_Button = popupView.findViewById(R.id.cancel_Button);
//        cancel_Button.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View view) {
//        popupWindow.dismiss();
//    }
//});
//
//        //There is cancel button fot that
//      // handler_for_clicking_on_the_inactive_zone(popupWindow,popupView);
//
//
//
//        //add_answers(questionsBoundary);
//
//
//    }
//
//    //Not in use
//    //Handler for clicking on the inactive zone of the window
//    private void handler_for_clicking_on_the_inactive_zone(final PopupWindow popupWindow,final View popupView) {
//        popupView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                //Close the window when clicked
//                popupWindow.dismiss();
//                return true;
//            }
//        });
//    }
//
//
//    private void add_question(final PopupWindow popupWindow,final QuestionBoundary[] questionBoundary)
//    {
////        Question question=questionBoundary.toEntityQuestion();
////        int radioButtonID = radioGroup.getCheckedRadioButtonId();
////        View radioButton = radioGroup.findViewById(radioButtonID);
////
////        int selectedAnswer = radioGroup.indexOfChild(radioButton);
////        question.setSelectedAnswer(selectedAnswer + "");
////
////        advertisingQuestionnairesActivity.addQuestionToSimilarQuestions(question);
////        advertisingQuestionnairesActivity.update_count_of_total_questions_added_TextView();
////        advertisingQuestionnairesActivity.clean_create_questionnaire_fields();
////        advertisingQuestionnairesActivity.add_id_to_ids_of_selected_questions(questionBoundary.getIdDB());
////        popupWindow.dismiss();
////
////
////        showUpperToast(UserMessages.The_question_added_successfully,Toast.LENGTH_LONG);
//
//    }
//
//    private void showUpperToast(String message,int length)
//    {
//        Toast toast = Toast.makeText(context,message,length);
//        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 150);
//        View view = toast.getView();
//
//        //To change the Background of Toast
//        view.setBackgroundColor(Color.rgb(178,255,102));
//
//        toast.show();
//    }
//
//    private void ask_if_sure_to_add_question(final PopupWindow popupWindow,final QuestionBoundary[] questionsBoundary)
//    {
//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case DialogInterface.BUTTON_POSITIVE:
//                        //Yes button clicked
//                        add_question(popupWindow,questionsBoundary);
//                        break;
//
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        //No button clicked
//                        break;
//                }
//            }
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage(R.string.Are_you_sure_you_want_to_add_this_question).setPositiveButton(R.string.Yes, dialogClickListener)
//                .setNegativeButton(R.string.no, dialogClickListener).show();
//    }
//
//    private void add_answers(final QuestionBoundary questionBoundary)
//    {
//        for (String s:questionBoundary.getAnswers()) {
//            Log.d("popupsimilar", s);
//            createRadioButton(s);
//
//        }
//    }
//
//    private void createRadioButton(String text) {
//        RadioButton radioButton = new RadioButton(context);
//        radioButton.setText(text);
//        radioGroup.addView(radioButton);
//
//
//    }
//
//}