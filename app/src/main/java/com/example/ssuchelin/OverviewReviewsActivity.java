package com.example.ssuchelin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

//파베 부분 추가 안함(현재 동적X), 카테고리 메인 화면

public class OverviewReviewsActivity extends AppCompatActivity {

    // 카테고리 항목들이 담길 LinearLayout. 카테고리와 하위 카테고리들을 동적으로 추가하고 제거하기 위해 사용됩니다.
    private LinearLayout categoryContainer;

    // 각 상위 카테고리의 확장 상태를 저장하는 HashMap. 카테고리가 열려있는지(true) 닫혀있는지(false)를 관리합니다.
    private HashMap<String, Boolean> expandedCategories;

    // 검색 입력 필드 (EditText). 사용자가 입력한 텍스트를 기반으로 카테고리와 하위 카테고리를 필터링합니다.
    private EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_reviews);

        // category_container 레이아웃을 초기화합니다.
        // 하위 카테고리들이 이 LinearLayout에 동적으로 추가됩니다.
        categoryContainer = findViewById(R.id.category_container);

        // search_input EditText 초기화 및 텍스트 변화 감지 리스너 추가
        // 사용자가 입력할 때마다 filterCategories 메서드를 호출하여 카테고리를 필터링합니다.
        searchInput = findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCategories(s.toString()); // 입력된 텍스트에 맞춰 카테고리 필터링
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 확장 상태를 관리하는 맵 초기화
        // 초기 상태는 모든 카테고리가 닫혀있는 상태입니다.
        expandedCategories = new HashMap<>();
        expandedCategories.put("뚝배기", false);
        expandedCategories.put("덮밥", false);
        expandedCategories.put("양식", false);

        // 상위 카테고리 클릭 리스너 설정
        // 각 카테고리에 클릭 이벤트를 연결합니다.
        setupCategory("뚝배기", R.id.category_tukbaegi);
        setupCategory("덮밥", R.id.category_rice);
        setupCategory("양식", R.id.category_western);
    }

    // 각 상위 카테고리에 클릭 리스너를 설정하는 메서드
    private void setupCategory(String categoryName, int viewId) {
        // viewId에 해당하는 View에 클릭 이벤트 설정
        findViewById(viewId).setOnClickListener(v -> toggleSubCategories(categoryName, v));
    }

    // 특정 카테고리를 클릭할 때 하위 카테고리를 열고 닫는 메서드
    private void toggleSubCategories(String category, View categoryView) {
        // 현재 카테고리의 확장 상태를 반전시킴
        boolean isExpanded = expandedCategories.get(category);
        expandedCategories.put(category, !isExpanded);

        // 하위 카테고리를 추가하거나 제거
        if (!isExpanded) {
            addSubCategories(category, categoryView); // 하위 카테고리 추가
        } else {
            removeSubCategories(categoryView); // 하위 카테고리 제거
        }
    }

    // 선택한 상위 카테고리에 따라 하위 카테고리를 동적으로 추가하는 메서드
    private void addSubCategories(String category, View categoryView) {
        String[] subCategories; // 하위 카테고리를 담을 배열
        switch (category) {
            case "뚝배기":
                subCategories = new String[]{"찌개", "찜", "면"};
                break;
            case "덮밥":
                subCategories = new String[]{"덮밥"};
                break;
            case "양식":
                subCategories = new String[]{"파스타"};
                break;
            default:
                subCategories = new String[]{};
                break;
        }

        // 선택한 상위 카테고리 아래에 하위 카테고리 추가
        int index = categoryContainer.indexOfChild(categoryView);
        for (String subCategory : subCategories) {
            TextView subCategoryTextView = new TextView(this);
            subCategoryTextView.setText("   > " + subCategory); // 하위 카테고리 텍스트 설정
            subCategoryTextView.setTextSize(16);
            subCategoryTextView.setPadding(48, 8, 8, 8);
            subCategoryTextView.setOnClickListener(v -> openReviewList(subCategory)); // 클릭 시 리뷰 목록 열기
            categoryContainer.addView(subCategoryTextView, ++index); // 하위 카테고리 추가
        }
    }

    // 상위 카테고리 클릭 시 하위 카테고리를 제거하는 메서드
    private void removeSubCategories(View categoryView) {
        int index = categoryContainer.indexOfChild(categoryView) + 1;

        // 하위 카테고리가 있는 동안 삭제
        while (index < categoryContainer.getChildCount() &&
                categoryContainer.getChildAt(index) instanceof TextView &&
                ((TextView) categoryContainer.getChildAt(index)).getText().toString().startsWith("   >")) {
            categoryContainer.removeViewAt(index);
        }
    }

    // 하위 카테고리를 선택할 때 해당 카테고리의 리뷰 목록 화면을 여는 메서드
    private void openReviewList(String subCategory) {
        Intent intent = new Intent(this, ReviewListActivity.class);
        intent.putExtra("subCategory", subCategory); // 선택된 하위 카테고리를 인텐트에 추가하여 전달
        startActivity(intent);
    }

    // 검색어에 맞춰 카테고리를 필터링하는 메서드
    private void filterCategories(String query) {
        for (int i = 0; i < categoryContainer.getChildCount(); i++) {
            View view = categoryContainer.getChildAt(i);
            if (view instanceof TextView) {
                TextView categoryTextView = (TextView) view;
                String categoryText = categoryTextView.getText().toString();

                // 검색어가 포함되었는지 확인하여 포함된 항목만 표시, 그렇지 않으면 숨김
                if (categoryText.contains(query) || categoryText.startsWith("> " + query)) {
                    categoryTextView.setVisibility(View.VISIBLE);
                } else {
                    categoryTextView.setVisibility(View.GONE);
                }
            }
        }
    }
}
