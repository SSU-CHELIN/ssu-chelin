package com.example.ssuchelin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewActivity extends BaseActivity {

    private DatabaseReference database;
    private TextView studentIdTextView, usernameTextView;
    private TextView changeProfile, changeInitialSettings, checkReviews, termsOfService, privacyPolicy, contactUs, logout;
    private String studentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentLayout(R.layout.activity_profile_view);

        changeProfile = findViewById(R.id.change_profile);
        changeInitialSettings = findViewById(R.id.change_initial_settings);
        checkReviews = findViewById(R.id.check_reviews);
        termsOfService = findViewById(R.id.terms_of_service);
        privacyPolicy = findViewById(R.id.privacy_policy);
        contactUs = findViewById(R.id.contact_us);
        logout = findViewById(R.id.logout);



        // Retrieve the student ID from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

        studentIdTextView = findViewById(R.id.student_id);
        usernameTextView = findViewById(R.id.username);

        // Display the student ID
        studentIdTextView.setText(studentId);

        // Fetch the username from Firebase using the student ID
        fetchUsernameFromDatabase(studentId);

        // OnClickListener 설정
        //changeProfile.setOnClickListener(view -> navigateToChangeProfile());
        changeInitialSettings.setOnClickListener(view -> navigateToInitialSettings());
        checkReviews.setOnClickListener(view -> navigateToCheckReviews());
        termsOfService.setOnClickListener(view -> navigateToTermsOfService());
        //privacyPolicy.setOnClickListener(view -> navigateToPrivacyPolicy());
        contactUs.setOnClickListener(view -> navigateToFeedback());
        logout.setOnClickListener(view -> logout());
    }

    private void navigateToInitialSettings() {
        // 초기 설정 화면으로 이동
        Intent intent = new Intent(ProfileViewActivity.this, FirstSettingActivity.class);
        startActivity(intent);

    }

    private void navigateToCheckReviews() {
        // 초기 설정 화면으로 이동
        Intent intent = new Intent(ProfileViewActivity.this, CheckReviewsActivity.class);
        startActivity(intent);

    }

    private void navigateToPrivacyPolicy() {
        // 개인정보 처리방침 화면으로 이동
        Intent intent = new Intent(ProfileViewActivity.this, InitialSettingActivity.class);
        startActivity(intent);
    }

    private void navigateToTermsOfService() {
        // 초기 설정 화면으로 이동
        Intent intent = new Intent(ProfileViewActivity.this, WriteReviewActivity.class);
        startActivity(intent);

    }

    private void navigateToFeedback() {
        // 문의하기 화면으로 이동
        Intent intent = new Intent(this, FeedbackActivity.class);
        Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }


    private void logout() {
        Intent intent = new Intent(this,FirstloginActivity.class);
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        startActivity(intent);
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
                    Toast.makeText(ProfileViewActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                    usernameTextView.setText("Unknown User");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProfileViewActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
