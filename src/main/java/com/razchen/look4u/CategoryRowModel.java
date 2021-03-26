package com.razchen.look4u;

import androidx.annotation.NonNull;

public class CategoryRowModel implements Cloneable {
    private String title;
    private boolean chosen;
    private boolean hasSubcategories;
    private boolean currentlyHasSubcategories;


    public CategoryRowModel(String title, boolean hasSubcategories, boolean currentlyHasSubcategories) {
        this.title = title;
        this.chosen = false;
        this.hasSubcategories = hasSubcategories;
        this.currentlyHasSubcategories = currentlyHasSubcategories;

    }

    public String getTitle() {
        return title;
    }


    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public boolean getChosen() {
        return chosen;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public boolean isHasSubcategories() {
        return hasSubcategories;
    }

    public boolean isCurrentlyHasSubcategories() {
        return currentlyHasSubcategories;
    }


    @Override
    public String toString() {
        return title;
    }

    @NonNull
    @Override
    protected CategoryRowModel clone() throws CloneNotSupportedException {
        return (CategoryRowModel) super.clone();
    }
}
