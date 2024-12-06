package com.example.ssuchelin.review;

public class Review {
    private String username;
    private String userReview;
    private float starCount;
    private int saltPreference;
    private int spicyPreference;
    private String allergies;

    private boolean liked;        // 좋아요 여부
    private int likeCount;        // 좋아요 수

    private boolean disliked;     // 싫어요 여부
    private int dislikeCount;     // 싫어요 수

    public Review(String username, String userReview, float starCount, int saltPreference, int spicyPreference, String allergies,
                  boolean liked, int likeCount, boolean disliked, int dislikeCount) {
        this.username = username;
        this.userReview = userReview;
        this.starCount = starCount;
        this.saltPreference = saltPreference;
        this.spicyPreference = spicyPreference;
        this.allergies = allergies;
        this.liked = liked;
        this.likeCount = likeCount;
        this.disliked = disliked;
        this.dislikeCount = dislikeCount;
    }

    public Review() {
        // Firebase 매핑 위해 기본 생성자 필요
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserReview() { return userReview; }
    public void setUserReview(String userReview) { this.userReview = userReview; }

    public float getStarCount() { return starCount; }
    public void setStarCount(float starCount) { this.starCount = starCount; }

    public int getSaltPreference() { return saltPreference; }
    public void setSaltPreference(int saltPreference) { this.saltPreference = saltPreference; }

    public int getSpicyPreference() { return spicyPreference; }
    public void setSpicyPreference(int spicyPreference) { this.spicyPreference = spicyPreference; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public boolean isDisliked() { return disliked; }
    public void setDisliked(boolean disliked) { this.disliked = disliked; }

    public int getDislikeCount() { return dislikeCount; }
    public void setDislikeCount(int dislikeCount) { this.dislikeCount = dislikeCount; }
}
