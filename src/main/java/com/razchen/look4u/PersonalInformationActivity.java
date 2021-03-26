package com.razchen.look4u;

import androidx.annotation.NonNull;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.java_classes.MyDate;
import com.razchen.look4u.java_classes.User;
import com.razchen.look4u.util.UtilFunctions;
import com.razchen.look4u.util.ViewTexts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.razchen.look4u.util.Keys.USER_ID;

public class PersonalInformationActivity extends UserMenu {
    private final int ROWS = 6;
   // private String[] titles={"שם","שם משפחה","אימייל","תמונה","תאריך לידה","מין"};
    private String[] info;
    private String userID;
    User user;

    //Database
    private Firebase firebase;

    //GUI
//    private TableLayout tableLayout;
//    private TableRow tableRow;
//    private TextView textView;

    //Gender
    //private Spinner spinner;
    private String genderString;

    //Date
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String birthdate;

    //Log
    private static final String TAG = "PersonalInformation";

    private ImageView profileImageView;
    private TextView fullNameTextView;
    private TextView  mailTextView;
    private TextView birthDayTextView;
    private TextView genderTextView;
    private Spinner genderSpinner;
    private   DatePickerDialog datePickerDialog;
    private SpinKitView spinKit;
    private RelativeLayout detailsRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        Bundle inBundle = getIntent().getExtras();
        userID=inBundle.get(USER_ID).toString();


        //Database
        firebase=new Firebase();

        info=new String[ROWS];
        getUserDataFromDataBase();
        setViews();
    }
    private void setViews() {

        profileImageView=findViewById(R.id.profileImageView);
         fullNameTextView=findViewById(R.id.fullNameTextView);
         mailTextView=findViewById(R.id.mailTextView);
         birthDayTextView=findViewById(R.id.birthDayTextView);
         genderTextView=findViewById(R.id.genderTextView);
        genderSpinner=findViewById(R.id.genderSpinner);
        spinKit=findViewById(R.id.spinKit);
        detailsRelativeLayout=findViewById(R.id.detailsRelativeLayout);
    }
    private void  getUserDataFromDataBase()    {
        firebase.getUsersTable().child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class);
                if(!checkIfUserHaveGender())
                    createSpinner();
                updateDateFromDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void updateDateFromDatabase() {
        Picasso.with(this).load(user.getImage()).resize(500,500).into(profileImageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                spinKit.setVisibility(View.GONE);
                detailsRelativeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {

            }
        });
        fullNameTextView.setText(user.getFirstName()+" "+user.getLastName());
        mailTextView.setText(user.getEmail());
        if(checkIfUserHaveGender()) {
            genderTextView.setText(UtilFunctions.convertGenderChoiceToHebrew(user.getGender()));
        }
        else
            genderSpinner.setVisibility(View.VISIBLE);

        if(!checkIfUserHaveBirthDay()) {
            createBirthDate();
            birthDayTextView.setText(ViewTexts.click_to_select_a_date_of_birth);
        }
        else
            birthDayTextView.setText(user.getBirthday().toString());
    }
    /*Credit for https://www.youtube.com/watch?v=FcMiw16bouA*/
    private  void createSpinner()    {
        final List<String> gender=new ArrayList<>();
        gender.add(ViewTexts.choose_gender);
        gender.add(ViewTexts.male);
        gender.add(ViewTexts.female);

        //Style and populate the spinner
        ArrayAdapter<String> dataAdapter;
        dataAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,gender);

        //DropDown layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        genderSpinner.setAdapter(dataAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getItemAtPosition(i).equals(ViewTexts.choose_gender))
                {
                    //do nothing
                    genderString="";
                    updateUserGenderAndSaveToDataBase();
                }
                else
                {
                    //on selecting a spinner item


                    String item = adapterView.getItemAtPosition(i).toString();

                    genderString= UtilFunctions.convertGenderChoiceToEnglish(item);
                    updateUserGenderAndSaveToDataBase();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    private  boolean checkIfUserHaveBirthDay()
    {
        return user.getBirthday()!=null;
    }
    private  boolean checkIfUserHaveGender()    {
        if(user.getGender()==null)
            return false;
        else
        if(user.getGender().equals(""))
            return false;
        return true;
    }
    /*Credit for https://www.youtube.com/watch?v=hwe1abDO2Ag */
    private void createBirthDate() {

        //date

            birthDayTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                     datePickerDialog = new DatePickerDialog(
                            PersonalInformationActivity.this,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDateSetListener,
                            year, month, day);
                    datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, ViewTexts.ok, datePickerDialog);
                    datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, ViewTexts.cancel, datePickerDialog);
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.YEAR, -120); // subtract 120 years from now

                    datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                    c.add(Calendar.YEAR, 120-12);
                    datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.show();
                }
            });


        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);


                    month = month + 1;
                    Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);


                    birthdate = day + "/" + month + "/" + year;
                    birthDayTextView.setText(birthdate);

                    updateUserBirthDayAndSaveToDataBase(year, month, day);

                }
            };


    }
    private void updateUserBirthDayAndSaveToDataBase(int year, int month, int day) {
        user.setBirthday(new MyDate(day,month,year));
        firebase.getUsersTable().child(userID).child(Firebase.BIRTHDAY).setValue(user.getBirthday());

    }
    private void updateUserGenderAndSaveToDataBase() {
        if(!genderString.isEmpty()) {
            user.setGender(genderString);
            firebase.getUsersTable().child(userID).child(Firebase.GENDER).setValue(user.getGender());

        }
    }

}
