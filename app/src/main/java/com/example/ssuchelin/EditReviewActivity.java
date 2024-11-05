package com.example.ssuchelin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// 리뷰 수정

public class EditReviewActivity extends AppCompatActivity {

    private EditText reviewContent;
    private Button saveButton;
    private DatabaseReference reviewRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review); //리뷰 작성화면 xml

        reviewContent = findViewById(R.id.review_content_edit);
        saveButton = findViewById(R.id.save_button);

        String reviewId = getIntent().getStringExtra("review_id");
        reviewRef = FirebaseDatabase.getInstance().getReference("reviews/user_id").child(reviewId); // 파베 , Firebase Database Reference 초기화

        saveButton.setOnClickListener(v -> {
            String newContent = reviewContent.getText().toString().trim();
            if (!newContent.isEmpty()) {
                reviewRef.child("content").setValue(newContent).addOnSuccessListener(aVoid -> {
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


