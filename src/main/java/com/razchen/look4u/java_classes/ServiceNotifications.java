package com.razchen.look4u.java_classes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.MyChatsActivity;
import com.razchen.look4u.R;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.util.UserMessages;

import java.util.Date;

import static com.razchen.look4u.util.Keys.USER_ID;

public class ServiceNotifications extends Service {
    private DatabaseReference userchatidDBRef;
    private DatabaseReference userchatDBRef;
    private boolean canNotify = true;
    public static String CHANNEL_ID = "my_channel_id";
    private NotificationManagerCompat notificationManager;
    private String userID = "";
    private NotificationCompat.Builder builder;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        canNotify = true;
        userID = (String) intent.getExtras().get(USER_ID);
        userchatidDBRef = new Firebase().getUserchatsids().child(userID);
        userchatDBRef = new Firebase().getChatsReference();
        notificationManager = NotificationManagerCompat.from(this);
        searchForChats();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        canNotify = false;
    }


    private void searchForChats() {


        userchatidDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                    userchatDBRef.child(snapshot1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            final DatabaseReference databaseReferenceTest = snapshot.getRef();
                            ChildEventListener childEventListener = new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot_chat, @Nullable String s) {
                                }

                                @Override
                                public void onChildChanged(@NonNull final DataSnapshot dataSnapshot_chat, @Nullable String s) {

                                    sendNotification(snapshot1, dataSnapshot_chat);

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            };
                            databaseReferenceTest.addChildEventListener(childEventListener);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    ;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void sendNotification(final DataSnapshot snapshot1, final DataSnapshot dataSnapshot_chat) {
        final String fromUserID = snapshot1.getKey().replace(userID, "").replace("_", "");
        new Firebase().getUsersTable().child(fromUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                builder = new NotificationCompat.Builder(ServiceNotifications.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_icon)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                User user = dataSnapshot.getValue(User.class);
                builder.setContentTitle(UserMessages.New_message_from + " " + user.getFirstName() + " " + user.getLastName());
                if (dataSnapshot_chat.getKey().equals(Firebase.LASTMESSAGE)) {
                    builder.setContentText(dataSnapshot_chat.getValue(String.class));
                    setOnNotificationPress();
                    String fromUserIDShort;
                    if (fromUserID.length() > 4) {
                        fromUserIDShort = fromUserID.substring(0, 4);
                    } else
                        fromUserIDShort = fromUserID;
                    if (canNotify)
                        notificationManager.notify((int) (new Date().getTime() / 1000) + Integer.parseInt(fromUserIDShort), builder.build());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setOnNotificationPress() {
        Intent resultIntent = new Intent(ServiceNotifications.this, MyChatsActivity.class);
        resultIntent.putExtra(USER_ID, userID);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ServiceNotifications.this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(ServiceNotifications.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
