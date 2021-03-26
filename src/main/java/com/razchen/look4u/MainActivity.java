package com.razchen.look4u;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.onesignal.OneSignal;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.java_classes.MyDate;
import com.razchen.look4u.java_classes.ServiceNotifications;
import com.razchen.look4u.java_classes.User;
import com.razchen.look4u.server.Finals;
import com.razchen.look4u.util.Keys;

import static com.razchen.look4u.util.Keys.USER_ID;

public class MainActivity extends UserMenu {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        config();
        String userID = getIntent().getExtras().get(USER_ID).toString();
        UserMenu.setId(userID);
        OneSignal.sendTag(USER_ID, this.userID);

        handleOnClickEvents();
        start_notifications();

    }

    private void start_notifications() {
        Intent intent = new Intent(this, ServiceNotifications.class);
        intent.putExtra(USER_ID, userID);
        startService(intent);
    }


    private void handleOnClickEvents() {
        final PopUpInfo popUpInfo = new PopUpInfo();

        findViewById(R.id.answerQuestionLinearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfCategoriesExist();
            }
        });

        findViewById(R.id.answerQuestionLinearLayout).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popUpInfo.showPopupWindow(v, getString(R.string.fill_out_questionnaires), getString(R.string.fill_out_questionnaires_description));
                return true;
            }
        });

        findViewById(R.id.advertiseLinearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advertising_Questionnaires();
            }
        });
        findViewById(R.id.advertiseLinearLayout).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popUpInfo.showPopupWindow(v, getString(R.string.create_questionnaires), getString(R.string.advertise_questionnaires_description));
                return true;
            }
        });


        findViewById(R.id.chatsLinearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfExistChats();
            }
        });
        findViewById(R.id.chatsLinearLayout).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popUpInfo.showPopupWindow(v, getString(R.string.chats), getString(R.string.chats_description));
                return true;
            }
        });

        findViewById(R.id.matchesLinearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfThereAreMatches();
            }
        });
        findViewById(R.id.matchesLinearLayout).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popUpInfo.showPopupWindow(v, getString(R.string.matches), getString(R.string.matches_description));
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Finals.setIPFromDB();
    }


    private void createUsers(int numOfUsers) {
        for (int i = 0; i < numOfUsers; i++) {
            User user = new User("2000000" + i, "firstName" + i, "lastName" + i, "https://publicdomainvectors.org/photos/Male-Head-Profile-Silhouette.png", "email@test" + i, "male", new MyDate(1, 1, 1990));
            DatabaseReference usersdatabaseReference = new Firebase().getUsersTable();
            usersdatabaseReference.child("2000000" + i).setValue(user);
        }
    }

}
