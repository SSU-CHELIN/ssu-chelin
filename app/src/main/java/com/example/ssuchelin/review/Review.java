package com.example.ssuchelin.review;


public class Review {
    private String username;
    private String userReview;
    private int starCount;
    private int saltPreference;
    private int spicyPreference;
    private String allergies;

    public Review() {
        // Firebase 데이터 매핑을 위해 기본 생성자 필요
    }

    public Review(String username, String userReview, int starCount, int saltPreference, int spicyPreference, String allergies) {
        this.username = username;
        this.userReview = userReview;
        this.starCount = starCount;
        this.saltPreference = saltPreference;
        this.spicyPreference = spicyPreference;
        this.allergies = allergies;
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

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }
    public int getSaltPreference() { return saltPreference; }
    public int getSpicyPreference() { return spicyPreference; }
    public String getAllergies() { return allergies; }
}

