package com.razchen.look4u

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.razchen.look4u.dl.Firebase
import com.razchen.look4u.dl.Firebase.*
import com.razchen.look4u.java_classes.ServiceNotifications
import com.razchen.look4u.java_classes.User
import com.razchen.look4u.util.Keys
import com.razchen.look4u.util.UserMessages
import com.razchen.look4u.util.UtilFunctions
import com.razchen.look4u.util.ViewTexts
import java.util.*

//Credit https://www.youtube.com/watch?v=nSsoodcMPOU
//https://github.com/atotalks/Firebase-RealTimeChatting-kotlin
class ChatMainActivity : UserMenu(), View.OnClickListener {

    private var chats: String? = null
    private var mAdapter: CustomAdapter? = null

    private var database: FirebaseDatabase? = null
    private var myRef: DatabaseReference? = null

    private var mFMessages: ArrayList<FriendlyMessage>? = null
    private var currentUserID: String? = null
    private var candidateUserID: String? = null

    private var userName: String? = null
    private var userImage: String? = null

    private var mRecyclerView: RecyclerView? = null
    private var msgBtn: ImageButton? = null

    private var msgText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        msgBtn = findViewById(R.id.msgsend)
        msgBtn!!.setOnClickListener(this)
        msgText = findViewById(R.id.msgmessgaeedit)
        mRecyclerView = findViewById(R.id.recyclerview) as RecyclerView
        val mLinearLayoutManager = LinearLayoutManager(this)
        mRecyclerView!!.setLayoutManager(mLinearLayoutManager)
        currentUserID = intent.getStringExtra(Keys.USER_ID);
        candidateUserID = intent.getStringExtra(Keys.USER_CHAT_ID);
        mFMessages = ArrayList()

        // Write a message to the database
        database = FirebaseDatabase.getInstance()
        if (currentUserID!! < candidateUserID!!)
            chats = CHATS + SLASH + currentUserID + SEPARATPOR + candidateUserID + SLASH + MESSAGES;
        else
            chats = CHATS + SLASH + candidateUserID + SEPARATPOR + currentUserID + SLASH + MESSAGES;


        myRef = database!!.getReference(chats!!);

        mHandler = Handler()
        updateFetchMessage();

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        getUserName();
        return true
    }


    private fun getUserName() {
        //Database
        var firebase = Firebase()
        firebase.getUsersTable().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var user = dataSnapshot.child(currentUserID.toString()).getValue(User::class.java)
                userName = user!!.firstName + " " + user!!.lastName;

                userImage = user!!.image


                var chatUser = dataSnapshot.child(candidateUserID.toString()).getValue(User::class.java)
                setUserMenuText_TextView(ViewTexts.chat_with + chatUser!!.firstName + " " + chatUser!!.lastName);

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    fun updateFetchMessage() {
        fetchMessage()
    }

    private var mHandler: Handler? = null


    private fun start_notifications() {
        val intent = Intent(this, ServiceNotifications::class.java)
        intent.putExtra(Keys.USER_ID, currentUserID)
        startService(intent)
    }

    private fun stop_notifications() {
        val intent = Intent(this, ServiceNotifications::class.java)
        intent.putExtra(Keys.USER_ID, currentUserID)
        stopService(intent)
    }

    private fun fetchMessage() {


        database!!.getReference(chats!!).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                updateMessages()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                updateMessages()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }

    private fun updateMessages() {
        database!!.getReference().child(chats!!).addListenerForSingleValueEvent(
                object : ValueEventListener {


                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        mFMessages = ArrayList()

                        for (ds in dataSnapshot.children) {
                            val fromUserId = ds.child("fromUserId").getValue(String::class.java)
                            val name = ds.child("name").getValue(String::class.java)
                            val text = ds.child("text").getValue(String::class.java)
                            val timestamp = ds.child("timeStamp").getValue(Long::class.java)
                            val image = ds.child("userImageUrl").getValue(String::class.java)

                            Log.d("TAG", "$fromUserId / $name / $text / $timestamp")
                            mFMessages!!.add(FriendlyMessage(text, name, fromUserId, image, timestamp))
                        }

                        if (mFMessages!!.size > 0) {

                            mAdapter = CustomAdapter(mFMessages!!, currentUserID!!)
                            mRecyclerView!!.setAdapter(mAdapter)
                            mRecyclerView!!.scrollToPosition(mRecyclerView!!.getAdapter()!!.itemCount - 1)
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("", "getUser:onCancelled", databaseError.toException())
                    }


                })
    }

    private fun subStringName(str: String): String {

        val subString = str.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                ?: return ""

        return subString[0]
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.msgsend -> {


                if (msgText!!.getText() == null || msgText!!.getText().length <= 0) return
                val message = msgText!!.getText().trim()
                if (message.isEmpty()) {
                    UtilFunctions.showUpperToast(UserMessages.cant_send_empty_message, Toast.LENGTH_LONG)
                    return
                }
                val friendlyMessage = FriendlyMessage(
                        message.toString().trim { it <= ' ' },
                        userName,
                        currentUserID, userImage
                )

                cleanEditText()
                myRef!!.push().setValue(friendlyMessage).addOnSuccessListener {
                    // Write was successful!
                    // ...
                    Log.w("", " Write was successful!")


                }.addOnFailureListener {
                    // Write failed
                    // ...
                    Log.d("", " Write was failed!")
                }
                myRef!!.parent!!.child("lastMessage").setValue(message.toString());
                myRef!!.parent!!.child("lastMessageTime").setValue(friendlyMessage.timeStamp.toString());
                myRef!!.parent!!.child("userIDlastMessage").setValue(currentUserID);


            }
        }
    }

    private fun cleanEditText() {

        msgText?.setText("")

    }

    override fun onResume() {
        super.onResume()
        stop_notifications()
    }

    override fun onPause() {
        super.onPause()
        start_notifications()
    }
}