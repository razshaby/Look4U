package com.razchen.look4u;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.util.UserMessages;
import com.razchen.look4u.util.UtilFunctions;

import java.util.Locale;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;

import static com.razchen.look4u.util.Keys.ID;
import static com.razchen.look4u.util.Keys.SHARED_PREFS;
import static com.razchen.look4u.util.Keys.USER_ID;


public class UserMenu extends AppCompatActivity {


    protected TextView userMenu_TextView;
    protected ActionBarRtlizer userMenu_actionBarRtlizer;
    protected static String userID;

    public static void setId(String userID) {
        UserMenu.userID = userID;
    }

    public void config() {
        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale("he"));
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }

    ////  Menu //// >>>  ////>>>  Menu ////>>>      ////>>>  Menu ////>>      ////>>  Menu ////
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


//credit https://stackoverflow.com/questions/19750635/icon-in-menu-not-showing-in-android
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        Log.d("Menu", getClass().getSimpleName());
        setTitleLogoAndHelp();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    private void setTitleLogoAndHelp() {
        userMenu_TextView = new TextView(this);
        userMenu_TextView.setTextSize(18);
        userMenu_TextView.setTextColor(Color.rgb(255, 255, 255));
        userMenu_actionBarRtlizer = new ActionBarRtlizer(this);

        ViewGroup actionBarView = userMenu_actionBarRtlizer.getActionBarView();
        ViewGroup homeView = (ViewGroup) userMenu_actionBarRtlizer.getHomeView();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        ImageView imageView_logo = new ImageView(this);
        imageView_logo.setImageResource(R.drawable.look4u_logo);

        ImageView imageView_help = new ImageView(this);
        imageView_help.setImageResource(R.drawable.help_menu);

        RelativeLayout.LayoutParams paramsFor_userMenu_TextView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams paramsForImageView_help = new RelativeLayout.LayoutParams(100, 100);
        RelativeLayout.LayoutParams paramsForImageView_Logo = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams paramsForSpace = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsForSpace.addRule(RelativeLayout.CENTER_IN_PARENT);

        boolean imageSide = false;
        if (getClass().getSimpleName().equals(MainActivity.class.getSimpleName())) {
            userMenu_TextView.setText(R.string.home);
            paramsForImageView_help.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            paramsForImageView_help.addRule(RelativeLayout.CENTER_VERTICAL);
            paramsForImageView_Logo.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            paramsForImageView_Logo.addRule(RelativeLayout.CENTER_VERTICAL);
            imageView_logo.setLayoutParams(paramsForImageView_Logo);
            final PopUpInfo popUpInfo = new PopUpInfo();
            imageView_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popUpInfo.showPopupWindow(v, getString(R.string.help), getString(R.string.help_description));

                }
            });
            imageSide = true;
        } else if (getClass().getSimpleName().equals(PersonalInformationActivity.class.getSimpleName()))
            userMenu_TextView.setText(R.string.Personal_Information);
        else if (getClass().getSimpleName().equals(FillOutQuestionnairesActivity.class.getSimpleName()) ||
                getClass().getSimpleName().equals(FillOutQuestionnairesChooseAdvertiserDetails.class.getSimpleName()) ||
                getClass().getSimpleName().equals(FillOutQuestionnaires_ShowMatches.class.getSimpleName()))
            userMenu_TextView.setText(R.string.fill_out_questionnaires);
        else if (getClass().getSimpleName().equals(CreateQuestionnaire_main.class.getSimpleName()))
            userMenu_TextView.setText(R.string.advertiseQuestionnaire);
        else if (getClass().getSimpleName().equals(MyMatchesActivity.class.getSimpleName()))
            userMenu_TextView.setText(R.string.matches);
        else if (getClass().getSimpleName().equals(MyChatsActivity.class.getSimpleName()))
            userMenu_TextView.setText(R.string.chats);
        paramsFor_userMenu_TextView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        userMenu_TextView.setLayoutParams(paramsFor_userMenu_TextView);
        if (imageSide == false) {
            relativeLayout.addView(userMenu_TextView, paramsFor_userMenu_TextView);
            paramsForImageView_Logo.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            imageView_help.setLayoutParams(paramsForImageView_help);
            relativeLayout.addView(imageView_help);
        }
        imageView_logo.setLayoutParams(paramsForImageView_Logo);
        relativeLayout.addView(imageView_logo);
        actionBarView.addView(relativeLayout);
        userMenu_actionBarRtlizer.flipActionBarUpIconIfAvailable(homeView);
        RtlizeEverything.rtlize(actionBarView);
        RtlizeEverything.rtlize(homeView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkInternet() == false) {
            showPopupWindowWithOk("בדוק את החיבור לאינטרנט כדי להמשיך להשתמש באפליקציה");
        }
    }

    public void showPopupWindowWithOk(final String info) {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(info)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (checkInternet() == true) {
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.help:
                help();
                return true;
            case R.id.personal_information:
                showUserDetails();
                return true;

            case R.id.answering_questionnaires:
                checkIfCategoriesExist();
                return true;

            case R.id.advertising_Questionnaires:
                advertising_Questionnaires();
                return true;

            case R.id.matches:
                checkIfThereAreMatches();

                return true;
            case R.id.chats:
                checkIfExistChats();

                return true;
            case R.id.logout:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    protected void chats() {
        Intent intent = new Intent(this, MyChatsActivity.class);
        Bundle inBundle = getIntent().getExtras();
        intent.putExtra(USER_ID, inBundle.get(USER_ID).toString());
        startActivity(intent);
        callFinish();
    }

    protected void help() {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle inBundle = getIntent().getExtras();
        intent.putExtra(USER_ID, inBundle.get(USER_ID).toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        callFinish();
    }

    private void callFinish() {
        if (!this.getClass().getSimpleName().equals(MainActivity.class.getSimpleName())) {
            finish();
        }
    }


    protected void checkIfThereAreMatches() {

        new Firebase().getUsersTable().child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(Firebase.CANDIDATES)) {
                    checkIfQuestionnaresExist();
                } else
                    matches();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void checkIfCategoriesExist() {
        new Firebase().getCategoriesTable().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren())
                    UtilFunctions.showUpperToast(UserMessages.No_questionnaires, Toast.LENGTH_LONG);
                else
                    fill_Out_Questionnaires();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkIfQuestionnaresExist() {
        new Firebase().getUsersTable().child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(Firebase.QUESTIONNAIRES_TABLE_NAME)) {
                    UtilFunctions.showUpperToast(UserMessages.No_matches, Toast.LENGTH_LONG);
                } else
                    UtilFunctions.showUpperToast(UserMessages.No_matches_because_no_one_answered, Toast.LENGTH_LONG);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    protected void checkIfExistChats() {

        new Firebase().getUserchatsids().child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {

                    new Firebase().getUsersTable().child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(Firebase.CANDIDATES)) {
                                UtilFunctions.showUpperToast(getString(R.string.noChatsExist), Toast.LENGTH_LONG);

                            } else
                                UtilFunctions.showUpperToast(UserMessages.No_chats_yet_chats_must_be_opened_through_the_adjustments_screen, Toast.LENGTH_LONG);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else
                    chats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected boolean checkInternet() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            return false;

        } else {
            return true;
        }
    }

    protected void matches() {
        Intent intent = new Intent(this, MyMatchesActivity.class);
        Bundle inBundle = getIntent().getExtras();
        intent.putExtra(USER_ID, inBundle.get(USER_ID).toString());
        startActivity(intent);
        callFinish();
    }

    protected void advertising_Questionnaires() {
        help();
        Intent intent = new Intent(this, CreateQuestionnaire_main.class);
        Bundle inBundle = getIntent().getExtras();
        intent.putExtra(USER_ID, inBundle.get(USER_ID).toString());
        startActivity(intent);
    }

    protected void showUserDetails() {
        Intent intent = new Intent(this, PersonalInformationActivity.class);
        Bundle inBundle = getIntent().getExtras();
        intent.putExtra(USER_ID, inBundle.get(USER_ID).toString());
        startActivity(intent);
        callFinish();
    }

    protected void logOut() {
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, WelcomeActivity.class);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(ID, "");

        editor.apply();
        //Credit https://stackoverflow.com/questions/6330260/finish-all-previous-activities
        finishAffinity();
        startActivity(intent);
    }

    public void setUserMenuText_TextView(String text) {
        this.userMenu_TextView.setText(text);
    }
    protected void fill_Out_Questionnaires() {
        Intent intent = new Intent(this, FillOutQuestionnairesActivity.class);
        Bundle inBundle = getIntent().getExtras();

        intent.putExtra(USER_ID, inBundle.get(USER_ID).toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        callFinish();

    }
    ////<<<  Menu ////<<<  ////<<<   Menu ////<<<   ////  Menu ////<<<   ////<<<   Menu ////<<<
}
