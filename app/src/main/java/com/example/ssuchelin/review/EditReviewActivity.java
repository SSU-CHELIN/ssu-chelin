package com.example.ssuchelin.review;

import android.os.Bundle;
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

public class EditReviewActivity extends AppCompatActivity {

    private EditText reviewContent; // 리뷰 내용을 입력할 EditText
    private Button saveButton; // 저장 버튼
    private DatabaseReference reviewRef; // Firebase 데이터베이스 참조 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editreview); // 기존 레이아웃 활용

        reviewContent = findViewById(R.id.review_edit_text);
        saveButton = findViewById(R.id.submit_button);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("리뷰 수정");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // 이전 화면에서 전달된 데이터 수신
        String reviewId = getIntent().getStringExtra("review_id");
        String studentId = getIntent().getStringExtra("student_id");
        String username = getIntent().getStringExtra("username"); // 사용자 이름 추가

        if (reviewId == null || studentId == null) {
            Toast.makeText(this, "잘못된 리뷰 ID 또는 사용자 정보입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 사용자 이름 표시 (예: Toolbar나 TextView에 설정)
        if (username != null) {
            Toast.makeText(this, "사용자 이름: " + username, Toast.LENGTH_SHORT).show();
        }

        reviewRef = FirebaseDatabase.getInstance()
                .getReference("User")
                .child(studentId)
                .child("myReviewData")
                .child(reviewId);

        reviewRef.child("userReview").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String existingContent = task.getResult().getValue(String.class);
                if (existingContent != null) {
                    reviewContent.setText(existingContent);
                }
            } else {
                Toast.makeText(this, "리뷰 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> {
            String newContent = reviewContent.getText().toString().trim();
            if (!newContent.isEmpty()) {
                reviewRef.child("userReview").setValue(newContent).addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditReviewActivity.this, "리뷰가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(EditReviewActivity.this, "수정 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(EditReviewActivity.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
