package com.razchen.look4u.dl;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class Firebase {
    public static final String USERS_TABLE_NAME = "users";
    public static final String HAS_SUB_CATEGORIES = "hasSubcategories";
    public static final String CATEGORIES_TABLE_NAME = "categories";
    public static final String QUESTIONNAIRES_TABLE_NAME ="questionnaires";
    public static final  String CHATS = "chats";
    public static final  String USERCHATSID = "userchatsids";
    public static final  String SEPARATPOR = "_";
    public  static final String QUESTIONS="questions";
    public  static final String QUESTION="question";
    public  static final String SERVERPORT="serverport";
    public  static final String ANSWERED_QUESTIONS="answeredQuestions";
    public  static final String ID_DB="idDB";
    public  static final String ANSWERS="answers";
    public  static final String AGE_FROM="ageFrom";
    public  static final String AGE_TO="ageTo";
    public  static final String CREATOR_OF_THE_QUESTIONNAIRE="creator_of_the_questionnaire";
    public  static final String FIRST_NAME="firstName";
    public  static final String LAST_NAME="lastName";
    public  static final String IMAGE="image";
    public  static final String LASTMESSAGE="lastMessage";

    public  static final String NUM_OF_ANSWERS="numOfAnswers";





    public static final  String SLASH = "/";
    public  static final String MESSAGES="messages";
    public  static final String CANDIDATES="candidates";
    public  static final String BIRTHDAY="birthday";
    public  static final String GENDER="gender";



    private DatabaseReference usersTable;
    private DatabaseReference categoriesTable;
    private DatabaseReference questionnaires;
    private DatabaseReference chats;
    private DatabaseReference userchatsids;
    private DatabaseReference serverPort;
    private DatabaseReference questions;
    private DatabaseReference answeredQuestions;

//private  FirebaseFirestore firebaseFirestore;

    public Firebase() {

        usersTable = FirebaseDatabase.getInstance().getReference(USERS_TABLE_NAME);
        categoriesTable=FirebaseDatabase.getInstance().getReference(CATEGORIES_TABLE_NAME);
        //firebaseFirestore= FirebaseFirestore.getInstance();
        questionnaires=FirebaseDatabase.getInstance().getReference(QUESTIONNAIRES_TABLE_NAME);
        chats = FirebaseDatabase.getInstance().getReference(CHATS);
        userchatsids = FirebaseDatabase.getInstance().getReference(USERCHATSID);
        questions =  FirebaseDatabase.getInstance().getReference(QUESTIONS);
        serverPort = FirebaseDatabase.getInstance().getReference(SERVERPORT);
        answeredQuestions =  FirebaseDatabase.getInstance().getReference(ANSWERED_QUESTIONS);

    }

    public DatabaseReference getServerPort() {
        return serverPort;
    }

    public DatabaseReference getUsersTable() {
        return usersTable;
    }

    public DatabaseReference getCategoriesTable() {
        return categoriesTable;
    }

    public DatabaseReference getQuestionnairesTable() {
        return questionnaires;
    }

    public DatabaseReference getQuestionsTable() {
        return questions;
    }

    public DatabaseReference getChatsReference() {
        return chats;
    }

    public DatabaseReference getUserchatsids() {
        return userchatsids;
    }

    public DatabaseReference getAnsweredQustionsTable() {
        return answeredQuestions;
    }



    //    public FirebaseFirestore getFirebaseFirestore() {
//        return firebaseFirestore;
//    }


}
