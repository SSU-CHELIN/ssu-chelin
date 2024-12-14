package com.example.ssuchelin.user;

import android.content.Context;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ssuchelin.R;
import com.example.ssuchelin.login.LoginActivity;
import com.example.ssuchelin.review.CheckReviewsFragment;
import com.example.ssuchelin.review.FeedbackFragment;
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
    private View profileLoadingLayout, profileContentLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_view, container, false);

        // 로딩 및 콘텐츠 레이아웃 초기화
        profileLoadingLayout = view.findViewById(R.id.profile_loading_layout);
        profileContentLayout = view.findViewById(R.id.profile_content_layout);

        // View 초기화
        studentIdTextView = view.findViewById(R.id.student_id);
        usernameTextView = view.findViewById(R.id.username);
        changeInitialSettings = view.findViewById(R.id.change_initial_settings);
        changeFirstSettings = view.findViewById(R.id.change_first_settings);
        checkReviews = view.findViewById(R.id.check_reviews);
        termsOfService = view.findViewById(R.id.terms_of_service);
        privacyPolicy = view.findViewById(R.id.privacy_policy);
        contactUs = view.findViewById(R.id.contact_us);
        logout = view.findViewById(R.id.logout);

        // SharedPreferences에서 student ID 가져오기
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

        studentIdTextView.setText(studentId);

        // 데이터 로드
        showLoading(true);
        fetchUsernameFromDatabase(studentId);

        // 버튼 리스너 설정
        setButtonListeners();

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
                    Toast.makeText(getActivity(), "사용자 데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    usernameTextView.setText("Unknown User");
                }
                showLoading(false); // 로딩 완료
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getActivity(), "데이터베이스 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                showLoading(false); // 로딩 완료
            }
        });
    }

    private void showLoading(boolean isLoading) {
        profileLoadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        profileContentLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void setButtonListeners() {
        changeInitialSettings.setOnClickListener(v -> {
            switchFragment(new InitialSettingFragment());
        });

        changeFirstSettings.setOnClickListener(v -> {
            switchFragment(new FirstSettingFragment());
        });

        checkReviews.setOnClickListener(v -> {
            switchFragment(new CheckReviewsFragment());
        });

        contactUs.setOnClickListener(v -> {
            switchFragment(new FeedbackFragment());
        });

        termsOfService.setOnClickListener(v -> {
            switchFragment(new WriteReviewFragment());
        });

        logout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인", (dialog, which) -> logout())
                    .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void switchFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isLoggedIn", false); // 로그아웃 상태로 저장
        editor.apply();

        Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 현재 스택을 정리하고 새로운 액티비티 시작
        startActivity(intent);
    }
}
