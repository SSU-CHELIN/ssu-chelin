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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

// 내 리뷰 확인 화면

public class CheckReviewsActivity extends BaseActivity {

    private LinearLayout reviewsContainer; // 리뷰 목록을 표시하는 LinearLayout
    private TextView noReviewsText; // "작성된 리뷰가 없습니다"라는 메시지를 표시하는 TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_reviews);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // 제목 숨기기
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 아이콘 활성화
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        reviewsContainer = findViewById(R.id.reviews_container); // 리뷰 목록을 담는 컨테이너 초기화
        noReviewsText = findViewById(R.id.no_reviews_text); // 리뷰가 없을 때 보여줄 메시지 초기화

//        loadReviews(); // 리뷰 데이터를 로드하는 함수 호출
    }

    /**
     * Firebase에서 현재 사용자의 리뷰 데이터를 불러와서 화면에 표시하는 메서드
     */
/*    private void loadReviews() {
        // 로그인된 사용자 ID 가져오기
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(userId);

        // Firebase에서 리뷰 데이터 불러오기
        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DataSnapshot> reviewsList = new ArrayList<>(); // Firebase에서 가져온 리뷰 목록을 저장하는 리스트
                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    reviewsList.add(reviewSnapshot); // 각 리뷰 데이터를 리스트에 추가
                }

                // 최신순으로 정렬
                Collections.sort(reviewsList, (a, b) -> {
                    Long timeA = a.child("timestamp").getValue(Long.class);
                    Long timeB = b.child("timestamp").getValue(Long.class);
                    return timeB.compareTo(timeA); // 시간 기준 내림차순 정렬
                });

                if (reviewsList.isEmpty()) {
                    // 리뷰가 없을 경우 "작성된 리뷰가 없습니다" 표시
                    noReviewsText.setVisibility(View.VISIBLE);
                } else {
                    // 리뷰가 있을 경우 표시
                    noReviewsText.setVisibility(View.GONE);
                    for (DataSnapshot reviewSnapshot : reviewsList) {
                        View reviewView = getLayoutInflater().inflate(R.layout.review_item, reviewsContainer, false);

                        // 리뷰 데이터 설정
                        TextView username = reviewView.findViewById(R.id.review_username); // 리뷰 작성자 이름 표시
                        TextView preferences = reviewView.findViewById(R.id.review_preferences); // 간, 맵기, 알레르기 정보 표시
                        TextView content = reviewView.findViewById(R.id.review_content); // 리뷰 내용 표시
                        TextView date = reviewView.findViewById(R.id.review_date); // 리뷰 작성 날짜 표시
                        TextView menu = reviewView.findViewById(R.id.review_menu); // 메뉴 카테고리 및 이름 표시
                        TextView editButton = reviewView.findViewById(R.id.edit_button); // 수정 버튼

                        // Firebase에서 개별 리뷰 데이터 가져와 설정
                        username.setText(reviewSnapshot.child("username").getValue(String.class));
                        preferences.setText("간 정도 : " + reviewSnapshot.child("saltLevel").getValue()
                                + " / 맵기 정도 : " + reviewSnapshot.child("spicyLevel").getValue()
                                + " / 알레르기 : " + reviewSnapshot.child("allergy").getValue());
                        content.setText(reviewSnapshot.child("content").getValue(String.class));
                        date.setText(reviewSnapshot.child("date").getValue(String.class));
                        menu.setText(reviewSnapshot.child("menuCategory").getValue(String.class) + " - "
                                + reviewSnapshot.child("menuName").getValue(String.class));

                        // 수정 버튼 클릭 리스너 설정 (로그인한 사용자의 리뷰일 경우에만 보이도록 설정)
                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        if (userId.equals(currentUserId)) {
                            editButton.setVisibility(View.VISIBLE);
                            editButton.setOnClickListener(v -> {
                                Intent intent = new Intent(CheckReviewsActivity.this, EditReviewActivity.class);
                                intent.putExtra("review_id", reviewSnapshot.getKey()); // 리뷰 ID 전달
                                startActivity(intent); // EditReviewActivity로 이동
                            });
                        } else {
                            editButton.setVisibility(View.GONE); // 다른 사용자의 리뷰일 경우 수정 버튼 숨김
                        }

                        reviewsContainer.addView(reviewView); // 리뷰 항목을 컨테이너에 추가
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Firebase 데이터베이스 접근 오류 처리
                // 예: Toast 메시지로 에러를 사용자에게 표시
            }
        });
    }

 */
}
