package com.razchen.look4u;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.java_classes.ServiceNotifications;
import com.razchen.look4u.java_classes.UserChat;
import com.razchen.look4u.util.UtilFunctions;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import static com.razchen.look4u.util.Keys.USER_CHAT_ID;
import static com.razchen.look4u.util.Keys.USER_ID;

public class MyChatsActivity extends UserMenu {

    private ListView listView;


    private String userID = "";
    private SpinKitView spinKit;

    private DatabaseReference userchatidDBRef;
    private DatabaseReference userchatDBRef;
    private ArrayList<UserChat> userChatArrayList = new ArrayList<>();

    private TextView no_chats_foundTextView;
    private long currentChatNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chats_activity);

        currentChatNumber = 0;
        Bundle inBundle = getIntent().getExtras();
        userID = inBundle.get(USER_ID).toString();
        listView = findViewById(R.id.MyChats_ListView);
        userchatidDBRef = new Firebase().getUserchatsids().child(userID);
        userchatDBRef = new Firebase().getChatsReference();
        no_chats_foundTextView = findViewById(R.id.no_chats_foundTextView);
        spinKit = findViewById(R.id.spinKit);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                openChatWithUser(userChatArrayList.get(position).getId());
            }
        });

        searchForChats();
        stop_notifications();
    }

    protected void openChatWithUser(String id) {
        Intent intent = new Intent(MyChatsActivity.this, ChatMainActivity.class);
        intent.putExtra(USER_CHAT_ID, id);
        intent.putExtra(USER_ID, userID);
        startActivity(intent);
    }


    private void searchForChats() {

        userchatidDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentChatNumber = 0;
                userChatArrayList.clear();
                final long countOfChats = dataSnapshot.getChildrenCount();
                if (countOfChats == 0) {
                    noChatsYetFunction();
                    return;
                }
                for (final DataSnapshot snapshot1 : dataSnapshot.getChildren()) {

                    userchatDBRef.child(snapshot1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String chatID = snapshot.getKey();
                            String[] s = chatID.split("_");

                            String lastMessage;
                            Long lastMessageTime;
                            String userIDlastMessage;
                            if (snapshot.child("lastMessageTime").exists()) {
                                lastMessage = snapshot.child("lastMessage").getValue(String.class);
                                if (lastMessage.trim().isEmpty()) {
                                    userchatDBRef.child(snapshot1.getKey()).child("messages").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                                String texts1 = s.child("text").getValue(String.class);
                                                userchatDBRef.child(snapshot1.getKey()).child("lastMessage").setValue(texts1);
                                                searchForChats();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    return;
                                }
                                lastMessageTime = Long.parseLong(snapshot.child("lastMessageTime").getValue(String.class));
                                userIDlastMessage = snapshot.child("userIDlastMessage").getValue(String.class);
                            } else {
                                lastMessage = "";
                                lastMessageTime = 0l;
                                userIDlastMessage = "None";
                            }

                            final DatabaseReference databaseReferenceTest = snapshot.getRef();
                            ChildEventListener childEventListener = new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String s) {
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    databaseReferenceTest.removeEventListener(this);
                                    searchForChats();

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


                            if (s[1].trim().equals(userID))
                                userChatArrayList.add(new UserChat(s[0], chatID, lastMessage, lastMessageTime, userIDlastMessage));
                            else
                                userChatArrayList.add(new UserChat(s[1], chatID, lastMessage, lastMessageTime, userIDlastMessage));
                            currentChatNumber++;

                            if (currentChatNumber == countOfChats)
                                setUserChatAttributesInTheArray();
                            spinKit.setVisibility(View.GONE);

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


    private void setUserChatAttributesInTheArray() {

        int currentChat = 0;


        for (int i = 0; i < userChatArrayList.size(); i++) {
            currentChat++;
            final UserChat userChat = userChatArrayList.get(i);
            final int currentChatFinal = currentChat;
            DatabaseReference userDBRef = new Firebase().getUsersTable().child(userChat.getId());
            userDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int day = Integer.parseInt(dataSnapshot.child("birthday/day").getValue().toString());
                    int month = Integer.parseInt(dataSnapshot.child("birthday/month").getValue().toString());
                    int year = Integer.parseInt(dataSnapshot.child("birthday/year").getValue().toString());
                    userChat.setAge(Functions.getAge(year, month, day));

                    String fname = dataSnapshot.child("firstName").getValue().toString();
                    String lname = dataSnapshot.child("lastName").getValue().toString();
                    userChat.setFullName(fname + " " + lname);
                    userChat.setImageUrl(dataSnapshot.child("image").getValue().toString());
                    userChat.setGender(dataSnapshot.child(Firebase.GENDER).getValue().toString());

                    if (currentChatFinal == userChatArrayList.size())
                        showUserChats();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    private void noChatsYetFunction() {
        no_chats_foundTextView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }


    private void showUserChats() {
        ArrayList<String> arrayStrings = new ArrayList<>();
        for (int i = 0; i < userChatArrayList.size(); i++)
            arrayStrings.add(userChatArrayList.get(i).getFullName() + "");
        Collections.sort(userChatArrayList);
        UserChatsAdapter adapter = new UserChatsAdapter(MyChatsActivity.this, userChatArrayList, arrayStrings);
        listView.setAdapter(adapter);

    }

    private void stop_notifications() {
        Intent intent = new Intent(this, ServiceNotifications.class);
        intent.putExtra(USER_ID, userID);
        stopService(intent);
    }

    private void start_notifications() {
        Intent intent = new Intent(this, ServiceNotifications.class);
        intent.putExtra(USER_ID, userID);
        startService(intent);
    }

    class UserChatsAdapter extends ArrayAdapter<String> {

        Context context;

        private ArrayList<UserChat> userChatArrayList;

        UserChatsAdapter(Context c, ArrayList<UserChat> userChatArrayList, ArrayList<String> stringArr) {
            super(c, R.layout.chat_row, R.id.userName_TextView, stringArr);
            this.userChatArrayList = userChatArrayList;
        }

        @NonNull
        @Override
        public View getView(final int position, @androidx.annotation.Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.chat_row, parent, false);

            TextView userName_TextView = row.findViewById(R.id.userName_TextView);

            String fullName = userChatArrayList.get(position).getFullName();

            if (fullName.length() > 10) {
                fullName = fullName.substring(0, 10) + "...";
            }
            userName_TextView.setText(fullName);

            TextView userDescription_TextView = row.findViewById(R.id.userDescription_TextView);
            userDescription_TextView.setText(userChatArrayList.get(position).getAge() + " " + UtilFunctions.convertGenderChoiceToHebrew(userChatArrayList.get(position).getGender()));

            Long lastMessageTime = userChatArrayList.get(position).getLastMessageTime();
            TextView lastMessage_TextView = row.findViewById(R.id.lastMessage_TextView);


            if (lastMessageTime != 0) {
                TextView lastMessageTime_TextView = row.findViewById(R.id.lastMessageTime_TextView);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm ");
                String time = simpleDateFormat.format(lastMessageTime);
                lastMessageTime_TextView.setText(time);

                String lastMessage = userChatArrayList.get(position).getLastMessage().trim();
                if (lastMessage.length() > 20) {
                    lastMessage = lastMessage.substring(0, 15) + "...";
                }
                lastMessage_TextView.setText(lastMessage);
                if (userID.trim().equals(userChatArrayList.get(position).getUserIDlastMessage().trim())) {
                    lastMessage_TextView.setTextColor(Color.parseColor("#FFFFFF"));
                    lastMessage_TextView.setBackgroundResource(R.drawable.my_message);
                } else {
                    lastMessage_TextView.setTextColor(Color.parseColor("#000000"));
                    lastMessage_TextView.setBackgroundResource(R.drawable.their_message);
                }
            } else {
                lastMessage_TextView.setText(R.string.no_massages_yet_press_to_send);
                lastMessage_TextView.setTextSize(16);
                lastMessage_TextView.setTextColor(Color.parseColor("#000000"));
            }


            ImageView images = row.findViewById(R.id.user_ImageView);

            Picasso.with(MyChatsActivity.this).load(userChatArrayList.get(position).getImageUrl()).into(images);


            return row;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        start_notifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stop_notifications();
    }

    @Override
    protected void onPause() {
        super.onPause();
        start_notifications();
    }
}
