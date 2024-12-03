package com.example.ssuchelin.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ssuchelin.review.CheckReviewsFragment;
import com.example.ssuchelin.R;
import com.example.ssuchelin.review.FeedbackFragment;
import com.example.ssuchelin.login.LoginActivity;
import com.example.ssuchelin.review.WriteReviewFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewFragment extends Fragment {

    private DatabaseReference database;
    private TextView studentIdTextView, usernameTextView;
    private TextView changeInitialSettings, changeFirstSettings, checkReviews, termsOfService, privacyPolicy, contactUs, logout;
    private String studentId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_view, container, false);

        view.findViewById(R.id.change_initial_settings).setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new InitialSettingFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        view.findViewById(R.id.change_first_settings).setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new FirstSettingFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        view.findViewById(R.id.check_reviews).setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new CheckReviewsFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        view.findViewById(R.id.contact_us).setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new FeedbackFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        view.findViewById(R.id.terms_of_service).setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new WriteReviewFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        privacyPolicy = view.findViewById(R.id.privacy_policy);
        logout = view.findViewById(R.id.logout);

        // Retrieve the sb tudent ID from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

        // SharedPreferences에서 student ID 가져오기
        studentIdTextView = view.findViewById(R.id.student_id);
        usernameTextView = view.findViewById(R.id.username);

        studentIdTextView.setText(studentId);
        fetchUsernameFromDatabase(studentId);

        logout.setOnClickListener(v -> logout());

        return view;
    }


    private void fetchUsernameFromDatabase(String studentId) {
        database = FirebaseDatabase.getInstance().getReference("User").child(studentId).child("userinfo");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("userName").getValue(String.class);
                    if (username != null) {
                        usernameTextView.setText(username);
                    } else {
                        usernameTextView.setText("Unknown User");
                    }
                } else {
                    Toast.makeText(getActivity(), "User data not found.", Toast.LENGTH_SHORT).show();
                    usernameTextView.setText("Unknown User");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getActivity(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


    private void logout() {
        Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 현재 스택을 정리하고 새로운 액티비티 시작
        startActivity(intent);
    }

}
