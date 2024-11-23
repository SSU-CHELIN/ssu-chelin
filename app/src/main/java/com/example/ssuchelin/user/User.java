package com.example.ssuchelin.user;

// 파이어베이스에서 데이터를 가져오거나 저장할 때 사용할 데이터 모델 역할을 함
public class User {
    private String id; // 사용자 ID
    private String name; // 사용자 이름
    private int score; // 사용자 점수 (추천수 등 랭킹에 반영될 수 있음)
    private String profileImageUrl; // 사용자 프로필 이미지 URL

    // 기본 생성자: Firebase에서 데이터를 불러올 때 필요
    public User() {
        // Firebase를 위한 기본 생성자
    }
    public User(String name) {
        this.name = name;
    }

    // 모든 필드를 초기화하는 생성자
    public User(String id, String name, int score, String profileImageUrl) {
        this.id = id; // 사용자 ID 초기화
        this.name = name; // 사용자 이름 초기화
        this.score = score; // 사용자 점수 초기화
        this.profileImageUrl = profileImageUrl; // 사용자 프로필 이미지 URL 초기화
    }

    // Getter 메서드들: 각 변수에 대한 접근자 메서드
    public String getId() {
        return id; // 사용자 ID 반환
    }

    public String getName() {
        return name; // 사용자 이름 반환
    }

    public int getScore() {
        return score; // 사용자 점수 반환
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl; // 사용자 프로필 이미지 URL 반환
    }
}