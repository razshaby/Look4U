package com.razchen.look4u;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.kimkevin.cachepot.CachePot;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.java_classes.Category;
import com.razchen.look4u.util.UserMessages;
import com.razchen.look4u.util.UtilFunctions;
import com.razchen.look4u.util.ViewTexts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import java.util.regex.Pattern;

import static com.razchen.look4u.util.Keys.CATEGORIES_STACK;
import static com.razchen.look4u.util.Keys.CATEGORY_LIST;
import static com.razchen.look4u.util.Keys.CATEGORY_PATH_FOR_USER;
import static com.razchen.look4u.util.Keys.DATABASE_REFERENCE;
import static com.razchen.look4u.util.Keys.NAME_OF_LAST_CATEGORY;
import static com.razchen.look4u.util.Keys.OK;
import static com.razchen.look4u.util.Keys.USER_ID;
import static com.razchen.look4u.util.UtilFunctions.show_or_hide_button;


/**
 * A simple {@link Fragment} subclass.
 */
public class selectCategory_fragment extends Fragment {
    private ViewPager viewPager;


    private AlertDialog.Builder addCategoryPopUp;
    private ArrayList<Category> categoryList;

    private boolean nextPressOnBackWillExit;
    private Button category_back_Button;
    private Button confirm_category_selection_Button;

    private CheckBox checkBox_subCategory;

    private DatabaseReference databaseReference;

    private EditText category_editText;

    private LinearLayout linearLayoutAddCategory;
    private ListView listView;

    private SpinKitView spinKit;
    private String userID;
    private Stack<String> categoriesStack;
    private String nameOfLastCategory;

    private TextView questionnaireAlreadyAddedTextView;
    private TextView pathTextView;

    private View rootView;


