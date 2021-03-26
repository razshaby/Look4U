package com.razchen.look4u.server;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;

public class Finals {
    //public static final String IP = "192.168.141.1";
   public static String IP = "";
    public static final int PORT=8036;

    public static void setIPFromDB() {

        DatabaseReference databaseReferenceServerPort = new Firebase().getServerPort();
        databaseReferenceServerPort.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                IP=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static void createIPInDatabase(String ip) {
        DatabaseReference databaseReferenceServerPort = new Firebase().getServerPort();
        databaseReferenceServerPort.setValue(ip);    }
}
