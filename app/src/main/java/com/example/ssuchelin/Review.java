package com.example.ssuchelin;

public class Review {

    // 리뷰 ID (Firebase에서 리뷰를 구분할 때 사용)
    private String reviewId;
    // 작성자 사용자 ID (Firebase에서 특정 사용자를 구분하기 위한 ID)
    private String userId;
    // 작성자 이름 (Firebase에서 가져온 사용자 이름)
    private String username;
    // 작성자 프로필 이미지 URL (Firebase에 저장된 이미지 URL)
    private String profileImageUrl;
    // 별점 (리뷰의 별점, 예: 1~5)
    private int starRating;
    // 간 정도 (리뷰에서 간 정도를 나타내는 값, 예: 1~5)
    private int saltLevel;
    // 맵기 정도 (리뷰에서 맵기 정도를 나타내는 값, 예: 1~5)
    private int spicyLevel;
    // 알레르기 (작성자의 알레르기 정보)
    private String allergy;
    // 리뷰 내용 (리뷰 텍스트)
    private String content;
    // 작성 날짜 (리뷰 작성 일자)
    private String date;
    // 메뉴 카테고리 (리뷰가 작성된 메뉴의 상위 카테고리, 예: '뚝배기', '덮밥')
    private String menuCategory;
    // 메뉴 이름 (리뷰가 작성된 메뉴의 이름, 예: '김치찌개')
    private String menuName;
    // 시간 스탬프 (정렬용으로 사용되는 리뷰 작성 시간)
    private long timestamp;

    // Firebase에서 데이터를 가져올 때 기본 생성자가 필요합니다.
    public Review() {
    }

    // 생성자 (모든 필드를 초기화하는 생성자)
    public Review(String reviewId, String userId, String username, String profileImageUrl, int starRating,
                  int saltLevel, int spicyLevel, String allergy, String content, String date,
                  String menuCategory, String menuName, long timestamp) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.starRating = starRating;
        this.saltLevel = saltLevel;
        this.spicyLevel = spicyLevel;
        this.allergy = allergy;
        this.content = content;
        this.date = date;
        this.menuCategory = menuCategory;
        this.menuName = menuName;
        this.timestamp = timestamp;
    }

    // Getter와 Setter 메서드들 (각 필드에 접근하고 수정하는 메서드)

    // 리뷰 ID 가져오기
    public String getReviewId() {
        return reviewId;
    }

    // 리뷰 ID 설정
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    // 사용자 ID 가져오기
    public String getUserId() {
        return userId;
    }

    // 사용자 ID 설정
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // 사용자 이름 가져오기
    public String getUsername() {
        return username;
    }

    // 사용자 이름 설정
    public void setUsername(String username) {
        this.username = username;
    }

    // 프로필 이미지 URL 가져오기
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    // 프로필 이미지 URL 설정
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // 별점 가져오기
    public int getStarRating() {
        return starRating;
    }

    // 별점 설정
    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }

    // 간 정도 가져오기
    public int getSaltLevel() {
        return saltLevel;
    }

    // 간 정도 설정
    public void setSaltLevel(int saltLevel) {
        this.saltLevel = saltLevel;
    }

    // 맵기 정도 가져오기
    public int getSpicyLevel() {
        return spicyLevel;
    }

    // 맵기 정도 설정
    public void setSpicyLevel(int spicyLevel) {
        this.spicyLevel = spicyLevel;
    }

    // 알레르기 정보 가져오기
    public String getAllergy() {
        return allergy;
    }

    // 알레르기 정보 설정
    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    // 리뷰 내용 가져오기
    public String getContent() {
        return content;
    }

    // 리뷰 내용 설정
    public void setContent(String content) {
        this.content = content;
    }

    // 작성 날짜 가져오기
    public String getDate() {
        return date;
    }

    // 작성 날짜 설정
    public void setDate(String date) {
        this.date = date;
    }

    // 메뉴 카테고리 가져오기
    public String getMenuCategory() {
        return menuCategory;
    }

    // 메뉴 카테고리 설정
    public void setMenuCategory(String menuCategory) {
        this.menuCategory = menuCategory;
    }

    // 메뉴 이름 가져오기
    public String getMenuName() {
        return menuName;
    }

    // 메뉴 이름 설정
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    // 타임스탬프(작성 시간) 가져오기
    public long getTimestamp() {
        return timestamp;
    }

    // 타임스탬프(작성 시간) 설정
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
