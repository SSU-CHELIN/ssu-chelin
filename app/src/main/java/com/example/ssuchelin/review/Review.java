package com.example.ssuchelin.review;

// Review 객체 (각 리뷰의 데이터를 담는 모델)
public class Review {
    private final String username;
    private final String userReview;
    private final int starCount;

    public Review(String username, String userReview, int starCount) {
        this.username = username;
        this.userReview = userReview;
        this.starCount = starCount;
    }

    public String getUsername() {
        return username;
    }

    public String getUserReview() {
        return userReview;
    }

    public int getStarCount() {
        return starCount;
    }
}
