package com.example.ssuchelin.review;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchResultsActivity extends AppCompatActivity {

    private LinearLayout resultsContainer;
    private DatabaseReference categoryRef;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 활성화
        }

        toolbar.setNavigationOnClickListener(v -> finish()); // 뒤로가기 버튼 클릭 리스너

        resultsContainer = findViewById(R.id.results_container);
        categoryRef = FirebaseDatabase.getInstance().getReference("Category");

        // 검색어 가져오기
        searchQuery = getIntent().getStringExtra("searchQuery");

        // Firebase 데이터 로드 및 검색
        searchCategories();
    }

    private void searchCategories() {
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resultsContainer.removeAllViews();
                boolean hasResults = false;

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String parentNode = categorySnapshot.getKey(); // 상위 노드

                    for (DataSnapshot subCategorySnapshot : categorySnapshot.getChildren()) {
                        String subCategoryKey = subCategorySnapshot.getKey(); // 중간 노드
                        boolean subCategoryHasResults = false;

                        LinearLayout subCategoryLayout = new LinearLayout(SearchResultsActivity.this);
                        subCategoryLayout.setOrientation(LinearLayout.VERTICAL);

                        for (DataSnapshot childNodeSnapshot : subCategorySnapshot.getChildren()) {
                            String childNodeName = childNodeSnapshot.getValue(String.class); // 하위 노드의 값 (메뉴 이름)

                            if (childNodeName != null && childNodeName.toLowerCase().contains(searchQuery.toLowerCase())) {
                                subCategoryHasResults = true;
                                hasResults = true;

                                TextView childTextView = new TextView(SearchResultsActivity.this);
                                childTextView.setText("   > " + childNodeName);
                                childTextView.setTextSize(16);
                                childTextView.setPadding(48, 8, 8, 8);
                                subCategoryLayout.addView(childTextView);
                            }
                        }

                        // 중간 노드 출력
                        if (subCategoryHasResults) {
                            TextView subCategoryTextView = new TextView(SearchResultsActivity.this);
                            subCategoryTextView.setText(parentNode + "코너 > " + subCategoryKey);
                            subCategoryTextView.setTextSize(20);
                            subCategoryTextView.setPadding(32, 16, 16, 16);
                            resultsContainer.addView(subCategoryTextView);
                            resultsContainer.addView(subCategoryLayout);
                        }
                    }
                }

                // 검색 결과가 없을 경우
                if (!hasResults) {
                    TextView noResultsTextView = new TextView(SearchResultsActivity.this);
                    noResultsTextView.setText("검색 결과가 없습니다.");
                    noResultsTextView.setTextSize(18);
                    noResultsTextView.setPadding(16, 16, 16, 16);
                    resultsContainer.addView(noResultsTextView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                TextView errorTextView = new TextView(SearchResultsActivity.this);
                errorTextView.setText("데이터를 불러오는 중 오류가 발생했습니다.");
                errorTextView.setTextSize(18);
                errorTextView.setPadding(16, 16, 16, 16);
                resultsContainer.addView(errorTextView);
            }
        });
    }
}
