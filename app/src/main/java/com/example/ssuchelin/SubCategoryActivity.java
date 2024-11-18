package com.example.ssuchelin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SubCategoryActivity extends AppCompatActivity {

    // 하위 카테고리를 표시하는 레이아웃 컨테이너
    private LinearLayout subCategoryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 아이콘 활성화
        }

        // 뒤로가기 버튼 클릭 리스너 설정
        toolbar.setNavigationOnClickListener(v -> finish());

        // 하위 카테고리를 추가할 컨테이너 레이아웃 초기화
        subCategoryContainer = findViewById(R.id.sub_category_container);

        // MainActivity에서 전달된 상위 카테고리 이름을 가져옵니다.
        String mainCategory = getIntent().getStringExtra("mainCategory");

        // 상위 카테고리에 따라 하위 카테고리를 화면에 표시
        displaySubCategories(mainCategory);
    }

    /**
     * 상위 카테고리에 따라 하위 카테고리를 추가하는 메서드
     *
     * @param mainCategory 선택된 상위 카테고리 이름
     */
    private void displaySubCategories(String mainCategory) {
        // 상위 카테고리가 "뚝배기"일 경우 하위 카테고리 "찌개", "찜", "면" 추가
        if ("뚝배기".equals(mainCategory)) {
            addSubCategory("찌개");
            addSubCategory("찜");
            addSubCategory("면");
        }
        // 상위 카테고리가 "덮밥"일 경우 하위 카테고리 "덮밥" 추가
        else if ("덮밥".equals(mainCategory)) {
            addSubCategory("덮밥");
        }
        // 상위 카테고리가 "양식"일 경우 하위 카테고리 "파스타" 추가
        else if ("양식".equals(mainCategory)) {
            addSubCategory("파스타");
        }
    }

    /**
     * 하위 카테고리를 레이아웃에 추가하는 메서드
     *
     * @param subCategoryName 추가할 하위 카테고리 이름
     */
    private void addSubCategory(String subCategoryName) {
        // 새로운 하위 카테고리 텍스트뷰 생성
        TextView subCategoryTextView = new TextView(this);
        subCategoryTextView.setText("> " + subCategoryName); // 하위 카테고리 이름 설정
        subCategoryTextView.setTextSize(16); // 텍스트 크기 설정
        subCategoryTextView.setPadding(16, 8, 8, 8); // 패딩 설정

        // 하위 카테고리 클릭 시 리뷰 목록으로 이동하는 클릭 리스너 설정
        subCategoryTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SubCategoryActivity.this, ReviewListActivity.class);
            intent.putExtra("subCategory", subCategoryName); // 선택한 하위 카테고리를 전달
            startActivity(intent);
        });

        // 컨테이너에 하위 카테고리 텍스트뷰 추가
        subCategoryContainer.addView(subCategoryTextView);
    }
}
