package com.example.ssuchelin.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchResultsFragment extends Fragment {

    private LinearLayout resultsContainer;
    private DatabaseReference categoryRef;
    private String searchQuery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        // Toolbar 설정
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
            }
        }

        toolbar.setNavigationOnClickListener(v -> {
            if (!requireActivity().getSupportFragmentManager().isStateSaved()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // View 초기화
        resultsContainer = view.findViewById(R.id.results_container);
        categoryRef = FirebaseDatabase.getInstance().getReference("Category");

        // 검색어 가져오기
        if (getArguments() != null) {
            searchQuery = getArguments().getString("searchQuery", "");
        }

        // Firebase 데이터 로드 및 검색
        searchCategories();

        return view;
    }

    private void searchCategories() {
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resultsContainer.removeAllViews();
                boolean hasResults = false;

                // /// 수정 부분: 새로운 구조에 맞게 탐색
                // Category/{type}/Mainmenu/{메뉴명} 형태
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String parentNode = categorySnapshot.getKey(); // 상위 노드(type 들어가는곳)
                    if (parentNode == null) continue;

                    DataSnapshot mainMenuSnapshot = categorySnapshot.child("Mainmenu");
                    if (!mainMenuSnapshot.exists()) {
                        continue;
                    }

                    // mainMenuSnapshot 하위에 실제 메뉴명들이 key로 있음
                    // 예: mainMenuSnapshot.child("숯불바베큐치밥")
                    // menuName.toLowerCase().contains(searchQuery.toLowerCase()) 검사
                    boolean typeHasResults = false;
                    LinearLayout menuLayout = new LinearLayout(getContext());
                    menuLayout.setOrientation(LinearLayout.VERTICAL);

                    for (DataSnapshot menuSnapshot : mainMenuSnapshot.getChildren()) {
                        String menuName = menuSnapshot.getKey();
                        if (menuName != null && menuName.toLowerCase().contains(searchQuery.toLowerCase())) {
                            // 검색어 포함 메뉴명 발견
                            typeHasResults = true;
                            hasResults = true;

                            TextView menuTextView = new TextView(getContext());
                            menuTextView.setText("   > " + menuName);
                            menuTextView.setTextSize(16);
                            menuTextView.setPadding(48, 8, 8, 8);

                            // 메뉴 클릭 시 SubCategoryFragment로 이동
                            // parentNode = type 들어가는곳
                            // subCategoryKey = menuName (메뉴 이름)
                            // subCategoryName = menuName
                            menuTextView.setOnClickListener(v -> openSubCategory(parentNode, menuName, menuName));

                            menuLayout.addView(menuTextView);
                        }
                    }

                    if (typeHasResults) {
                        TextView typeTextView = new TextView(getContext());
                        typeTextView.setText("> " + parentNode);
                        typeTextView.setTextSize(20);
                        typeTextView.setPadding(32, 16, 16, 16);

                        resultsContainer.addView(typeTextView);
                        resultsContainer.addView(menuLayout);
                    }
                }

                // 검색 결과가 없을 경우
                if (!hasResults) {
                    TextView noResultsTextView = new TextView(getContext());
                    noResultsTextView.setText("검색 결과가 없습니다.");
                    noResultsTextView.setTextSize(18);
                    noResultsTextView.setPadding(16, 16, 16, 16);
                    resultsContainer.addView(noResultsTextView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                TextView errorTextView = new TextView(getContext());
                errorTextView.setText("데이터를 불러오는 중 오류가 발생했습니다.");
                errorTextView.setTextSize(18);
                errorTextView.setPadding(16, 16, 16, 16);
                resultsContainer.addView(errorTextView);
            }
        });
    }

    public static SearchResultsFragment newInstance(String query) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString("searchQuery", query);
        fragment.setArguments(args);
        return fragment;
    }

    private void openSubCategory(String parentNode, String subCategoryKey, String subCategoryName) {
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
