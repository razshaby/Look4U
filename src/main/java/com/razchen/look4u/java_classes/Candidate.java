package com.razchen.look4u.java_classes;


public class Candidate implements  Comparable<Candidate>{
    private String candidateId = "";
    private int compatibility = 0;
    private String imageUrl;
    private String fullName;
    private String mail;
    private String gender;
    private int age;

    public Candidate(String candidateId, int compatibility){
        this.candidateId = candidateId;
        this.compatibility = compatibility;
    };

    public String getCandidateId() {
        return candidateId;
    }

    public int getCompatibility() {
        return compatibility;
    }


    @Override
    public int compareTo(Candidate candidate) {
        return candidate.getCompatibility()-this.compatibility;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public void setCompatibility(int compatibility) {
        this.compatibility = compatibility;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
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


}
