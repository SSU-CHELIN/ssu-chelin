package com.example.ssuchelin.review;

import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ssuchelin.R;
import com.example.ssuchelin.databinding.FragmentReviewBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviewFragment extends Fragment {
    private FragmentReviewBinding binding;
    private List<Review> reviewList = new ArrayList<>();
    private List<String> reviewKeys = new ArrayList<>(); // 리뷰 키를 저장할 리스트
    private ReviewAdapter reviewAdapter;
    private String type;

    private Button recommendBtn, latestBtn; // 추천순, 최신순 버튼
    private String currentMainMenu; // 현재 메뉴 이름 저장
    private String typeN;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReviewBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String mainMenu = bundle.getString("mainMenu");
            String subMenu = bundle.getString("subMenu");
            String category = bundle.getString("category");

            binding.foodCategory.setText(category);
            binding.foodSubMenu.setText(subMenu);
            binding.foodMainMenu.setText(mainMenu);
            binding.foodImage.setImageBitmap(bundle.getParcelable("imageBitmap"));
            currentMainMenu = mainMenu;
        }

        type = getArguments().getString("type");
        if ("ddook".equals(type)) typeN = "뚝배기 코너";
        if ("dub".equals(type)) typeN = "덮밥 코너";
        if ("yang".equals(type)) typeN = "양식 코너";

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String studentId = sharedPreferences.getString("realStudentId", "Unknown ID");
        reviewAdapter = new ReviewAdapter(reviewList, reviewKeys, studentId);
        binding.reviewRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.reviewRecyclerView.setAdapter(reviewAdapter);

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

        // 추천순, 최신순 버튼 참조
        recommendBtn = view.findViewById(R.id.recommend);
        latestBtn = view.findViewById(R.id.latest);

        recommendBtn.setOnClickListener(v -> {
            Collections.sort(reviewList, (r1, r2) -> r2.getLikeDifference() - r1.getLikeDifference());
            Log.d("ReviewFragment", "Sorted by recommendation: " + reviewList.toString());
            reviewAdapter.updateReviews(reviewList, reviewKeys);
        });

        latestBtn.setOnClickListener(v -> {
            if (reviewList.isEmpty()) {
                Log.e("ReviewFragment", "No reviews to sort by latest!");
                return;
            }
            Collections.sort(reviewList, (r1, r2) -> Long.compare(r2.getTimeMillis(), r1.getTimeMillis()));
            Log.d("ReviewFragment", "Sorted by latest: " + reviewList.toString());
            reviewAdapter.updateReviews(reviewList, reviewKeys);
        });

        // Firebase에서 리뷰 데이터 로드
        fetchReviewsFromCategory(typeN, currentMainMenu);

        binding.writeReview.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mainMenu", binding.foodMainMenu.getText().toString());
            bundle.putString("subMenu", binding.foodSubMenu.getText().toString());
            bundle.putString("category", binding.foodCategory.getText().toString());
            bundle.putString("type", type);
            bundle.putParcelable("imageBitmap", (((BitmapDrawable) binding.foodImage.getDrawable()).getBitmap()));

            WriteReviewFragment writeReviewFragment = new WriteReviewFragment();
            writeReviewFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, writeReviewFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void fetchReviewsFromCategory(String typeN, String mainMenu) {
        if (typeN == null || typeN.isEmpty()) {
            Log.e("ReviewFragment", "typeN is null or empty");
            return;
        }

        DatabaseReference whoWriteRef = FirebaseDatabase.getInstance().getReference("Category")
                .child(typeN)
                .child("Mainmenu")
                .child(mainMenu)
                .child("whoWriteReview");

        whoWriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> userIds = new ArrayList<>();
                for (DataSnapshot userIdSnap : snapshot.getChildren()) {
                    String userId = userIdSnap.getKey();
                    if (userId != null) {
                        userIds.add(userId);
                    }
                }
                if (userIds.isEmpty()) {
                    Toast.makeText(getContext(), "리뷰가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    fetchReviewsAndKeys(userIds, mainMenu);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "데이터를 불러오지 못했습니다: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchReviewsAndKeys(List<String> userIds, String mainMenu) {
        reviewList.clear();
        reviewKeys.clear(); // 키 리스트 초기화

        final int[] remaining = {userIds.size()};
        for (String userId : userIds) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    DataSnapshot reviewDataSnapshot = userSnapshot.child("myReviewData");
                    for (DataSnapshot reviewSnap : reviewDataSnapshot.getChildren()) {
                        String reviewMainMenu = reviewSnap.child("Mainmenu").getValue(String.class);
                        if (mainMenu.equals(reviewMainMenu)) {
                            // 리뷰 데이터와 키 추가
                            Review review = parseReview(reviewSnap);
                            reviewList.add(review);
                            reviewKeys.add(reviewSnap.getKey());
                        }
                    }
                    remaining[0]--;
                    if (remaining[0] == 0) {
                        reviewAdapter.updateReviews(reviewList, reviewKeys);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ReviewFragment", "Failed to fetch data: " + error.getMessage());
                    remaining[0]--;
                }
            });
        }
    }

    private Review parseReview(DataSnapshot reviewSnap) {
        String mainMenu = reviewSnap.child("Mainmenu").getValue(String.class);
        String subMenu = reviewSnap.child("Submenu").getValue(String.class);
        String userReview = reviewSnap.child("userReview").getValue(String.class);
        int starCount = parseInt(reviewSnap.child("starCount").getValue(Integer.class), 0);
        Long timeValue = reviewSnap.child("time").getValue(Long.class);
        long timeMillis = (timeValue != null) ? timeValue : 0;
        int likeDifference = parseInt(reviewSnap.child("likeDifference").getValue(Integer.class), 0);
        return new Review(mainMenu, subMenu, userReview, starCount, timeMillis, likeDifference);
    }

    private int parseInt(Object value, int defaultValue) {
        try {
            if (value instanceof Integer) return (Integer) value;
            if (value instanceof String) return Integer.parseInt((String) value);
        } catch (Exception e) {
            Log.e("parseInt", "Failed to parse int: " + value, e);
        }
        return defaultValue;
    }
}
