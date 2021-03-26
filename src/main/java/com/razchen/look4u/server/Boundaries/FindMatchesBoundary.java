package com.razchen.look4u.server.Boundaries;

public class FindMatchesBoundary {
    private String categoryPath;
    private String seekerFavorGender;
    private String seekerUserId;
    private int fromAge;
    private int toAge;
    private String categoryChoicesArrInString;

    public FindMatchesBoundary() {
    }

    public FindMatchesBoundary(String categoryPath, String seekerFavorGender, String seekerUserId, int fromAge, int toAge, String categoryChoicesArrInString) {
        this.categoryPath = categoryPath;
        this.seekerFavorGender = seekerFavorGender;
        this.seekerUserId = seekerUserId;
        this.fromAge = fromAge;
        this.toAge = toAge;
        this.categoryChoicesArrInString = categoryChoicesArrInString;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public String getSeekerFavorGender() {
        return seekerFavorGender;
    }

    public void setSeekerFavorGender(String seekerFavorGender) {
        this.seekerFavorGender = seekerFavorGender;
    }

    public String getSeekerUserId() {
        return seekerUserId;
    }

    public void setSeekerUserId(String seekerUserId) {
        this.seekerUserId = seekerUserId;
    }

    public int getFromAge() {
        return fromAge;
    }

    public void setFromAge(int fromAge) {
        this.fromAge = fromAge;
    }

    public int getToAge() {
        return toAge;
    }

    public void setToAge(int toAge) {
        this.toAge = toAge;
    }

    public String getCategoryChoicesArrInString() {
        return categoryChoicesArrInString;
    }

    public void setCategoryChoicesArrInString(String categoryChoicesArrInString) {
        this.categoryChoicesArrInString = categoryChoicesArrInString;
    }
}
