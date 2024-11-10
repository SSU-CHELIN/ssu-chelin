package com.example.ssuchelin;

// OverviewReviewsActivity와 연동하여 특정 카테고리의 리뷰 목록을 표시하는 액티비티

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviewListActivity extends AppCompatActivity {

    // 리뷰 목록을 표시할 컨테이너 레이아웃
    private LinearLayout reviewListContainer;
    // 하위 카테고리 이름을 저장하는 변수
    private String subCategory;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        // View 초기화
        reviewListContainer = findViewById(R.id.review_list_container);
        // OverviewReviewsActivity에서 전달받은 하위 카테고리 이름을 가져옵니다.
        subCategory = getIntent().getStringExtra("subCategory");

        // 리뷰 목록 로드 함수 호출
        loadReviews();
    }

    /**
     * Firebase에서 선택한 하위 카테고리의 리뷰 목록을 불러오는 함수
     */
    private void loadReviews() {
        // Firebase Database에서 'reviews' 경로 아래 해당 하위 카테고리 이름을 가진 노드 참조
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(subCategory);

        // Firebase에서 리뷰 데이터를 비동기적으로 가져옵니다.
        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // 리뷰 데이터를 담을 리스트 생성
                List<DataSnapshot> reviewsList = new ArrayList<>();
                // Firebase로부터 데이터를 가져와 리스트에 추가
                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    reviewsList.add(reviewSnapshot);
                }

                // 리뷰를 최신순으로 정렬 (timestamp를 기준으로 내림차순 정렬)
                Collections.sort(reviewsList, (a, b) -> {
                    Long timeA = a.child("timestamp").getValue(Long.class);
                    Long timeB = b.child("timestamp").getValue(Long.class);
                    return timeB.compareTo(timeA);
                });

                // 리뷰가 없는 경우, "작성된 리뷰가 없습니다." 메시지 표시
                if (reviewsList.isEmpty()) {
                    TextView noReviewsText = new TextView(ReviewListActivity.this);
                    noReviewsText.setText("작성된 리뷰가 없습니다.");
                    reviewListContainer.addView(noReviewsText);
                } else {
                    // 리뷰가 있는 경우, 각 리뷰를 레이아웃에 추가하여 화면에 표시
                    for (DataSnapshot reviewSnapshot : reviewsList) {
                        // overview_review_item 레이아웃을 inflate하여 리뷰 아이템 생성
                        View reviewView = getLayoutInflater().inflate(R.layout.overview_review_item, reviewListContainer, false);

                        // 각 리뷰 아이템의 View 요소 초기화 및 데이터 설정
                        TextView username = reviewView.findViewById(R.id.review_username); // 사용자 이름
                        TextView preferences = reviewView.findViewById(R.id.review_preferences); // 간/맵기/알레르기 정보
                        TextView content = reviewView.findViewById(R.id.review_content); // 리뷰 내용
                        TextView date = reviewView.findViewById(R.id.review_date); // 작성 날짜
                        TextView menu = reviewView.findViewById(R.id.review_menu); // 메뉴 카테고리 및 메뉴 이름

                        // Firebase에서 가져온 데이터를 각 TextView에 설정
                        username.setText(reviewSnapshot.child("username").getValue(String.class));
                        preferences.setText("간 정도 : " + reviewSnapshot.child("saltLevel").getValue()
                                + " / 맵기 정도 : " + reviewSnapshot.child("spicyLevel").getValue()
                                + " / 알레르기 : " + reviewSnapshot.child("allergy").getValue());
                        content.setText(reviewSnapshot.child("content").getValue(String.class));
                        date.setText(reviewSnapshot.child("date").getValue(String.class));
                        menu.setText(reviewSnapshot.child("menuCategory").getValue(String.class) + " - "
                                + reviewSnapshot.child("menuName").getValue(String.class));

                        // 생성된 리뷰 아이템을 레이아웃에 추가
                        reviewListContainer.addView(reviewView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // 에러가 발생한 경우 처리 (예: 로그 출력, 사용자에게 알림 표시)
            }
        });
    }
}