    public selectCategory_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        handleNextPressOnBackWillExit();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_select_category, container, false);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);

        categoriesStack = new Stack<>();
        Bundle inBundle = getActivity().getIntent().getExtras();
        userID = inBundle.get(USER_ID).toString();
        Firebase firebase = new Firebase();
        databaseReference = firebase.getCategoriesTable();
        checkIfThereIsACategories();

        //Categories
        Button add_category_Button = rootView.findViewById(R.id.add_category_Button);

        createAndSetCategory_back_Button();

        confirm_category_selection_Button = rootView.findViewById(R.id.next_button);
        questionnaireAlreadyAddedTextView = rootView.findViewById(R.id.questionnaireAlreadyAddedTextView);
        createAndSetCategory_editText();
        checkBox_subCategory = new CheckBox(rootView.getContext());
        checkBox_subCategory.setText(UserMessages.Contains_subcategories);
        linearLayoutAddCategory = new LinearLayout(rootView.getContext());
        linearLayoutAddCategory.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayoutAddCategory.setOrientation(LinearLayout.VERTICAL);
        linearLayoutAddCategory.addView(category_editText);
        linearLayoutAddCategory.addView(checkBox_subCategory);

        pathTextView = rootView.findViewById(R.id.pathTextView);
        listView = rootView.findViewById(R.id.listView);

        add_category_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addCategoryPopUp.show();
            }
        });

        category_back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back_buttonPressed();
            }
        });

        confirm_category_selection_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfUserHasGenderAndAge();

            }
        });

        onChildAddedToCategories();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickOnCategory((Category) parent.getItemAtPosition(position));

            }
        });


        createEditTextPopUp();

        selectByPreviousChoice();


        spinKit = rootView.findViewById(R.id.spinKit);
        handel_back_press();
        return rootView;
    }

    private void checkIfThereIsACategories() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    spinKit.setVisibility(View.GONE);
                    showPopUPMessage(UserMessages.There_are_no_categories_yet_click_the_plus_to_add_a_category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void handleNextPressOnBackWillExit() {
        String nameOfLastCategoryTemp = CachePot.getInstance().pop(NAME_OF_LAST_CATEGORY);
        if (nameOfLastCategoryTemp == null)
            nextPressOnBackWillExit = true;
        else {
            nextPressOnBackWillExit = false;
            CachePot.getInstance().push(NAME_OF_LAST_CATEGORY, nameOfLastCategoryTemp);

        }
    }

    private void back_buttonPressed() {
        if (!databaseReference.getParent().equals(databaseReference.getRoot())) {
            databaseReference = databaseReference.getParent();
            databaseReference = databaseReference.getParent();

            categoriesStack.pop();
            onChildAddedToCategories();
            readDataFromDataBase();
            setPathTextView(false);
        }
        if (databaseReference.getParent().equals(databaseReference.getRoot())) {

            show_or_hide_button(false, category_back_Button);
        }

        show_or_hide_button(false, confirm_category_selection_Button);

        questionnaireAlreadyAddedTextView.setVisibility(View.GONE);
        cancel_permission_to_next_stages();
    }

    private void handel_back_press() {
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        if (nextPressOnBackWillExit == true)
                            return false;

                        back_buttonPressed();
                        if (categoriesStack.empty())
                            nextPressOnBackWillExit = true;
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void createAndSetCategory_back_Button() {
        category_back_Button = rootView.findViewById(R.id.back_Button);
    }

    private void createAndSetCategory_editText() {
        category_editText = new EditText(rootView.getContext());
        category_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                for (int i = editable.length(); i > 0; i--) {

                    if (editable.subSequence(i - 1, i).toString().equals("\n"))
                        editable.replace(i - 1, i, "");
                }

                String myTextString = editable.toString();
            }
        });


    }

    private void checkIfUserHasGenderAndAge() {
        DatabaseReference dbUserRef = new Firebase().getUsersTable().child(userID);
        dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(Firebase.BIRTHDAY) || !dataSnapshot.hasChild(Firebase.GENDER)
                        || dataSnapshot.child(Firebase.GENDER).getValue().toString().trim().equals("")
                ) {

                    PopUpFillAgeAndGender popUpFillAgeAndGender = new PopUpFillAgeAndGender();
                    popUpFillAgeAndGender.showPopupWindow(rootView.getRootView(), !dataSnapshot.hasChild(Firebase.GENDER), !dataSnapshot.hasChild(Firebase.BIRTHDAY), userID);
                    return;

                }
                confirmClicked();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onChildAddedToCategories() {

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                readDataFromDataBase();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void clickOnCategory(Category category) {
        nextPressOnBackWillExit = false;
        cancel_permission_to_next_stages();
        nameOfLastCategory = category.getName();
        questionnaireAlreadyAddedTextView.setVisibility(View.GONE);

        show_or_hide_button(false, confirm_category_selection_Button);

        if (category.isHasSubcategories()) {
            categoriesStack.push(category.toString());
            setPathTextView(false);

            show_or_hide_button(true, category_back_Button);
            show_or_hide_button(false, confirm_category_selection_Button);

            questionnaireAlreadyAddedTextView.setVisibility(View.GONE);
            databaseReference = databaseReference.child(category.getName());
            databaseReference = databaseReference.child(Firebase.CATEGORIES_TABLE_NAME);


            onChildAddedToCategories();
            readDataFromDataBase();
        } else {
            categorySelected(category);
        }

    }

    private void setPathTextView(boolean selected) {
        if (categoriesStack.size() != 0) {
            if (!selected)
                pathTextView.setText(categoriesStack.toString().substring(2, categoriesStack.toString().length() - 1).replace(",", "-"));
            else {
                String categoryName = categoriesStack.pop();
                categoriesStack.push(UserMessages.Smaller_than + categoryName);
                pathTextView.setText(categoriesStack.toString().substring(2, categoriesStack.toString().length() - 1).replace(",", "-"));
                categoriesStack.pop();
            }

        } else
            pathTextView.setText("");

    }

    private void createEditTextPopUp() {

        addCategoryPopUp = new AlertDialog.Builder(rootView.getContext());
        category_editText.setHint(UserMessages.typeCategoryToAdd);

        addCategoryPopUp.setView(linearLayoutAddCategory);


        addCategoryPopUp.setPositiveButton(UserMessages.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                saveCategoryInfoToDataBase();

                ((ViewGroup) linearLayoutAddCategory.getParent()).removeView(linearLayoutAddCategory);
            }
        });

        addCategoryPopUp.setNegativeButton(UserMessages.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ((ViewGroup) linearLayoutAddCategory.getParent()).removeView(linearLayoutAddCategory);

            }
        });

        addCategoryPopUp.setCancelable(false);

    }

    private void saveCategoryInfoToDataBase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (checkEditTextEmpty(category_editText)) {
                    Toast.makeText(rootView.getContext(), UserMessages.Please_enter_a_category, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (category_editText.getText().toString().length() == 1) {
                    Toast.makeText(rootView.getContext(), UserMessages.More_than_one_character_must_be_entered, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Pattern.matches("[a-zA-Zא-ת\" \"0-9]+", category_editText.getText())) {
                    Toast.makeText(rootView.getContext(), UserMessages.Only_letters_and_numbers_are_allowed, Toast.LENGTH_SHORT).show();
                    return;
                }
                String categoryName = category_editText.getText().toString().trim();
                if (dataSnapshot.hasChild(categoryName)) {
                    UtilFunctions.showUpperToast(UserMessages.Cant_add_the_category_already_exists, Toast.LENGTH_LONG);

                } else {

                    if ((checkBox_subCategory.isChecked()) && (categoriesStack.size() >= 10)) {
                        Toast.makeText(rootView.getContext(), UserMessages.Cannot_add_because_the_number_of_subcategories_is_too_high, Toast.LENGTH_LONG).show();
                    } else {
                        Category category = new Category(categoryName, userID, checkBox_subCategory.isChecked());
                        databaseReference.child(categoryName).setValue(category);
                        Toast.makeText(rootView.getContext(), UserMessages.Category_added_successfully, Toast.LENGTH_LONG).show();
                    }
                }
                clean_category_editText();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean checkEditTextEmpty(EditText editText) {


        String name = editText.getText().toString();
        if (name.trim().length() == 0) {
            return true;
        }
        return false;
    }

    private void clean_category_editText() {
        category_editText.setText("");

    }

    private void readDataFromDataBase() {
        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        categoryList = sortCategories(dataSnapshot.getChildren());
                        spinKit.setVisibility(View.GONE);


                        ArrayAdapter arrayAdapter = new ArrayAdapter(rootView.getContext(), android.R.layout.simple_list_item_1, categoryList);

                        listView.setAdapter(arrayAdapter);

                        if (categoryList.size() == 0)
                            showPopUPMessage(UserMessages.There_are_no_categories_yet_click_the_plus_to_add_a_category);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private ArrayList<Category> sortCategories(Iterable<DataSnapshot> children) {
        ArrayList<Category> list = new ArrayList<>();
        for (DataSnapshot child : children) {
            Category category = child.getValue(Category.class);
            list.add(category);
        }

        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                return o1.compareTo(o2);
            }
        });

        return list;
    }

    private void confirmClicked() {

        goto_filtering_users_fragment();

    }

    private void categorySelected(Category category) {
        categoriesStack.push(category.toString());
        haveQuestionnairesInThisCategory();
        setPathTextView(true);

    }

    private void haveQuestionnairesInThisCategory() {
        Firebase firebase = new Firebase();
        DatabaseReference userRef = firebase.getUsersTable().child(userID);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean haveAlreadyCategory = true;
                if (dataSnapshot.hasChild(Firebase.QUESTIONNAIRES_TABLE_NAME)) {
                    dataSnapshot = dataSnapshot.child(Firebase.QUESTIONNAIRES_TABLE_NAME);
                    Stack<String> tempStack = new Stack<>();
                    while (!categoriesStack.isEmpty()) {
                        tempStack.push(categoriesStack.pop());
                    }
                    while (!tempStack.isEmpty()) {
                        if (dataSnapshot.hasChild(tempStack.peek().substring(1))) {
                            dataSnapshot = dataSnapshot.child(tempStack.peek().substring(1));
                        } else {
                            haveAlreadyCategory = false;
                        }
                        categoriesStack.push(tempStack.pop());
                    }

                } else {
                    haveAlreadyCategory = false;
                }

                if (!dataSnapshot.hasChild(nameOfLastCategory))
                    haveAlreadyCategory = false;

                if (!haveAlreadyCategory) {

                    show_or_hide_button(true, confirm_category_selection_Button);

                    questionnaireAlreadyAddedTextView.setVisibility(View.GONE);
                } else {
                    show_or_hide_button(false, confirm_category_selection_Button);
                    showPopUPMessage(UserMessages.Youve_already_compiled_a_questionnaire_for_the_topic+ " " + "\"" + nameOfLastCategory + "\"" +" "+UserMessages.Please_select_another_topic_you_cannot_connect_more_than_one_questionnaire_for_the_same_topic +".");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void showPopUPMessage(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        builder.setMessage(message);

        // add a button
        builder.setPositiveButton(ViewTexts.ok, null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); //create a new one
        layoutParams.weight = 1.0f;
        layoutParams.gravity = Gravity.CENTER; //this is layout_gravity
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setLayoutParams(layoutParams);
    }

    private void selectByPreviousChoice() {
        String nameOfLastCategoryTemp = CachePot.getInstance().pop(NAME_OF_LAST_CATEGORY);
        if (nameOfLastCategoryTemp != null) {

            read_from_cachepot(nameOfLastCategoryTemp);
            show_or_hide_button(true, confirm_category_selection_Button);
            categoriesStack.push(nameOfLastCategory);
            show_or_hide_button(true, category_back_Button);
            setPathTextView(true);
            save_to_cachepot();
        } else {
            cancel_permission_to_next_stages();
        }
    }

    private void cancel_permission_to_next_stages() {
        //How to use https://github.com/kimkevin/CachePot
        CachePot.getInstance().push(2, null);//can't go to filtering
        CachePot.getInstance().push(3, null);//can't go to crete questions

    }

    public void read_from_cachepot(String nameOfLastCategoryTemp) {
        categoryList = CachePot.getInstance().pop(CATEGORY_LIST);
        nameOfLastCategory = nameOfLastCategoryTemp;
        categoriesStack = CachePot.getInstance().pop(CATEGORIES_STACK);
        databaseReference = CachePot.getInstance().pop(DATABASE_REFERENCE);
    }

    public void save_to_cachepot() {
        CachePot.getInstance().push(CATEGORY_LIST, categoryList);
        CachePot.getInstance().push(NAME_OF_LAST_CATEGORY, nameOfLastCategory);
        CachePot.getInstance().push(DATABASE_REFERENCE, databaseReference);
        CachePot.getInstance().push(CATEGORIES_STACK, categoriesStack);
        CachePot.getInstance().push(CATEGORY_PATH_FOR_USER, pathTextView.getText().toString());


    }

    private void goto_filtering_users_fragment() {
        save_to_cachepot();
        CachePot.getInstance().push(2, OK);
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CachePot.getInstance().pop(CATEGORY_LIST);
        CachePot.getInstance().pop(NAME_OF_LAST_CATEGORY);
        CachePot.getInstance().pop(DATABASE_REFERENCE);
        CachePot.getInstance().pop(CATEGORIES_STACK);
        CachePot.getInstance().pop(CATEGORY_PATH_FOR_USER);
    }
}
