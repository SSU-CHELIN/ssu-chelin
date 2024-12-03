package com.example.ssuchelin.review;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class OverviewReviewsFragment extends Fragment {

    private LinearLayout categoryContainer; // 카테고리를 동적으로 추가할 컨테이너
    private HashMap<String, Boolean> expandedCategories; // 카테고리 확장 상태 저장
    private DatabaseReference categoryRef; // Firebase Database 참조

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview_reviews, container, false);

        categoryContainer = view.findViewById(R.id.category_container);
        expandedCategories = new HashMap<>();
        categoryRef = FirebaseDatabase.getInstance().getReference("Category"); // Firebase "Category" 루트

        // 검색 입력창 초기화 및 리스너 추가
        EditText searchInput = view.findViewById(R.id.search_input);
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String query = searchInput.getText().toString().trim();

                if (!query.isEmpty()) {
                    Intent intent = new Intent(getContext(), SearchResultsActivity.class);
                    intent.putExtra("searchQuery", query);
                    startActivity(intent);
                    searchInput.clearFocus();
                }
                return true; // 이벤트 소비
            }
            return false; // 다른 이벤트로 전달
        });

        // Firebase에서 상위 카테고리 로드
        loadCategories();

        return view;
    }

    private void loadCategories() {
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    TextView noDataTextView = new TextView(getContext());
                    noDataTextView.setText("카테고리가 없습니다.");
                    noDataTextView.setTextSize(18);
                    noDataTextView.setPadding(22, 12, 12, 12);
                    categoryContainer.addView(noDataTextView);
                    return;
                }

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryKey = categorySnapshot.getKey(); // 상위 카테고리 키
                    if (categoryKey != null) {
                        expandedCategories.put(categoryKey, false); // 초기 확장 상태 설정
                        addCategoryToView(categoryKey, categoryKey); // 카테고리를 추가
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                TextView errorTextView = new TextView(getContext());
                errorTextView.setText("카테고리를 불러오지 못했습니다.");
                errorTextView.setTextSize(18);
                errorTextView.setPadding(22, 12, 12, 12);
                categoryContainer.addView(errorTextView);
            }
        });
    }

    private void addCategoryToView(String categoryKey, String displayName) {
        TextView categoryTextView = new TextView(getContext());
        categoryTextView.setText("> " + displayName);
        categoryTextView.setTextSize(22);
        categoryTextView.setPadding(22, 12, 12, 12);

        categoryTextView.setOnClickListener(v -> toggleSubCategories(categoryKey, categoryTextView));
        categoryContainer.addView(categoryTextView);
    }

    private void toggleSubCategories(String categoryKey, TextView categoryTextView) {
        boolean isExpanded = expandedCategories.get(categoryKey);
        expandedCategories.put(categoryKey, !isExpanded);

        if (!isExpanded) {
            addSubCategories(categoryKey, categoryTextView);
        } else {
            removeSubCategories(categoryTextView);
        }
    }

    private void addSubCategories(String categoryKey, TextView categoryTextView) {
        categoryRef.child(categoryKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int index = categoryContainer.indexOfChild(categoryTextView) + 1;

                if (expandedCategories.get(categoryKey) && index < categoryContainer.getChildCount()) {
                    removeSubCategories(categoryTextView);
                }

                if (!snapshot.hasChildren()) {
                    TextView noSubCategoryTextView = new TextView(getContext());
                    noSubCategoryTextView.setText("   > 해당하는 메뉴가 없습니다");
                    noSubCategoryTextView.setTextSize(16);
                    noSubCategoryTextView.setPadding(48, 8, 8, 8);
                    categoryContainer.addView(noSubCategoryTextView, index);
                    return;
                }

                for (DataSnapshot subCategorySnapshot : snapshot.getChildren()) {
                    String subCategoryKey = subCategorySnapshot.getKey();
                    String subCategoryName = subCategorySnapshot.getKey();

                    if (subCategoryKey != null && subCategoryName != null) {
                        TextView subCategoryTextView = new TextView(getContext());
                        subCategoryTextView.setText("   > " + subCategoryName);
                        subCategoryTextView.setTextSize(16);
                        subCategoryTextView.setPadding(48, 8, 8, 8);

                        subCategoryTextView.setOnClickListener(v -> openReviewList(subCategoryKey, subCategoryName, categoryKey));
                        categoryContainer.addView(subCategoryTextView, index++);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void removeSubCategories(TextView categoryTextView) {
        int index = categoryContainer.indexOfChild(categoryTextView) + 1;

        while (index < categoryContainer.getChildCount() &&
                categoryContainer.getChildAt(index) instanceof TextView &&
                ((TextView) categoryContainer.getChildAt(index)).getText().toString().startsWith("   >")) {
            categoryContainer.removeViewAt(index);
        }
    }

    private void openReviewList(String subCategoryKey, String subCategoryName, String parentNode) {
        SubCategoryFragment fragment = new SubCategoryFragment();
        Bundle args = new Bundle();
        args.putString("parentNode", parentNode);
        args.putString("subCategoryKey", subCategoryKey);
        args.putString("subCategoryName", subCategoryName);
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
