package com.razchen.look4u;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.kimkevin.cachepot.CachePot;
import com.razchen.look4u.util.UtilFunctions;

import static com.razchen.look4u.util.Keys.CATEGORY_PATH_FOR_USER;
import static com.razchen.look4u.util.Keys.FROM_AGE;
import static com.razchen.look4u.util.Keys.GENDER;
import static com.razchen.look4u.util.Keys.OK;
import static com.razchen.look4u.util.Keys.TO_AGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterUsers_fragment extends Fragment {
    private ViewPager viewPager;
    private RelativeLayout filterUsers_fragment_RelativeLayout;
    private TextView chooseGenderTextView;
    private TextView pathTextView;
    private NumberPicker from_age_range_numberPicker;
    private NumberPicker to_age_range_numberPicker;
    private NumberPicker genderPicker;
    private int fromAge;
    private int toAge;
    private String gender;
    private Button back_Button;
    private Button next_button;
    private View rootView;

    public FilterUsers_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_filter_users, container, false);

        filterUsers_fragment_RelativeLayout = rootView.findViewById(R.id.filterUsers_fragment_RelativeLayout);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);

        CachePot.getInstance().push(3, null);//can't go to crete questions

        createPickAgeGUI();
        createPickGenderGUI();
        set_next_button();
        set_back_button();
        // Inflate the layout for this fragment


        handel_back_press();
        readPathText();
        return rootView;
    }

    private void set_back_button() {
        back_Button = rootView.findViewById(R.id.back_Button);
        back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);

            }
        });
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
                        viewPager.setCurrentItem(0);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void sendGetRequest() {
        String value = CachePot.getInstance().pop(2);
        if (value == null) {
            viewPager.setCurrentItem(0);
            return;
        }
        if (value.equals(OK)) ;
        {
            viewPager.setCurrentItem(1);
            CachePot.getInstance().push(2, OK);

        }
        readPathText();
        CachePot.getInstance().push(2, OK);
        CachePot.getInstance().push(3, null);//can't go to crete questions

    }


    //filtering screen functions
    private void createPickGenderGUI() {
        create_chooseGenderTextView();
        create_genderPicker();
    }

    private void create_chooseGenderTextView() {
        chooseGenderTextView = rootView.findViewById(R.id.chooseGenderTextView);
    }

    private void createPickAgeGUI() {
        set_from_and_to_age_range_numberPicker();
        set_from_age_range_numberPicker();
        set_to_age_range_numberPicker();
    }

    private void create_genderPicker() {
        genderPicker = rootView.findViewById(R.id.genderPicker);
        final String genders[] = {getString(R.string.male), getString(R.string.Irrelevant), getString(R.string.female)};
        genderPicker.setMinValue(0);
        genderPicker.setMaxValue(genders.length - 1);
        genderPicker.setDisplayedValues(genders);
        genderPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        genderPicker.setValue(1);

        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            }
        };
        genderPicker.setOnValueChangedListener(myValChangedListener);

    }

    private void set_from_and_to_age_range_numberPicker() {
        from_age_range_numberPicker = rootView.findViewById(R.id.from_age_range_numberPicker);
        to_age_range_numberPicker = rootView.findViewById(R.id.to_age_range_numberPicker);

    }

    private void set_from_age_range_numberPicker() {

        from_age_range_numberPicker.setMinValue(12);
        from_age_range_numberPicker.setMaxValue(120);
        from_age_range_numberPicker.setWrapSelectorWheel(true);


        from_age_range_numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                to_age_range_numberPicker.setMinValue(from_age_range_numberPicker.getValue());
            }
        });

    }

    private void set_to_age_range_numberPicker() {

        to_age_range_numberPicker.setMinValue(from_age_range_numberPicker.getValue());
        to_age_range_numberPicker.setMaxValue(120);
        to_age_range_numberPicker.setValue(120);
        to_age_range_numberPicker.setWrapSelectorWheel(true);

        to_age_range_numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
    }


    private void set_next_button() {

        next_button = rootView.findViewById(R.id.next_button);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                save_info_to_CachePot();
                goto_choose_questions_from_database_fragment();
            }
        });
    }

    private void save_info_to_CachePot() {


        fromAge = from_age_range_numberPicker.getValue();
        toAge = to_age_range_numberPicker.getValue();
        gender = genderPicker.getDisplayedValues()[genderPicker.getValue()];
        gender = UtilFunctions.convertGenderChoiceToEnglish(gender);

        CachePot.getInstance().push(FROM_AGE, fromAge);
        CachePot.getInstance().push(TO_AGE, toAge);
        CachePot.getInstance().push(GENDER, gender);
    }

    private void readPathText() {
        String categoryPathForUser = CachePot.getInstance().pop(CATEGORY_PATH_FOR_USER);
        pathTextView = rootView.findViewById(R.id.pathTextView);
        pathTextView.setText(categoryPathForUser);
        CachePot.getInstance().push(CATEGORY_PATH_FOR_USER, categoryPathForUser);

    }

    private void goto_choose_questions_from_database_fragment() {
        CachePot.getInstance().push(3, OK);
        viewPager.setCurrentItem(2);
    }


}
