package com.example.ssuchelin.review;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import java.util.List;

public class ReviewFragment extends Fragment {
    private FragmentReviewBinding binding;
    private List<Review> reviewList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;
    private DatabaseReference mDatabaseRef;
    private String type;

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
        }

        // /// 수정 부분: type 변수 가져오기
        type = getArguments().getString("type");

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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

        String typeN = null;
        if (type == "ddook") typeN = "뚝배기 코너";
        if (type == "dub") typeN = "덮밥 코너";
        if (type == "yang") typeN = "양식 코너";

        String selectedMainMenu = binding.foodMainMenu.getText().toString();

        // /// 수정 부분: whoWriteReview에서 userId 목록 가져와 해당 유저 리뷰 로드 및 평균 계산
        fetchReviewsFromCategory(typeN, selectedMainMenu);

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

    // /// 수정 부분: Category에서 whoWriteReview 읽고 해당 userId의 리뷰 로드
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

        // /// 수정 부분: doneOneUser 메서드를 Runnable로 정의
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
                // /// 수정 부분: avg에 따라 별 이미지 변경
                updateStarImages(avg);
            }
        };

        for (String userId : userIds) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    // userinfo 데이터 가져오기
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

                            Review review = new Review(mainMenu, subMenu, userReview != null ? userReview : "", starCount);
                            review.setUsername(username);
                            review.setSaltPreference(saltPreference);
                            review.setSpicyPreference(spicyPreference);
                            review.setAllergies(allergies);

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

    // /// 수정 부분: avg 값(0~3)을 이용해 starButton1, starButton2, starButton3의 이미지를 변경
    private void updateStarImages(float avg) {
        // 각 별은 1점(=100%)에 해당
        // 별의 portion 계산: portion = avg - i (i=0,1,2)
        // portion <=0 => star_0
        // portion >=1 => star_100
        // 그 사이 => 근접한 12.5% 단위 이미지 선택

        float portionStar1 = avg - 0;
        float portionStar2 = avg - 1;
        float portionStar3 = avg - 2;

        binding.starButton1.setImageResource(getStarResourceForPortion(portionStar1));
        binding.starButton2.setImageResource(getStarResourceForPortion(portionStar2));
        binding.starButton3.setImageResource(getStarResourceForPortion(portionStar3));
    }

    // /// 수정 부분: portion(0~1 사이 값)에 따라 알맞은 star_x 이미지 반환
    // portion <0 => 0%
    // portion >1 => 100%
    // 그 사이이면 0,12.5%,25%,37.5%,50%,62.5%,75%,87.5%,100% 중 가장 가까운 값 선택
    private int getStarResourceForPortion(float portion) {
        if (portion <= 0) {
            return R.drawable.star_0;
        } else if (portion >= 1) {
            return R.drawable.star_100;
        } else {
            float percent = portion * 100;
            // 가능한 값: 0,12.5,25,37.5,50,62.5,75,87.5,100
            // percent와 가장 가까운 값 찾기
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
