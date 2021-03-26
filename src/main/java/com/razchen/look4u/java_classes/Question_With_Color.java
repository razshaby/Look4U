package com.razchen.look4u.java_classes;

import android.graphics.Color;

public class Question_With_Color extends  QuestionRow{
    private  int color;

    public Question_With_Color() {
        super();
        color= Color.BLACK;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


}
