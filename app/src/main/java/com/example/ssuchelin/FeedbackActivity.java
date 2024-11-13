package com.example.ssuchelin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// 문의하기 화면

public class FeedbackActivity extends BaseActivity {

    private EditText feedbackInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_feedback);

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
        submitButton.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String feedbackText = feedbackInput.getText().toString().trim();

        if (!feedbackText.isEmpty()) {
            // Firebase Database 참조 , 파베
            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks");

            // 새로운 피드백 ID 생성 후 저장 , 파베
            String feedbackId = feedbackRef.push().getKey();
            if (feedbackId != null) {
                feedbackRef.child(feedbackId).setValue(feedbackText)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(FeedbackActivity.this, "의견이 제출되었습니다.", Toast.LENGTH_SHORT).show();
                            feedbackInput.setText("");  // 입력 필드 초기화
                        })
                        .addOnFailureListener(e -> Toast.makeText(FeedbackActivity.this, "제출 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        } else {
            Toast.makeText(this, "의견을 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }


}
