package com.example.ssuchelin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends BaseActivity {

    private TextView usernameTextView, studentIdTextView, rankingTextView; // 사용자 이름, 학번, 랭킹을 표시하는 텍스트뷰
    private ImageView profilePicture; // 프로필 사진을 표시하는 이미지뷰
    private ImageView star1, star2, star3; // 리뷰 점수에 따른 별 이미지뷰 (3개의 별)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // View 초기화
        usernameTextView = findViewById(R.id.username);
        studentIdTextView = findViewById(R.id.student_id);
        rankingTextView = findViewById(R.id.ranking);
        profilePicture = findViewById(R.id.profile_picture);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);

        // Firebase에서 사용자 데이터 가져오기
        loadUserData();
    }

    // Firebase에서 사용자 데이터를 불러오는 함수
    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // 현재 로그인된 사용자 정보
        if (currentUser != null) {
            String userId = currentUser.getUid(); // 사용자 고유 ID
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId); // Firebase Realtime Database의 "users" 경로 참조

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.child("username").getValue(String.class); // Firebase에서 사용자 이름 가져오기
                    String studentId = snapshot.child("studentId").getValue(String.class); // Firebase에서 학번 가져오기
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class); // Firebase에서 프로필 이미지 URL 가져오기
                    Integer reviewScore = snapshot.child("reviewScore").getValue(Integer.class); // Firebase에서 리뷰 점수 가져오기

                    // 사용자 이름 설정 (데이터가 없으면 Firebase 기본 이름 사용)
                    if (username != null && !username.isEmpty()) {
                        usernameTextView.setText(username);
                    } else {
                        usernameTextView.setText(currentUser.getDisplayName()); // Firebase 기본 이름 사용
                    }

                    // 학번 설정 (데이터가 없으면 "N/A" 표시)
                    if (studentId != null && !studentId.isEmpty()) {
                        studentIdTextView.setText(studentId);
                    } else {
                        studentIdTextView.setText("N/A"); // 학번 데이터 없을 때 기본값
                    }

                    // 랭킹 설정 (예시용으로 필요에 따라 수정 가능)
                    rankingTextView.setText("랭킹 : " + (snapshot.child("ranking").getValue() != null ? snapshot.child("ranking").getValue() : "N/A"));

                    // 프로필 이미지 설정 (데이터 없을 때 기본 이미지 사용)
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this).load(profileImageUrl).into(profilePicture);
                    } else {
                        profilePicture.setImageResource(R.drawable.none_pro); // 기본 프로필 이미지
                    }

                    // 별점 설정 (reviewScore가 없으면 0으로)
                    if (reviewScore != null) {
                        updateStarRating(reviewScore);
                    } else {
                        updateStarRating(0); // 기본값 0점
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 별 색상을 리뷰 점수에 따라 업데이트하는 함수
    private void updateStarRating(int score) {
        // 리뷰 점수에 따라 별 이미지를 설정
        if (score >= 70) {
            star1.setImageResource(R.drawable.star);
            star2.setImageResource(R.drawable.star);
            star3.setImageResource(R.drawable.star);
        } else if (score >= 40) {
            star1.setImageResource(R.drawable.star);
            star2.setImageResource(R.drawable.star);
            star3.setImageResource(R.drawable.star_off);
        } else if (score >= 10) {
            star1.setImageResource(R.drawable.star);
            star2.setImageResource(R.drawable.star_off);
            star3.setImageResource(R.drawable.star_off);
        } else {
            star1.setImageResource(R.drawable.star_off);
            star2.setImageResource(R.drawable.star_off);
            star3.setImageResource(R.drawable.star_off);
        }
    }

    // 프로필 사진 변경 함수 (클릭 리스너에 연결하여 프로필 사진 변경 화면으로 이동)
    public void changeProfilePicture(View view) {
        // 프로필 사진을 변경하는 로직 추가
    }

    // 화면 전환 함수들
    public void navigateToChangeProfile(View view) {
        Intent intent = new Intent(this, InitialSettingActivity.class); // InitialSettingActivity로 이동
        startActivity(intent);
    }

    public void navigateToInitialSettings(View view) {
        Intent intent = new Intent(this, FirstSettingActivity.class); // FirstSettingActivity로 이동
        startActivity(intent);
    }

    public void navigateToCheckReviews(View view) {
        Intent intent = new Intent(this, CheckReviewsActivity.class); // CheckReviewsActivity로 이동
        startActivity(intent);
    }

    public void navigateToTermsOfService(View view) {
        // Intent intent = new Intent(this, TermsOfServiceActivity.class); // 서비스 이용약관 화면으로 이동 (코멘트 처리)
        // startActivity(intent);
    }

    public void navigateToPrivacyPolicy(View view) {
        // Intent intent = new Intent(this, PrivacyPolicyActivity.class); // 개인정보 처리방침 화면으로 이동 (코멘트 처리)
        // startActivity(intent);
    }

    public void navigateToFeedback(View view) {
        Intent intent = new Intent(this, FeedbackActivity.class); // FeedbackActivity로 이동
        startActivity(intent);
    }

    // Firebase Authentication을 사용한 로그아웃 기능
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut(); // Firebase에서 로그아웃
        Intent intent = new Intent(this, MainActivity.class); // MainActivity로 이동 (로그인 화면)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 이전 액티비티 스택 삭제
        startActivity(intent); // 새로운 액티비티 시작
        finish(); // 현재 액티비티 종료
    }
}
