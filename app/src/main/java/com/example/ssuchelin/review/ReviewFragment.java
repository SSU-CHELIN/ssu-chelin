package com.example.ssuchelin.review;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button; // 추가: 추천순/최신순 버튼 제어

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
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ReviewFragment extends Fragment {
    private FragmentReviewBinding binding;
    private List<Review> reviewList = new ArrayList<>();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        reviewAdapter = new ReviewAdapter(reviewList, new ArrayList<>(), "Unknown ID");
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

        toolbar.setNavigationOnClickListener(v -> {
            if (!requireActivity().getSupportFragmentManager().isStateSaved()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // 추천순, 최신순 버튼 참조
        recommendBtn = view.findViewById(R.id.recommend);
        latestBtn = view.findViewById(R.id.latest);

        recommendBtn.setOnClickListener(v->{
            // 추천순 정렬: likeDifference 내림차순
            // tie 시 무작위? tie면 정렬상 같으면 정렬 순서 안정적이므로 그냥 내림차순이면 tie이면 순서는 구현체에 따라 결정됨
            // 요구사항: tie시 무작위 허용 -> 굳이 무작위 섞지 않아도 됨.
            // 추천순 정렬
            Collections.sort(reviewList, (r1, r2) -> {
                int diff = r2.getLikeDifference() - r1.getLikeDifference();
                return diff == 0 ? 0 : diff; // tie 처리
            });

            Log.d("ReviewFragment", "Sorted by recommendation: " + reviewList.toString());
            reviewAdapter.updateReviews(reviewList);
        });

        latestBtn.setOnClickListener(v -> {
            if (reviewList == null || reviewList.isEmpty()) {
                Log.e("ReviewFragment", "No reviews to sort by latest!");
                return;
            }

            // 최신순 정렬
            Collections.sort(reviewList, (r1, r2) -> Long.compare(r2.getTimeMillis(), r1.getTimeMillis()));

            Log.d("ReviewFragment", "Sorted by latest: " + reviewList.toString());
            reviewAdapter.updateReviews(reviewList);
        });

        // whoWriteReview에서 userId 목록 읽고 리뷰 로드
        fetchReviewsFromCategory(typeN, currentMainMenu);

        binding.writeReview.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mainMenu", binding.foodMainMenu.getText().toString());
            bundle.putString("subMenu", binding.foodSubMenu.getText().toString());
            bundle.putString("category", binding.foodCategory.getText().toString());
            bundle.putString("type",type);
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
                    // 리뷰 없음
                    Toast.makeText(getContext(), "리뷰가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    fetchReviewsAndCalculateAverage(userIds, mainMenu);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "데이터를 불러오지 못했습니다: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchReviewsAndCalculateAverage(List<String> userIds, String mainMenu) {
        reviewList.clear();
        final int[] remaining = {userIds.size()};
        final float[] totalStar = {0};
        final float[] reviewCount = {0};

        Runnable doneOneUser = () -> {
            remaining[0]--;
            if (remaining[0] == 0) {
                reviewAdapter.notifyDataSetChanged();
                float avg = 0;
                if (reviewCount[0] > 0) {
                    avg = totalStar[0] / reviewCount[0];
                }
                String formattedAvg = String.format("%.1f", avg);
                binding.scoreNum.setText(formattedAvg);
                updateStarImages(avg);
            }
        };

        for (String userId : userIds) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    DataSnapshot userInfoSnapshot = userSnapshot.child("userinfo");
                    if (!userInfoSnapshot.exists()) {
                        Log.d("ReviewFragment", "userinfo 데이터가 없음: " + userSnapshot.getKey());
                        doneOneUser.run();
                        return;
                    }

                    String username = userInfoSnapshot.child("userName").getValue(String.class);
                    if (username == null) username = "Unknown User";

                    int saltPreference = 0;
                    int spicyPreference = 0;
                    try {
                        Long saltPrefValue = userInfoSnapshot.child("saltPreference").getValue(Long.class);
                        Long spicyPrefValue = userInfoSnapshot.child("spicyPreference").getValue(Long.class);
                        saltPreference = saltPrefValue != null ? saltPrefValue.intValue() : 0;
                        spicyPreference = spicyPrefValue != null ? spicyPrefValue.intValue() : 0;
                    } catch (Exception e) {
                        Log.e("ReviewFragment", "Error parsing preferences: " + e.getMessage());
                    }

                    List<String> allergyList = new ArrayList<>();
                    DataSnapshot allergiesSnapshot = userInfoSnapshot.child("allergies");
                    if (allergiesSnapshot.exists()) {
                        for (DataSnapshot allergySnapshot : allergiesSnapshot.getChildren()) {
                            String allergy = allergySnapshot.getValue(String.class);
                            if (allergy != null) {
                                allergyList.add(allergy);
                            }
                        }
                    }
                    String allergies = allergyList.isEmpty() ? "None" : String.join(", ", allergyList);

                    DataSnapshot reviewDataSnapshot = userSnapshot.child("myReviewData");
                    for (DataSnapshot reviewSnap : reviewDataSnapshot.getChildren()) {
                        String reviewMainMenu = reviewSnap.child("Mainmenu").getValue(String.class);
                        if (mainMenu.equals(reviewMainMenu)) {
                            String subMenu = reviewSnap.child("Submenu").getValue(String.class);
                            String userReview = reviewSnap.child("userReview").getValue(String.class);
                            Integer starCount = reviewSnap.child("starCount").getValue(Integer.class);
                            if (starCount == null) starCount = 0;

                            // likeDifference, timeMillis 추가로 가져오기
                            Integer likeDifference = reviewSnap.child("likeDifference").getValue(Integer.class);
                            if (likeDifference == null) likeDifference = 0;

                            Long timeVal = reviewSnap.child("time").getValue(Long.class);
                            long timeMillis = timeVal != null ? timeVal : 0;

                            Boolean liked = reviewSnap.child("liked").getValue(Boolean.class);
                            if (liked == null) liked = false;
                            Boolean disliked = reviewSnap.child("disliked").getValue(Boolean.class);
                            if (disliked == null) disliked = false;
                            Integer likeCount = reviewSnap.child("likeCount").getValue(Integer.class);
                            if (likeCount == null) likeCount = 0;
                            Integer dislikeCount = reviewSnap.child("dislikeCount").getValue(Integer.class);
                            if (dislikeCount == null) dislikeCount = 0;

                            Review review = new Review(mainMenu, subMenu, userReview != null ? userReview : "", starCount);
                            review.setUsername(username);
                            review.setSaltPreference(saltPreference);
                            review.setSpicyPreference(spicyPreference);
                            review.setAllergies(allergies);
                            review.setLikeDifference(likeDifference);
                            review.setTimeMillis(timeMillis);
                            review.setLiked(liked);
                            review.setDisliked(disliked);
                            review.setLikeCount(likeCount);
                            review.setDislikeCount(dislikeCount);

                            reviewList.add(review);
                            totalStar[0] += starCount;
                            reviewCount[0]++;
                        }
                    }
                    doneOneUser.run();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    doneOneUser.run();
                }
            });
        }
    }

    private void updateStarImages(float avg) {
        float portionStar1 = avg - 0;
        float portionStar2 = avg - 1;
        float portionStar3 = avg - 2;

        binding.starButton1.setImageResource(getStarResourceForPortion(portionStar1));
        binding.starButton2.setImageResource(getStarResourceForPortion(portionStar2));
        binding.starButton3.setImageResource(getStarResourceForPortion(portionStar3));
    }

    private int getStarResourceForPortion(float portion) {
        if (portion <= 0) {
            return R.drawable.star_0;
        } else if (portion >= 1) {
            return R.drawable.star_100;
        } else {
            float percent = portion * 100;
            float[] thresholds = {0,12.5f,25f,37.5f,50f,62.5f,75f,87.5f,100f};
            int[] drawables = {
                    R.drawable.star_0,
                    R.drawable.star_12_5,
                    R.drawable.star_25,
                    R.drawable.star_37_5,
                    R.drawable.star_50,
                    R.drawable.star_62_5,
                    R.drawable.star_75,
                    R.drawable.star_87_5,
                    R.drawable.star_100
            };

            float minDiff = Float.MAX_VALUE;
            int chosenIndex = 0;
            for (int i = 0; i < thresholds.length; i++) {
                float diff = Math.abs(percent - thresholds[i]);
                if (diff < minDiff) {
                    minDiff = diff;
                    chosenIndex = i;
                }
            }

            return drawables[chosenIndex];
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
