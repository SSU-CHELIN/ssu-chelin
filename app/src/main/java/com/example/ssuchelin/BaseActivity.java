// BaseActivity.java
package com.example.ssuchelin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base); // 기본 레이아웃 설정

        // 네비게이션 바 버튼들 설정
        ImageButton homeButton = findViewById(R.id.navigate_home_button);
        ImageButton reviewButton = findViewById(R.id.navigate_review_button);
        ImageButton rankingButton = findViewById(R.id.navigate_ranking_button);
        ImageButton profileButton = findViewById(R.id.navigate_profile_button);

        homeButton.setOnClickListener(v -> navigateToHome());
//        profileButton.setOnClickListener(v -> navigateToProfile());
    }

    private void navigateToHome() {
        // 홈 화면으로 이동
        Intent intent = new Intent(this, MainViewActivity.class);
        startActivity(intent);
    }

//    private void navigateToProfile() {
//        // 프로필 화면으로 이동
//        Intent intent = new Intent(this, ProfileActivity.class);
//        startActivity(intent);
//    }
//    private void navigateToReview() {
//        //  review 화면으로 이동
//        Intent intent = new Intent(this, ProfileActivity.class);
//        startActivity(intent);
//    }
//    private void navigateToRanking() {
//        // ranking 화면으로 이동
//        Intent intent = new Intent(this, ProfileActivity.class);
//        startActivity(intent);
//    }
}