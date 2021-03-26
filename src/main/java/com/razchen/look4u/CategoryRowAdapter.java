package com.razchen.look4u;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.util.UserMessages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// credit for https://www.youtube.com/watch?v=caUfVkjlI7I
public class CategoryRowAdapter extends RecyclerView.Adapter<CategoryRowAdapter.Viewholder> {
    private List<CategoryRowModel> categoryRowModelList;
    private Firebase firebase;
    private boolean backButtonPressed = false;
    private Stack<CategoryRowModel> categoryRowModelStack = new Stack<>();
    private FillOutQuestionnairesActivity fillOutQuestionnairesActivity;
    private DatabaseReference databaseReference;
    private ValueEventListener childEventListener;
    private int lastPositionClicked = -1;
    private boolean continueBtFlag = false;
    private boolean lastChoseHasSubCategories = false;
    private boolean stillNotChooseCategory = true;
    private boolean hasSubcategories = false;

    public CategoryRowAdapter(FillOutQuestionnairesActivity fillOutQuestionnairesActivity) {
        this.categoryRowModelList = new ArrayList<>();
        this.fillOutQuestionnairesActivity = fillOutQuestionnairesActivity;
        firebase = new Firebase();
        databaseReference = firebase.getCategoriesTable();
        onDatabaseChanged();
    }

    public void setAllRowModelToBlackColor() {
        for (int i = 0; i < categoryRowModelList.size(); i++)
            categoryRowModelList.get(i).setChosen(false);
    }


