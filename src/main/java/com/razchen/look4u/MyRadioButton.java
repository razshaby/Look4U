package com.razchen.look4u;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


// TODO: If you are using androidx
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;

import com.razchen.look4u.util.UtilFunctions;

// TODO: If you are using appcompat
//import android.support.annotation.Nullable;
//import android.support.v7.widget.AppCompatRadioButton;

public class MyRadioButton extends AppCompatRadioButton {

    private View view;
    private TextView textView;
    private ImageView imageView;
    private Button btn;

    public MyRadioButton(Context context) {
        super(context);
        init(context);
    }

    public MyRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }






    // setText is a final method in ancestor, so we must take another name.
    public void setTextWith(int resId) {
        textView.setText(resId);
        redrawLayout();
    }

    public void setTextWith(CharSequence text) {
        textView.setText(text);
        redrawLayout();
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.my_radio_button_content, null);
        textView = view.findViewById(R.id.textView);
        btn=view.findViewById(R.id.btn);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilFunctions.showUpperToast("dfd", Toast.LENGTH_SHORT);
            }
        });
        imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilFunctions.showUpperToast("dfd", Toast.LENGTH_SHORT);
            }
        });
        redrawLayout();
    }

    private void redrawLayout() {
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(getResources(), bitmap), null, null, null);
        view.setDrawingCacheEnabled(false);
    }

    private int dp2px(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

}
