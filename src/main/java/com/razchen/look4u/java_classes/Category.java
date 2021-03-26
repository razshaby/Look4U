package com.razchen.look4u.java_classes;

public class Category implements Comparable<Category> {
    private String name;
    private String addedBy;
    private boolean hasSubcategories;

    public Category() {
    }

    public Category(String name, String addedBy, boolean hasSubcategories) {
        this.name = name;
        this.addedBy = addedBy;
        this.hasSubcategories = hasSubcategories;
    }

    public String getName() {
        return name;
    }

    public String getAddedBy() {
        return addedBy;
    }



    public boolean isHasSubcategories() {
        return hasSubcategories;
    }

    @Override
    public int compareTo(Category category) {
        return this.name.compareTo(category.getName());
    }

    @Override
    public String toString() {
        if(isHasSubcategories())
            return ">"+name;
        else
        return  name;
    }
}
