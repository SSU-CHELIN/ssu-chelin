package com.example.ssuchelin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckReviewsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference userInfoReference;
    private RecyclerView reviewsRecyclerView;
    private ReviewsAdapter reviewsAdapter;
    private List<Review> reviewsList = new ArrayList<>();
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_reviews);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(studentId).child("myReviewData");
        userInfoReference = FirebaseDatabase.getInstance().getReference("User").child(studentId).child("userinfo");

        reviewsRecyclerView = findViewById(R.id.reviews_recycler_view); // RecyclerView 초기화
        // LinearLayoutManager 설정 (리스트 형태로 표시)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        reviewsRecyclerView.setLayoutManager(layoutManager); // LayoutManager 설정

        // 어댑터 설정
        reviewsAdapter = new ReviewsAdapter(reviewsList);
        reviewsRecyclerView.setAdapter(reviewsAdapter);
        // 사용자 정보 로드
        loadUserInfo();


    }

    private void loadUserInfo() {
        userInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = snapshot.child("userName").getValue(String.class);
                if (username == null) {
                    username = "Unknown User";
                }

                // 리뷰 데이터 로드
                loadReviews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckReviewsActivity.this, "사용자 정보 로드 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReviews() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewsList.clear(); // 이전 리뷰 목록 초기화

                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    String myReview = reviewSnapshot.child("userReview").getValue(String.class);
                    Integer starCount = reviewSnapshot.child("starCount").getValue(Integer.class);

                    // 리뷰 객체 생성
                    Review review = new Review(username, myReview, starCount != null ? starCount : 0);

                    // 리스트에 추가
                    reviewsList.add(review);
                }

                // 어댑터에 데이터 전달
                reviewsAdapter = new ReviewsAdapter(reviewsList);
                reviewsRecyclerView.setAdapter(reviewsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CheckReviewsActivity.this, "데이터 로드 실패: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 툴바의 뒤로가기 버튼 클릭 처리
        if (item.getItemId() == android.R.id.home) {
            // 현재 Activity 종료하여 이전 화면으로 돌아감
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

