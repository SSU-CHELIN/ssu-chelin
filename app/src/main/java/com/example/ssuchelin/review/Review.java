package com.example.ssuchelin.review;


public class Review {
    private String username;
    private String userReview;
    private float score;
    private int saltPreference;
    private int spicyPreference;
    private String allergies;



    public Review(String username, String userReview, float score, int saltPreference, int spicyPreference, String allergies) {
        this.username = username;
        this.userReview = userReview;
        this.score = score;
        this.saltPreference = saltPreference;
        this.spicyPreference = spicyPreference;
        this.allergies = allergies;
    }

    public Review() {
        // Firebase 데이터 매핑을 위해 기본 생성자 필요
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserReview() {
        return userReview;
    }

    public void setUserReview(String userReview) {
        this.userReview = userReview;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getSaltPreference() {
        return saltPreference;
    }

    public void setSaltPreference(int saltPreference) {
        this.saltPreference = saltPreference;
    }

    public int getSpicyPreference() {
        return spicyPreference;
    }

    public void setSpicyPreference(int spicyPreference) {
        this.spicyPreference = spicyPreference;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }



}

