package com.razchen.look4u.util;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.razchen.look4u.R;

import io.grpc.Context;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UtilFunctions {
    public static final String male = "זכר";
    public static final String female = "נקבה";
    public static final String not_relevant = "לא רלוונטי";



    static public String convertGenderChoiceToEnglish(String gender)
    {
        switch(gender) {
            case male:
                gender="male";
                break;
            case female:
                gender="female";
                break;
            default:
                gender="not relevant";
        }
        return gender;
    }

    static public String convertGenderChoiceToHebrew(String gender)
    {
        switch(gender) {
            case "male":
                gender=male;
                break;
            case "female":
                gender=female;
                break;
            default:
                gender=not_relevant;
        }
        return gender;
    }

    static public void showUpperToast(String message, int length)
    {
        Toast toast = Toast.makeText(getApplicationContext(),message,length);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 150);
        View view = toast.getView();
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.rgb(0,0,0));
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(25);

       // int backgroundColor = ResourcesCompat.getColor(toast.getView().getResources(), R.color.colorPrimary, null);
        toast.getView().getBackground().setColorFilter(Color.rgb(224,224,224), PorterDuff.Mode.SRC_IN);
        //To change the Background of Toast
        //view.setBackgroundColor(Color.rgb(178,255,102));

        toast.show();
    }
    static public  void show_or_hide_button(boolean show, Button btn)
    {

        if(show)
        {
            btn.setAlpha(1);
            btn.setEnabled(true);
        }
        else
        {
            btn.setAlpha(0);
            btn.setEnabled(false);
        }

    }
}