    public void readFromFirebase() {
        categoryRowModelList.clear();
        lastPositionClicked = -1;
        if (backButtonPressed) {
            backButtonPressed = false;
            if (!categoryRowModelStack.peek().isHasSubcategories()) {
                categoryRowModelStack.pop();
                databaseReference = databaseReference.getParent().getParent();
            }
            databaseReference = databaseReference.getParent().getParent();
            categoryRowModelStack.pop();
        }


        String categoryStr = categoryRowModelStack.isEmpty() ? "" : categoryRowModelStack.peek().getTitle();
        if (categoryStr.equals(""))
            databaseReference = firebase.getCategoriesTable();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String categoriesChild = Firebase.CATEGORIES_TABLE_NAME;
                hasSubcategories = false;
                stillNotChooseCategory = databaseReference.getRoot().equals(databaseReference.getParent());
                if (stillNotChooseCategory) {
                    hasSubcategories = true;
                    categoriesChild = "";
                    fillOutQuestionnairesActivity.getBackButton().setAlpha(0);
                    fillOutQuestionnairesActivity.getBackButton().setEnabled(false);
                } else
                    hasSubcategories = dataSnapshot.hasChild(Firebase.CATEGORIES_TABLE_NAME);

                if (!hasSubcategories)
                    return;

                for (DataSnapshot postSnapshot : dataSnapshot.child(categoriesChild).getChildren()) {
                    hasSubcategories = postSnapshot.child(Firebase.HAS_SUB_CATEGORIES).getValue(Boolean.class);
                    boolean currentlyHasSubcategories = postSnapshot.hasChild(Firebase.CATEGORIES_TABLE_NAME);
                    CategoryRowModel categoryRowModel = new CategoryRowModel(postSnapshot.getKey(), hasSubcategories, currentlyHasSubcategories);
                    categoryRowModelList.add(categoryRowModel);
                }

                Collections.sort(categoryRowModelList, new Comparator<CategoryRowModel>() {
                    @Override
                    public int compare(CategoryRowModel categoryRowModel, CategoryRowModel t1) {
                        return categoryRowModel.getTitle().compareTo(t1.getTitle());
                    }
                });

                notifyDataSetChanged();
                updateOuterClass();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void onDatabaseChanged() {
        childEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readFromFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {
        String localTitle = "";
        if (categoryRowModelList.get(position).isHasSubcategories())
            localTitle = UserMessages.Smaller_than;
        localTitle += categoryRowModelList.get(position).getTitle();

        if (categoryRowModelList.get(position).getChosen() && !categoryRowModelList.get(position).isHasSubcategories()) {
            continueBtFlag = true;
        } else if (!continueBtFlag) {
            fillOutQuestionnairesActivity.getContinueButton().setAlpha(0);
            fillOutQuestionnairesActivity.getContinueButton().setEnabled(false);
        }

        if (position == categoryRowModelList.size() - 1)
            if (continueBtFlag)
                continueBtFlag = false;

        holder.setData(localTitle);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryRowModelList.get(position).isHasSubcategories() && !categoryRowModelList.get(position).isCurrentlyHasSubcategories()) {
                    Toast.makeText(fillOutQuestionnairesActivity, UserMessages.No_subcategories_have_been_added_to_this_category, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (lastPositionClicked == position) {
                    return;
                }
                lastPositionClicked = position;
                if (categoryRowModelList.size() == 0) {
                    return;
                }
                if (!categoryRowModelStack.isEmpty()) {
                    if (!categoryRowModelStack.peek().isHasSubcategories())
                        lastChoseHasSubCategories = false;
                    else
                        lastChoseHasSubCategories = true;
                    if (!lastChoseHasSubCategories) {
                        categoryRowModelStack.pop();
                    }
                }

                categoryRowModelStack.push(categoryRowModelList.get(position));
                stillNotChooseCategory = false;
                if (categoryRowModelStack.size() > 1 && !lastChoseHasSubCategories) {
                    databaseReference = databaseReference.getParent().getParent();
                    databaseReference = databaseReference.child(Firebase.CATEGORIES_TABLE_NAME).child(categoryRowModelStack.peek().getTitle());

                } else if (categoryRowModelStack.size() > 1)
                    databaseReference = databaseReference.child(Firebase.CATEGORIES_TABLE_NAME).child(categoryRowModelStack.peek().getTitle());
                else {
                    if (!lastChoseHasSubCategories && !databaseReference.getParent().equals(databaseReference.getRoot()))
                        databaseReference = databaseReference.getParent();
                    databaseReference = databaseReference.child(categoryRowModelStack.peek().getTitle());
                }

                if (categoryRowModelList.get(position).isHasSubcategories()) {
                    fillOutQuestionnairesActivity.getBackButton().setAlpha(1);
                    fillOutQuestionnairesActivity.getBackButton().setEnabled(true);
                }
                CategoryRowModel chosenCategoryRowModel = categoryRowModelList.get(position);
                chosenCategoryRowModel.setChosen(true);

                if (!chosenCategoryRowModel.isHasSubcategories()) {
                    notifyDataSetChanged();
                    fillOutQuestionnairesActivity.getContinueButton().setAlpha(1);
                    fillOutQuestionnairesActivity.getContinueButton().setEnabled(true);
                    updateOuterClass();
                    continueBtFlag = true;
                    return;
                }

                fixTheCurrentPath();
                updateOuterClass();
            }
        });

    }


    public Stack<CategoryRowModel> getStack() {
        return categoryRowModelStack;
    }

    private void updateOuterClass() {
        this.fillOutQuestionnairesActivity.showUserPath(categoryRowModelStack);
        fillOutQuestionnairesActivity.hideSpinKit();
    }


    public void fixTheCurrentPath() {
        databaseReference.removeEventListener(childEventListener);
        onDatabaseChanged();
    }


    @Override
    public int getItemCount() {
        return categoryRowModelList.size();
    }

    public void setBackButtonPressed(boolean backButtonPressed) {
        this.backButtonPressed = backButtonPressed;
    }

    class Viewholder extends RecyclerView.ViewHolder {
        private TextView title;
        private LinearLayout linearLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView1);
            linearLayout = itemView.findViewById(R.id.innerLayout);

        }

        private void setData(String titleText) {
            title.setText(titleText);
        }


    }

}
