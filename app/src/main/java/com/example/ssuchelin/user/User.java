package com.example.ssuchelin.user;

// 파이어베이스에서 데이터를 가져오거나 저장할 때 사용할 데이터 모델 역할을 함
public class User {
    private String id;              // 사용자 고유 ID (토큰 등)
    private String name;            // 사용자 이름 (닉네임)
    private int score;              // 기존 사용자 점수 (사용 여부에 따라 유지)
    private String profileImageUrl; // 사용자 프로필 이미지 URL
    private String studentId;       // 사용자 학번 (랭킹 정렬에 사용)
    private int totalLike;          // 사용자 totalLike (모든 리뷰 총합)

    // 기본 생성자: Firebase에서 데이터를 불러올 때 필요
    public User() {
        // Firebase를 위한 기본 생성자
    }

    // 이름만 받는 생성자
    public User(String name) {
        this.name = name;
    }

    // 모든 필드를 초기화하는 생성자
    public User(String id, String name, int score, String profileImageUrl, String studentId, int totalLike) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.profileImageUrl = profileImageUrl;
        this.studentId = studentId;
        this.totalLike = totalLike;
    }

    // totalLike와 studentId를 사용하는 랭킹용 생성자 예시
    public User(String name, String studentId, int totalLike) {
        this.name = name;
        this.studentId = studentId;
        this.totalLike = totalLike;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }
}
