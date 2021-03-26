package com.razchen.look4u.java_classes;

public class QuestionRow extends Question {
    private boolean selected;
    public QuestionRow() {
        super();
        selected=false;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
