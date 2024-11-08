package com.example.ssuchelin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

// 내 리뷰 확인 화면

public class CheckReviewsActivity extends AppCompatActivity {

    private LinearLayout reviewsContainer;
    private TextView noReviewsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_reviews);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        reviewsContainer = findViewById(R.id.reviews_container);
        noReviewsText = findViewById(R.id.no_reviews_text);

        loadReviews();
    }

    private void loadReviews() {
        // 파베, Firebase Database Reference 초기화
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child("user_id");


        // 파베, Firebase 데이터 불러오기

        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DataSnapshot> reviewsList = new ArrayList<>();
                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    reviewsList.add(reviewSnapshot);
                }

                // 최신순으로 정렬
                Collections.sort(reviewsList, (a, b) -> {
                    Long timeA = a.child("timestamp").getValue(Long.class);
                    Long timeB = b.child("timestamp").getValue(Long.class);
                    return timeB.compareTo(timeA);
                });

                if (reviewsList.isEmpty()) {
                    noReviewsText.setVisibility(View.VISIBLE);
                } else {
                    noReviewsText.setVisibility(View.GONE);
                    for (DataSnapshot reviewSnapshot : reviewsList) {
                        View reviewView = getLayoutInflater().inflate(R.layout.review_item, reviewsContainer, false);

                        // 리뷰 데이터 설정
                        TextView username = reviewView.findViewById(R.id.review_username);
                        TextView preferences = reviewView.findViewById(R.id.review_preferences);
                        TextView content = reviewView.findViewById(R.id.review_content);
                        TextView date = reviewView.findViewById(R.id.review_date);
                        TextView menu = reviewView.findViewById(R.id.review_menu);
                        TextView editButton = reviewView.findViewById(R.id.edit_button);

                        username.setText("최정인");
                        preferences.setText("간 정도 : " + reviewSnapshot.child("saltLevel").getValue()
                                + " / 맵기 정도 : " + reviewSnapshot.child("spicyLevel").getValue()
                                + " / 알레르기 : " + reviewSnapshot.child("allergy").getValue());
                        content.setText(reviewSnapshot.child("content").getValue(String.class));
                        date.setText(reviewSnapshot.child("date").getValue(String.class));
                        menu.setText(reviewSnapshot.child("menuCategory").getValue(String.class) + " - "
                                + reviewSnapshot.child("menuName").getValue(String.class));

                        // 수정 버튼 클릭 리스너 설정
                        editButton.setOnClickListener(v -> {
                           // Intent intent = new Intent(CheckReviewsActivity.this, EditReviewActivity.class);
                            //intent.putExtra("review_id", reviewSnapshot.getKey());
                            //startActivity(intent);
                        });

                        reviewsContainer.addView(reviewView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        });


    }
}
