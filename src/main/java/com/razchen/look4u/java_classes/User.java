package com.razchen.look4u.java_classes;

import java.util.Calendar;

public class User  {
    private String id;
    private String firstName;
    private String lastName;
    private String image;
    private String email;
    private String gender;
    private MyDate birthday;
    public User()
    {

    }




    public User(String id,String firstName,String lastName,String image,String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName=lastName;
        this.image = image;
        this.email=email;
        //this.birthday=new MyDate(15,7,1992);
    }
    public User(String id,String firstName,String lastName,String image,String email,String gender,MyDate birthday) {
        this.id = id;
        this.firstName = firstName;
        this.lastName=lastName;
        this.image = image;
        this.email=email;
        this.gender=gender;
        this.birthday=birthday;
    }
    public MyDate getBirthday() {
        return birthday;
    }

    public void setBirthday(MyDate birthday) {
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }



}
