package com.example.ssuchelin.review;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckReviewsFragment extends Fragment {

    private DatabaseReference databaseReference;
    private DatabaseReference userInfoReference;
    private RecyclerView reviewsRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewsList = new ArrayList<>();
    private String username;
    private String saltPreference;
    private String spicyPreference;
    private String allergies;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_reviews, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

        // Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(studentId).child("myReviewData");
        userInfoReference = FirebaseDatabase.getInstance().getReference("User").child(studentId).child("userinfo");


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


        reviewsRecyclerView = view.findViewById(R.id.reviews_recycler_view); // RecyclerView 초기화
        // LinearLayoutManager 설정 (리스트 형태로 표시)
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        reviewsRecyclerView.setLayoutManager(layoutManager); // LayoutManager 설정

        // 어댑터 설정
        reviewAdapter = new ReviewAdapter(reviewsList);
        reviewsRecyclerView.setAdapter(reviewAdapter);

        // 사용자 정보 로드
        loadUserInfo();

        return view;
    }

    private void loadUserInfo() {
        userInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = snapshot.child("userName").getValue(String.class);
                saltPreference = snapshot.child("saltPreference").getValue(String.class);
                spicyPreference = snapshot.child("spicyPreference").getValue(String.class);

                // 알레르기 정보를 리스트로 가져옴
                List<String> allergyList = new ArrayList<>();
                DataSnapshot allergiesSnapshot = snapshot.child("allergies");
                if (allergiesSnapshot.exists()) {
                    for (DataSnapshot allergySnapshot : allergiesSnapshot.getChildren()) {
                        String allergy = allergySnapshot.getValue(String.class);
                        if (allergy != null) {
                            allergyList.add(allergy);
                        }
                    }
                }
                allergies = allergyList.isEmpty() ? "None" : String.join(", ", allergyList);

                if (username == null) username = "Unknown User";
                if (saltPreference == null) saltPreference = "Unknown";
                if (spicyPreference == null) spicyPreference = "Unknown";

                // 리뷰 데이터 로드
                loadReviews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "사용자 정보 로드 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReviews() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewsList.clear(); // 이전 리뷰 목록 초기화

                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    String myReview = reviewSnapshot.child("userReview").getValue(String.class);
                    Integer starCount = reviewSnapshot.child("starCount").getValue(Integer.class);

                    Review review = new Review(
                            username,
                            myReview,
                            starCount != null ? starCount : 0,
                            Integer.parseInt(saltPreference),  // 간 정도
                            Integer.parseInt(spicyPreference), // 맵기 정도
                            allergies
                    );

                    // 리스트에 추가
                    reviewsList.add(review);
                }

                // 어댑터에 데이터 전달
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "데이터 로드 실패: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
