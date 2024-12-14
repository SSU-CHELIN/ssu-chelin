package com.example.ssuchelin.user;

import static android.content.Context.MODE_PRIVATE;

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
    private View profileLoadingLayout, profileContentLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_view, container, false);

        // 로딩 및 콘텐츠 레이아웃 초기화
        profileLoadingLayout = view.findViewById(R.id.profile_loading_layout);
        profileContentLayout = view.findViewById(R.id.profile_content_layout);

        studentIdTextView = view.findViewById(R.id.student_id);
        usernameTextView = view.findViewById(R.id.username);

        // SharedPreferences에서 student ID 가져오기
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

        studentIdTextView.setText(studentId);

        // 데이터 로드
        showLoading(true);
        fetchUsernameFromDatabase(studentId);

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
}
