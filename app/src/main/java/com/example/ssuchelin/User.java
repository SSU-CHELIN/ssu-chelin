package com.example.ssuchelin;


//파이어베이스에서 데이터를 가져오거나 저장할 때 사용할 데이터 모델 역할을 함


public class User {
    private String id;
    private String name;
    private int score;
    private String profileImageUrl;

    public User() {
        // Firebase를 위한 기본 생성자
    }

    public User(String id, String name, int score, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.profileImageUrl = profileImageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
