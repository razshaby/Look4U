package com.razchen.look4u.java_classes;

public class UserChat implements Comparable<UserChat>{

    private String id;
    private String imageUrl;
    private String fullName;
    private String gender;
    private String chat_id;
    private int age;
    private String lastMessage;
    private Long lastMessageTime;
    private String userIDlastMessage;


    public UserChat(String id, String chat_id, String lastMessage, Long lastMessageTime,String userIDlastMessage) {
        this.id = id;
        this.chat_id = chat_id;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.userIDlastMessage=userIDlastMessage;
    }

    public String getUserIDlastMessage() {
        return userIDlastMessage;
    }

    public void setUserIDlastMessage(String userIDlastMessage) {
        this.userIDlastMessage = userIDlastMessage;
    }



    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int compareTo(UserChat userChat) {
        return userChat.getLastMessageTime().compareTo(this.lastMessageTime);
    }


}
