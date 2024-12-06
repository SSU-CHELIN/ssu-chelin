package com.example.ssuchelin.review;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reviewAdapter = new ReviewAdapter(reviewList, new ArrayList<>(), "Unknown ID");
        binding.reviewRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.reviewRecyclerView.setAdapter(reviewAdapter);

        // Firebase에서 리뷰 데이터 가져오기
        String selectedMainMenu = binding.foodMainMenu.getText().toString();
        fetchReviews(selectedMainMenu);

        binding.writeReview.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mainMenu", binding.foodMainMenu.getText().toString());
            bundle.putString("subMenu", binding.foodSubMenu.getText().toString());
            bundle.putString("category", binding.foodCategory.getText().toString());
            bundle.putParcelable("imageBitmap", (((BitmapDrawable) binding.foodImage.getDrawable()).getBitmap()));

            WriteReviewFragment writeReviewFragment = new WriteReviewFragment();
            writeReviewFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, writeReviewFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void fetchReviews(String selectedMainMenu) {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User");

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewList.clear(); // 기존 리스트 초기화
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot reviewDataSnapshot = userSnapshot.child("myReviewData");
                    for (DataSnapshot reviewSnapshot : reviewDataSnapshot.getChildren()) {
                        String mainMenu = reviewSnapshot.child("Mainmenu").getValue(String.class);
                        if (mainMenu != null && mainMenu.equals(selectedMainMenu)) {
                            // Firebase에서 데이터 가져오기
                            String subMenu = reviewSnapshot.child("Submenu").getValue(String.class);
                            String userReview = reviewSnapshot.child("userReview").getValue(String.class);
                            int starCount = reviewSnapshot.child("starCount").getValue(Integer.class) != null
                                    ? reviewSnapshot.child("starCount").getValue(Integer.class)
                                    : 0;

                            // 추가 데이터
                            String username = userSnapshot.child("UserAccount").child("username").getValue(String.class);
                            int saltPreference = reviewSnapshot.child("saltPreference").getValue(Integer.class) != null
                                    ? reviewSnapshot.child("saltPreference").getValue(Integer.class)
                                    : 0;
                            int spicyPreference = reviewSnapshot.child("spicyPreference").getValue(Integer.class) != null
                                    ? reviewSnapshot.child("spicyPreference").getValue(Integer.class)
                                    : 0;
                            String allergies = reviewSnapshot.child("allergies").getValue(String.class);

                            // Review 객체 생성
                            Review review = new Review(mainMenu, subMenu, userReview, starCount);
                            review.setUsername(username != null ? username : "Unknown");
                            review.setSaltPreference(saltPreference);
                            review.setSpicyPreference(spicyPreference);
                            review.setAllergies(allergies != null ? allergies : "없음");

                            // 리스트에 추가
                            reviewList.add(review);
                        }
                    }
                }
                reviewAdapter.notifyDataSetChanged(); // RecyclerView 갱신
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "데이터를 불러오지 못했습니다: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
