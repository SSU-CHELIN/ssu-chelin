package com.example.ssuchelin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
// import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends BaseActivity {

    private TextView usernameTextView, studentIdTextView, rankingTextView;
    private ImageView profilePicture;
    private ImageView star1, star2, star3;

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

    // Firebase Realtime Database를 사용한 사용자 데이터 가져오기 (파베) / 주의할 점 : user_id가 고정되어있음, 사용자에따라 맞게 바꿔야함
    // Firebase에서 사용자 정보 불러오기
    private void loadUserData() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child("user_id"); // user_id를 실제 사용자 ID로 교체

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                String studentId = snapshot.child("studentId").getValue(String.class);
                String ranking = snapshot.child("ranking").getValue(String.class);
                int reviewScore = snapshot.child("reviewScore").getValue(Integer.class); // 리뷰 점수를 1~100 사이로 가정

                usernameTextView.setText(username);
                studentIdTextView.setText(studentId);
                rankingTextView.setText("랭킹 : " + ranking + "위");

                // 리뷰 점수에 따라 별 색 채우기 (예: 3개 별)
                updateStarRating(reviewScore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        });
    }

    // 별 색상을 리뷰 점수에 따라 업데이트하는 함수
    private void updateStarRating(int score) {
        // 예시: 1~100의 점수를 받아 별을 채웁니다.
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

    // 프로필 사진 변경 함수
    public void changeProfilePicture(View view) {
        // 프로필 사진을 변경하는 로직 추가
    }

    // 화면 전환 함수들
    public void navigateToChangeProfile(View view) {
        Intent intent = new Intent(this, InitialSettingActivity.class);
        startActivity(intent);
    }

    public void navigateToInitialSettings(View view) {
        Intent intent = new Intent(this, FirstSettingActivity.class);
        startActivity(intent);
    }

    public void navigateToCheckReviews(View view) {
        Intent intent = new Intent(this, CheckReviewsActivity.class);
        startActivity(intent);
    }

    public void navigateToTermsOfService(View view) {
        //Intent intent = new Intent(this, TermsOfServiceActivity.class);
        //startActivity(intent);
    }

    public void navigateToPrivacyPolicy(View view) {
        //Intent intent = new Intent(this, PrivacyPolicyActivity.class);
        //startActivity(intent);
    }

    public void navigateToFeedback(View view) {
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }

    // Firebase Authentication을 사용한 로그아웃 기능 (파베)
    // 로그아웃 함수
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut(); // Firebase에서 로그아웃
        Intent intent = new Intent(this, MainActivity.class); // 로그인 화면으로 이동
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
