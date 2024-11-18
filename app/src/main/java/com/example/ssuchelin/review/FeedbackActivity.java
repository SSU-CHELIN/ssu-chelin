package com.example.ssuchelin.review;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

// 문의하기 화면

public class FeedbackActivity extends AppCompatActivity {

    private EditText feedbackInput;
    private Button submitButton;
    private DatabaseReference databse;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Retrieve the student ID from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // 의견 입력 필드와 제출 버튼 초기화
        feedbackInput = findViewById(R.id.feedback_input);
        submitButton = findViewById(R.id.submit_button);

        // 제출 버튼 클릭 리스너 설정
        submitButton.setOnClickListener(v -> submitFeedback(studentId,feedbackInput.getText().toString()));
    }

    private void submitFeedback(String studentId,String feedbackInput) {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);

        if (feedbackInput.isEmpty()) {
            Toast.makeText(this, "피드백 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 카운터 값을 가져와서 feedback1, feedback2 순으로 저장
        mDatabaseRef.child("Feedback").child("cnt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final long currentCount = snapshot.exists() && snapshot.getValue(Long.class) != null
                        ? snapshot.getValue(Long.class)
                        : 0;


                // 새로운 feedback 키 생성 (feedback1, feedback2 ...)
                String feedbackKey = "feedback" + (currentCount + 1);

                // 데이터를 저장할 해시맵 생성
                Map<String, Object> feedbackData = new HashMap<>();
                feedbackData.put(feedbackKey, feedbackInput);

                // 데이터를 Firebase에 저장
                mDatabaseRef.child("Feedback").updateChildren(feedbackData)
                        .addOnSuccessListener(aVoid -> {
                            // 카운터 값 업데이트
                            mDatabaseRef.child("Feedback").child("cnt").setValue(currentCount + 1)
                                    .addOnSuccessListener(aVoid1 -> Toast.makeText(FeedbackActivity.this, "피드백이 저장되었습니다.", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(FeedbackActivity.this, "카운터 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .addOnFailureListener(e -> Toast.makeText(FeedbackActivity.this, "피드백 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FeedbackActivity.this, "피드백 저장 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}
