package com.razchen.look4u;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.java_classes.MyDate;
import com.razchen.look4u.java_classes.User;
import com.razchen.look4u.util.UserMessages;
import com.razchen.look4u.util.UtilFunctions;
import com.razchen.look4u.util.ViewTexts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PopUpFillAgeAndGender {

    //PopupWindow display method
    private User user;
    private Spinner genderSpinner;
    private boolean birthDayBoolean;
    private boolean genderBoolean;
    private TextView progress_TextView;

    public void showPopupWindow(final View view, final boolean gender, final boolean birthDay, final String userID) {


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.pop_up_fill_age_and_gender, null);
        this.birthDayBoolean = birthDay;
        this.genderBoolean = gender;


        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

        genderSpinner = popupView.findViewById(R.id.genderSpinner);
        final DatePicker datePicker = popupView.findViewById(R.id.datePicker);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -120); // subtract 120 years from now

        datePicker.setMinDate(c.getTimeInMillis());
        c.add(Calendar.YEAR, 120-12);
        datePicker.setMaxDate(c.getTimeInMillis());

        final TextView titleTextView = popupView.findViewById(R.id.titleTextView);
        getUserDataFromDataBase(userID);
        if (birthDayBoolean == true) {
            titleTextView.setText(UserMessages.chooseBirthDay);

        } else {
            if (genderBoolean) {
                datePicker.setVisibility(View.GONE);
                titleTextView.setText(UserMessages.chooseYourGender);
                showGenderSpinner(popupView);
            }
        }
//        TextView titleText = popupView.findViewById(R.id.titleText);
//        titleText.setText(title);
//
//        TextView infoText = popupView.findViewById(R.id.infoText);
//        infoText.setText(info);

        progress_TextView = popupView.findViewById(R.id.progress_TextView);

        if ((genderBoolean) && (birthDayBoolean)) {
            progress_TextView.setText(ViewTexts.fill_next_details+ViewTexts.level_1_of_2);
        } else {
            progress_TextView.setText(ViewTexts.fill_next_details+ViewTexts.level_1_of_1);

        }

        Button okButton = popupView.findViewById(R.id.ok_Button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                int day = datePicker.getDayOfMonth();
                String birthdayString = day + "/" + month + "/" + year;
                Log.d("birthday", "onClick: " + birthdayString);
                if (birthDayBoolean == true) {
                    if (currentYear - year >= 12) {
                        updateUserBirthDayAndSaveToDataBase(day, month, year, userID);
                        UtilFunctions.showUpperToast(UserMessages.birthDay_saved_successfully, Toast.LENGTH_LONG);
                        if (genderBoolean == false)
                            popupWindow.dismiss();
                        else {
                            birthDayBoolean = false;
                            datePicker.setVisibility(View.GONE);
                            titleTextView.setText(UserMessages.chooseYourGender);
                            progress_TextView.setText(ViewTexts.fill_next_details+ViewTexts.level_2_of_2);

                            showGenderSpinner(popupView);
                        }

                    } else {
                        UtilFunctions.showUpperToast("נא לבחור תאריך לידה תקני", Toast.LENGTH_LONG);
                    }
                } else {
                    if ((genderBoolean == true) && (birthDayBoolean == false)) {
                        datePicker.setVisibility(View.GONE);

                        updateUserGenderAndSaveToDataBase(genderSpinner.getSelectedItem().toString(), userID, popupWindow);
                    }
                }
                //As an example, display the message
            }
        });


        //Handler for clicking on the inactive zone of the window

//        popupView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                //Close the window when clicked
//                popupWindow.dismiss();
//                return true;
//            }
//        });
    }

    private void showGenderSpinner(View popupView) {

        final List<String> gender = new ArrayList<>();
        gender.add(ViewTexts.press_to_choose_your_gender);
        gender.add(ViewTexts.male);
        gender.add(ViewTexts.female);

        //Style and populate the spinner
        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter(popupView.getContext(), android.R.layout.simple_spinner_item, gender);

        //DropDown layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        genderSpinner.setAdapter(dataAdapter);
    }

    private void updateUserGenderAndSaveToDataBase(String genderString, String userID, PopupWindow popupWindow) {
        if (genderString.equals(ViewTexts.press_to_choose_your_gender)) {
            UtilFunctions.showUpperToast(UserMessages.chooseYourGender, Toast.LENGTH_LONG);
        } else {
            String genderStringInEnglish = UtilFunctions.convertGenderChoiceToEnglish(genderString);

            user.setGender(genderStringInEnglish);
            new Firebase().getUsersTable().child(userID).child(Firebase.GENDER).setValue(user.getGender());

            UtilFunctions.showUpperToast(UserMessages.succsesful_gender_saved, Toast.LENGTH_LONG);
            popupWindow.dismiss();
        }


    }

    private void updateUserBirthDayAndSaveToDataBase(int day, int month, int year, String userID) {
        user.setBirthday(new MyDate(day, month, year));
        new Firebase().getUsersTable().child(userID).child(Firebase.BIRTHDAY).setValue(user.getBirthday());

    }

    private void getUserDataFromDataBase(final String userID) {
        new Firebase().getUsersTable().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.child(userID).getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}