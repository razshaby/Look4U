package com.razchen.look4u;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.razchen.look4u.util.UtilFunctions;
import com.razchen.look4u.util.ViewTexts;

import java.util.ArrayList;
import java.util.List;

import static com.razchen.look4u.util.Keys.CATEGORY_CHOICES_ARR;
import static com.razchen.look4u.util.Keys.FROM_AGE;
import static com.razchen.look4u.util.Keys.GENDER;
import static com.razchen.look4u.util.Keys.GLOBAL_PATH;
import static com.razchen.look4u.util.Keys.PATH_FOR_USER;
import static com.razchen.look4u.util.Keys.TO_AGE;
import static com.razchen.look4u.util.Keys.USER_ID;

public class FillOutQuestionnairesChooseAdvertiserDetails extends UserMenu {
    private NumberPicker npFrom;
    private NumberPicker npTo;
    private static final int MIN_VAL = 12;
    private static final int MAX_VAL = 120;
    private int fromAge = 0;
    private int toAge = 0;
    private Spinner spinner;
    private String genderString;
    private String seekerUserId = "";
    private String pathForUser;
    private ArrayList<String> categoryChoicesArr = new ArrayList<>();
    private Button searchButton;
    private TextView pathTxt;
    private Button back_Button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_out_questionnaires_choose_advertiser_details);
        npFrom = findViewById(R.id.numberPickerFrom);
        npTo = findViewById(R.id.numberPickerUntil);
        back_Button = findViewById(R.id.back_Button);
        numberPickerConfiguration(npFrom, MIN_VAL, MAX_VAL, true);
        numberPickerConfiguration(npTo, MIN_VAL, MAX_VAL, true);
        npTo.setValue(MAX_VAL);
        npFrom.setOnValueChangedListener(new MyOnValueChangedListener());
        npTo.setOnValueChangedListener(new MyOnValueChangedListener());
        createSpinner();
        Bundle iBundle = getIntent().getExtras();
        seekerUserId = iBundle.getString(USER_ID);
        pathForUser = iBundle.getString(GLOBAL_PATH);
        showUserPath();
        categoryChoicesArr = iBundle.getStringArrayList(CATEGORY_CHOICES_ARR);

        searchButton = findViewById(R.id.searchButton);
        back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FillOutQuestionnairesChooseAdvertiserDetails.this, FillOutQuestionnaires_ShowMatches.class);
                fromAge = npFrom.getValue();
                toAge = npTo.getValue();
                intent.putExtra(FROM_AGE, fromAge);
                intent.putExtra(TO_AGE, toAge);
                intent.putExtra(PATH_FOR_USER, pathForUser);
                intent.putExtra(GENDER, genderString);
                intent.putStringArrayListExtra(CATEGORY_CHOICES_ARR, categoryChoicesArr);
                intent.putExtra(USER_ID, seekerUserId);

                startActivity(intent);


            }
        });
        //  showUserPath();


    }

    public void showUserPath() {
        pathTxt = findViewById(R.id.fillOutChooseAdDetailsPathTextView);
        pathTxt.setText(pathForUser);


    }

    private void numberPickerConfiguration(NumberPicker np, int minVal, int maxVal, boolean isWheel) {
        np.setWrapSelectorWheel(isWheel);
        np.setMinValue(minVal);
        np.setMaxValue(maxVal);
    }

    class MyOnValueChangedListener implements NumberPicker.OnValueChangeListener {
        private NumberPicker npFrom = FillOutQuestionnairesChooseAdvertiserDetails.this.npFrom;
        private NumberPicker npTo = FillOutQuestionnairesChooseAdvertiserDetails.this.npTo;


        @Override
        public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
            numberPicker.setValue(newVal);
            if (npTo.getValue() < npFrom.getValue())
                npTo.setValue(npFrom.getValue());
        }
    }

    private void createSpinner() {
        spinner = findViewById(R.id.genderSpinner);
        final List<String> gender = new ArrayList<>();
        gender.add(ViewTexts.not_relevant);
        gender.add(ViewTexts.male);
        gender.add(ViewTexts.female);

        //Style and populate the spinner
        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gender);

        //DropDown layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("בחר מין")) {
                    //do nothing
                    genderString = "";
                } else {
                    //on selecting a spinner item


                    String item = adapterView.getItemAtPosition(i).toString();

                    genderString = UtilFunctions.convertGenderChoiceToEnglish(item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

}
