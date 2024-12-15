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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
            // starCount 기준으로 내림차순 정렬
            Collections.sort(reviewList, (r1, r2) -> Float.compare(r2.getStarCount(), r1.getStarCount()));
            Log.d("ReviewFragment", "Sorted by starCount: " + reviewList.toString());
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
        // 로딩 화면 표시
        binding.progressLayout.setVisibility(View.VISIBLE);
        binding.reviewLayout.setVisibility(View.GONE);

        DatabaseReference whoWriteRef = FirebaseDatabase.getInstance().getReference("Category")
                .child(typeN)
                .child("Mainmenu")
                .child(mainMenu)
                .child("whoWriteReview");

        whoWriteRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                List<String> userIds = new ArrayList<>();
                for (DataSnapshot userIdSnap : task.getResult().getChildren()) {
                    String userId = userIdSnap.getKey();
                    if (userId != null) userIds.add(userId);
                }

                if (userIds.isEmpty()) {
                    Toast.makeText(getContext(), "리뷰가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    fetchReviewsAndKeys(userIds, mainMenu);
                }
            } else {
                Toast.makeText(getContext(), "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }

            // 로딩 화면 숨기기, 실제 화면 표시
            binding.progressLayout.setVisibility(View.GONE);
            binding.reviewLayout.setVisibility(View.VISIBLE);
        });
    }
    private void fetchReviewsAndKeys(List<String> userIds, String mainMenu) {
        reviewList.clear();
        reviewKeys.clear(); // 키 리스트 초기화

        List<Task<DataSnapshot>> tasks = new ArrayList<>();
        for (String userId : userIds) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("myReviewData");
            tasks.add(userRef.get());
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
            float totalStar = 0; // 총 starCount
            int reviewCount = 0; // 리뷰 개수

            for (Task<DataSnapshot> individualTask : tasks) {
                if (individualTask.isSuccessful() && individualTask.getResult() != null) {
                    DataSnapshot reviewDataSnapshot = individualTask.getResult();
                    for (DataSnapshot reviewSnap : reviewDataSnapshot.getChildren()) {
                        String reviewMainMenu = reviewSnap.child("Mainmenu").getValue(String.class);
                        if (mainMenu.equals(reviewMainMenu)) {
                            Review review = parseReview(reviewSnap);
                            String reviewKey = reviewSnap.getKey(); // 키 가져오기
                            String userId = reviewSnap.getRef().getParent().getParent().getKey();

                            if (userId != null && reviewKey != null) {
                                loadUserInfoForReview(userId, review, reviewKey);
                            }

                            // 총 starCount 및 리뷰 개수 계산
                            totalStar += review.getStarCount();
                            reviewCount++;
                        }
                    }
                }
            }
            // 평균 평점 계산
            float averageStar = (reviewCount > 0) ? (totalStar / reviewCount) : 0;

            // UI에 평균 평점 표시
            String formattedAvg = String.format(Locale.KOREA, "%.1f", averageStar);
            binding.scoreNum.setText(formattedAvg);

            // 별 이미지 업데이트
            updateStarImages(averageStar);
        });
    }


    private void loadUserInfoForReview(String userId, Review review, String reviewKey) {
        DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("userinfo");
        userInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // `userinfo` 데이터 로드
                String username =  snapshot.child("userName").getValue(String.class);
                int saltPreference = parseInt(snapshot.child("saltPreference").getValue(String.class), 0);
                int spicyPreference = parseInt(snapshot.child("spicyPreference").getValue(String.class), 0);

                List<String> allergyList = new ArrayList<>();
                for (DataSnapshot allergySnap : snapshot.child("allergies").getChildren()) {
                    String allergy = allergySnap.getValue(String.class);
                    if (allergy != null) allergyList.add(allergy);
                }
                String allergies = allergyList.isEmpty() ? "없음" : String.join(", ", allergyList);

                // 리뷰 객체에 추가
                review.setUsername(username);
                review.setSaltPreference(saltPreference);
                review.setSpicyPreference(spicyPreference);
                review.setAllergies(allergies);

                // 리뷰와 키를 동기화하여 리스트에 추가
                synchronized (reviewList) {
                    reviewList.add(review);
                    reviewKeys.add(reviewKey); // 키도 함께 추가
                    reviewAdapter.updateReviews(new ArrayList<>(reviewList), new ArrayList<>(reviewKeys));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadUserInfoForReview", "Failed to load user info for review: " + error.getMessage());
            }
        });
    }


    private Review parseReview(DataSnapshot reviewSnap) {
        String mainMenu = reviewSnap.child("Mainmenu").getValue(String.class);
        String subMenu = reviewSnap.child("Submenu").getValue(String.class);
        String userReview = reviewSnap.child("userReview").getValue(String.class);
        int starCount = parseInt(reviewSnap.child("starCount").getValue(Integer.class), 0);
        Long timeValue = reviewSnap.child("time").getValue(Long.class);
        long timeMillis = (timeValue != null) ? timeValue : 0;

        // 시간 포맷팅
        String formattedTime = "알 수 없음";
        if (timeMillis > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            Date date = new Date(timeMillis);
            formattedTime = sdf.format(date);
        }

        int likeDifference = parseInt(reviewSnap.child("likeDifference").getValue(Integer.class), 0);

        // 리뷰 객체 생성 (userinfo 데이터 없이 초기화)
        Review review = new Review(mainMenu, subMenu, userReview, starCount, timeMillis, likeDifference);
        review.setReviewTime(formattedTime);

        return review;
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