package com.razchen.look4u;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.util.Keys;
import com.razchen.look4u.util.UserMessages;
import com.razchen.look4u.util.UtilFunctions;
import com.razchen.look4u.util.ViewTexts;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.razchen.look4u.util.Keys.CATEGORY_CHOICES_ARR;
import static com.razchen.look4u.util.Keys.USER_ID;

public class FillOutQuestionnairesActivity extends UserMenu {
    private RecyclerView recylerView;
    private CategoryRowAdapter categoryRowAdapter;
    private Button backButton;
    private Button continueButton;
    private String userID = "";
    private ArrayList<String> categoryChoicesArr = new ArrayList<>();
    private String globalPath;
    private SpinKitView spinKit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_out_questionnaires);
        config();

        Bundle inBundle = getIntent().getExtras();
        userID = inBundle.get(USER_ID).toString();


        spinKit = findViewById(R.id.spinKit);
        recylerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);
        backButton.setText(UserMessages.BACK_STR);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recylerView.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recylerView.addItemDecoration(itemDecoration);
        categoryRowAdapter = new CategoryRowAdapter(this);
        recylerView.setAdapter(categoryRowAdapter);
        continueButton = findViewById(R.id.continueButton);
        continueButton.setAlpha(0);
        continueButton.setEnabled(false);
        backButton.setAlpha(0);
        backButton.setEnabled(false);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assignCategoryChoicesArr();
                checkIfUserFilledGenderAndDateOfBirth();

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backBTPressed();
            }
        });
    }


    public void hideSpinKit() {
        spinKit.setVisibility(View.GONE);
    }

    private void assignCategoryChoicesArr() {
        categoryChoicesArr = new ArrayList<>();
        Stack<CategoryRowModel> stack = (Stack<CategoryRowModel>) categoryRowAdapter.getStack().clone();
        Stack<CategoryRowModel> FIFO = new Stack<>();
        int numOfIterations = stack.size();
        for (int i = 0; i < numOfIterations; i++) {
            FIFO.push(stack.pop());
        }
        numOfIterations = FIFO.size();

        for (int i = 0; i < numOfIterations; i++) {
            categoryChoicesArr.add(FIFO.pop().getTitle());
        }
    }


    private void checkIfUserFilledGenderAndDateOfBirth() {
        DatabaseReference databaseReference = new Firebase().getUsersTable().child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(Firebase.BIRTHDAY) || !dataSnapshot.hasChild(Firebase.GENDER)
                        || dataSnapshot.child(Firebase.GENDER).getValue().toString().trim().equals("")
                ) {
                    PopUpFillAgeAndGender popUpFillAgeAndGender = new PopUpFillAgeAndGender();
                    popUpFillAgeAndGender.showPopupWindow(findViewById(android.R.id.content).getRootView(), !dataSnapshot.hasChild(Firebase.GENDER), !dataSnapshot.hasChild(Firebase.BIRTHDAY), userID);

                    return;
                }

                Intent intent = new Intent(FillOutQuestionnairesActivity.this, FillOutQuestionnairesChooseAdvertiserDetails.class);
                intent.putStringArrayListExtra(CATEGORY_CHOICES_ARR, categoryChoicesArr);
                intent.putExtra(USER_ID, userID);
                intent.putExtra(Keys.GLOBALPATH, globalPath);


                startActivity(intent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (categoryRowAdapter.getDatabaseReference().getParent().equals(categoryRowAdapter.getDatabaseReference().getRoot()))
            finish();
        else if (categoryRowAdapter.getStack().size() == 1 && !categoryRowAdapter.getStack().peek().isHasSubcategories())
            finish();
        else
            backBTPressed();

    }

    private void backBTPressed() {
        if (categoryRowAdapter.getStack().size() == 1) {
            backButton.setAlpha(0);
            backButton.setEnabled(false);
        }
        categoryRowAdapter.setBackButtonPressed(true);
        categoryRowAdapter.setAllRowModelToBlackColor();
        categoryRowAdapter.fixTheCurrentPath();
        showUserPath(categoryRowAdapter.getStack());
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getContinueButton() {
        return continueButton;
    }


    public void showUserPath(Stack pathStack) {
        TextView userPath = findViewById(R.id.fillOutPathTextView);
        String path;
        if (pathStack.isEmpty())
            path = "";
        else {
            path = pathStack.toString();
            path = path.substring(1, path.length() - 1).replace(",", "->");
        }
        userPath.setText(path);
        globalPath = path;
    }

}
