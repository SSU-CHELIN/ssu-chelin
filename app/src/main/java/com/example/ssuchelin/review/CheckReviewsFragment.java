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

        reviewsRecyclerView = view.findViewById(R.id.reviews_recycler_view);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

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
                if (saltPreference == null) saltPreference = "0";
                if (spicyPreference == null) spicyPreference = "0";

                loadReviews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "사용자 정보 로드 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReviews() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewsList.clear();
                List<String> reviewKeys = new ArrayList<>();

                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    String myReview = reviewSnapshot.child("userReview").getValue(String.class);
                    Integer starCountValue = reviewSnapshot.child("starCount").getValue(Integer.class);
                    Boolean liked = reviewSnapshot.child("liked").getValue(Boolean.class);
                    Integer likeCount = reviewSnapshot.child("likeCount").getValue(Integer.class);

                    Boolean disliked = reviewSnapshot.child("disliked").getValue(Boolean.class);
                    Integer dislikeCount = reviewSnapshot.child("dislikeCount").getValue(Integer.class);

                    float starCount = starCountValue != null ? starCountValue : 0;
                    if (liked == null) liked = false;
                    if (likeCount == null) likeCount = 0;
                    if (disliked == null) disliked = false;
                    if (dislikeCount == null) dislikeCount = 0;

                    Review review = new Review(
                            username,
                            myReview,
                            starCount,
                            Integer.parseInt(saltPreference),
                            Integer.parseInt(spicyPreference),
                            allergies,
                            liked,
                            likeCount,
                            disliked,
                            dislikeCount
                    );

                    reviewKeys.add(reviewSnapshot.getKey());
                    reviewsList.add(review);
                }

                reviewAdapter = new ReviewAdapter(reviewsList, reviewKeys, studentId);
                reviewsRecyclerView.setAdapter(reviewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "데이터 로드 실패: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
