package com.razchen.look4u;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.razchen.look4u.dl.Firebase;
import com.razchen.look4u.java_classes.User;
import com.razchen.look4u.util.UserMessages;

import org.json.JSONException;
import org.json.JSONObject;

import info.semsamot.actionbarrtlizer.ActionBarRtlizer;
import info.semsamot.actionbarrtlizer.RtlizeEverything;

import static com.razchen.look4u.util.Keys.ID;
import static com.razchen.look4u.util.Keys.SHARED_PREFS;
import static com.razchen.look4u.util.Keys.USER_ID;

public class WelcomeActivity extends UserMenu {

    private RelativeLayout welcomeRelativeLayout;
    private LoginButton loginButton;
    private int width_phone;
    private int height_phone;
    private FirebaseAuth firebaseAuth;
    private CallbackManager mCallbackManager;
    private static final String TAG = "FACELOG";
    private String email="none";
    private String userName="none";
    private String lastName="none";
    private String idUser="none";
    private String image="none";


    //Database
    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeRelativeLayout=findViewById(R.id.welcomeRelativeLayout);

        //TODO add
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if(!sharedPreferences.getString(ID,"").equals(""))
        {
            openMainActivity(sharedPreferences.getString(ID,""));
            finish();
        }


       //TODO remove


//        openMainActivity("1433620840128308");
//        openMainActivity("10000000");//firstName
//        finish();

//            openMainActivity("2620274371328417");//רמי
//            finish();

//        openMainActivity("10215022833790750");//בוריס
//        finish();
//
//        openMainActivity("10220325131828018");//שלומית
//        finish();

//        openMainActivity("10220416625704598");//חן
//        finish();

//        openMainActivity("1433620840128308");//רז
//        finish();


//        openMainActivity("2555678737813687");//יוסף ישראל
//        finish();


//        openMainActivity("2902368549790632");//גואנה
//        finish();

        //Database
        firebase=new Firebase();
        loginButton = findViewById(R.id.facebook_login_button);
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        loginButton.setPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        init_width_and_height();




        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                if (accessToken2 == null) {
                    Log.d("FB", "User Logged Out.");

                    firebaseAuth.signOut();

                }
            }
        };

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        userMenu_actionBarRtlizer = new ActionBarRtlizer(this);
        ViewGroup actionBarView = userMenu_actionBarRtlizer.getActionBarView();
        getSupportActionBar().setTitle("");
        ViewGroup homeView = (ViewGroup) userMenu_actionBarRtlizer.getHomeView();

        userMenu_actionBarRtlizer.flipActionBarUpIconIfAvailable(homeView);
        RtlizeEverything.rtlize(actionBarView);
        RtlizeEverything.rtlize(homeView);
        return true;
    }




    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {

            startMainActivity();
        }

    }



    //Activity
    private void startMainActivity() {
        Profile profile = Profile.getCurrentProfile();

        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        if (profile != null) {

            userName=profile.getFirstName();
            lastName=profile.getLastName();
            image=profile.getProfilePictureUri(200,200).toString();

            intent.putExtra(USER_ID, profile.getId());
            idUser=profile.getId();

            saveUserInfoToDataBase();
            saveUserInfoTodevice();
        }

        finish();
        startActivity(intent);

    }

    private void openMainActivity(String id)
    {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.putExtra(USER_ID, id);
        startActivity(intent);

    }

    //UI
    private void logInUI() {
        Toast.makeText(WelcomeActivity.this, UserMessages.loginSuccsesful, Toast.LENGTH_LONG).show();
        startMainActivity();

    }


    //General
    private void init_width_and_height() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width_phone = size.x;
        height_phone = size.y;
        Log.e("Width", "" + width_phone);
        Log.e("height", "" + height_phone);
    }

    //Facebook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        welcomeRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        useLoginInformation(token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            logInUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            logInUI();
                        }


                    }
                });
    }

    private void useLoginInformation(AccessToken accessToken) {
        /**
         Creating the GraphRequest to fetch user details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    email = object.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }

    private void saveUserInfoTodevice() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ID, idUser);
        editor.apply();

    }


    private void  saveUserInfoToDataBase()
    {
        firebase.getUsersTable().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(idUser)) {

                } else
                {
                    User user=new User(idUser,userName,lastName,image,email);

                    firebase.getUsersTable().child(idUser).setValue(user);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
