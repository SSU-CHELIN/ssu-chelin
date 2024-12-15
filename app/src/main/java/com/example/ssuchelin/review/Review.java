package com.example.ssuchelin.review;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Review {
    private String username;
    private String userReview;
    private float starCount; // 별점 (0~5)
    private int saltPreference; // 간 정도 (0~5)
    private int spicyPreference; // 매운 정도 (0~5)
    private String allergies; // 알레르기 정보

    private boolean liked; // 좋아요 여부
    private int likeCount; // 좋아요 개수

    private boolean disliked; // 싫어요 여부
    private int dislikeCount; // 싫어요 개수

    private String mainMenu; // 메인 메뉴 이름
    private String subMenu; // 서브 메뉴 이름

    private String reviewTime; // 사람이 읽을 수 있는 리뷰 작성 시간
    private long timeMillis; // UNIX 시간 (리뷰 작성 시간)

    private int likeDifference; // 좋아요와 싫어요 차이
    private String authorId; // 리뷰 작성자의 사용자 ID
    // ---- 생성자 ----

    // 전체 필드를 초기화하는 생성자
    public Review(String username, String userReview, float starCount, int saltPreference, int spicyPreference,
                  String allergies, boolean liked, int likeCount, boolean disliked, int dislikeCount, String mainMenu,
                  String subMenu, String reviewTime, long timeMillis, int likeDifference) {
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
        this.mainMenu = mainMenu;
        this.subMenu = subMenu;
        this.reviewTime = reviewTime;
        this.timeMillis = timeMillis;
        this.likeDifference = likeDifference;
    }

    // 간단한 생성자 (메인 메뉴와 서브 메뉴 중심)
    public Review(String mainMenu, String subMenu, String userReview, float starCount) {
        this.mainMenu = mainMenu;
        this.subMenu = subMenu;
        this.userReview = userReview;
        this.starCount = starCount;

        this.username = "";
        this.saltPreference = 0;
        this.spicyPreference = 0;
        this.allergies = "";
        this.liked = false;
        this.likeCount = 0;
        this.disliked = false;
        this.dislikeCount = 0;
        this.reviewTime = "";
        this.timeMillis = 0L;
        this.likeDifference = 0;
    }

    // 사용자 리뷰 초기화를 위한 생성자
    public Review(String username, String userReview, float starCount, int saltPreference, int spicyPreference,
                  String allergies, boolean liked, int likeCount, boolean disliked, int dislikeCount) {
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

        // 기본값 초기화 (필요에 따라 수정 가능)
        this.mainMenu = "";
        this.subMenu = "";
        this.reviewTime = "";
        this.timeMillis = 0L;
        this.likeDifference = 0;
    }


    // 기본 생성자 (Firebase 매핑용)
    public Review() {
        this.username = "";
        this.userReview = "";
        this.starCount = 0;
        this.saltPreference = 0;
        this.spicyPreference = 0;
        this.allergies = "";
        this.liked = false;
        this.likeCount = 0;
        this.disliked = false;
        this.dislikeCount = 0;
        this.mainMenu = "";
        this.subMenu = "";
        this.reviewTime = "";
        this.timeMillis = 0L;
        this.likeDifference = 0;
    }

    // 간단한 생성자 (메인 메뉴, 서브 메뉴, 리뷰, 별점, 시간, 좋아요 차이 중심)
    public Review(String mainMenu, String subMenu, String userReview, float starCount, long timeMillis, int likeDifference) {
        this.mainMenu = mainMenu;
        this.subMenu = subMenu;
        this.userReview = userReview;
        this.starCount = starCount;
        this.timeMillis = timeMillis;
        this.likeDifference = likeDifference;

        // 나머지 필드 기본값으로 초기화
        this.username = "";
        this.saltPreference = 0;
        this.spicyPreference = 0;
        this.allergies = "";
        this.liked = false;
        this.likeCount = 0;
        this.disliked = false;
        this.dislikeCount = 0;
        this.reviewTime = "";
    }


    // ---- 유틸리티 메서드 ----

    // 작성 시간을 사람이 읽기 쉬운 형식으로 변환
    public String getFormattedTime() {
        if (timeMillis == 0L) return "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd (E)", Locale.getDefault());
        return formatter.format(new Date(timeMillis));
    }

    // ---- Getter/Setter ----

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserReview() { return userReview; }
    public void setUserReview(String userReview) { this.userReview = userReview; }

    public float getStarCount() { return starCount; }
    public void setStarCount(float starCount) {
        if (starCount < 0 || starCount > 5) {
            throw new IllegalArgumentException("별점은 0과 5 사이여야 합니다.");
        }
        this.starCount = starCount;
    }

    public int getSaltPreference() { return saltPreference; }
    public void setSaltPreference(int saltPreference) {
        if (saltPreference < 0 || saltPreference > 5) {
            throw new IllegalArgumentException("간 정도는 0과 5 사이여야 합니다.");
        }
        this.saltPreference = saltPreference;
    }

    public int getSpicyPreference() { return spicyPreference; }
    public void setSpicyPreference(int spicyPreference) {
        if (spicyPreference < 0 || spicyPreference > 5) {
            throw new IllegalArgumentException("맵기 정도는 0과 5 사이여야 합니다.");
        }
        this.spicyPreference = spicyPreference;
    }

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

    public String getMainMenu() { return mainMenu; }
    public void setMainMenu(String mainMenu) { this.mainMenu = mainMenu; }

    public String getSubMenu() { return subMenu; }
    public void setSubMenu(String subMenu) { this.subMenu = subMenu; }

    public String getReviewTime() { return reviewTime; }
    public void setReviewTime(String reviewTime) { this.reviewTime = reviewTime; }

    public long getTimeMillis() { return timeMillis; }
    public void setTimeMillis(long timeMillis) { this.timeMillis = timeMillis; }

    public int getLikeDifference() { return likeDifference; }
    public void setLikeDifference(int likeDifference) { this.likeDifference = likeDifference; }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "Review{" +
                "mainMenu='" + mainMenu + '\'' +
                ", subMenu='" + subMenu + '\'' +
                ", userReview='" + userReview + '\'' +
                ", starCount=" + starCount +
                ", likeDifference=" + likeDifference +
                ", timeMillis=" + timeMillis +
                '}';
    }

    public String getMenuName() {
        if (mainMenu != null && !mainMenu.isEmpty()) {
            return mainMenu + (subMenu != null && !subMenu.isEmpty() ? " - " + subMenu : "");
        }
        return "알 수 없음";
    }
}
