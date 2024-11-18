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

public class FeedbackActivity extends AppCompatActivity {

    private EditText feedbackInput; // 사용자 의견을 입력할 EditText 필드
    private Button submitButton; // 의견을 제출하는 Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback); // 문의하기 화면 레이아웃 설정

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // 제목 숨기기
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 기본 뒤로가기 아이콘 활성화
        }
        toolbar.setNavigationOnClickListener(v -> finish()); // 뒤로가기 클릭 시 액티비티 종료

        // 의견 입력 필드와 제출 버튼 초기화
        feedbackInput = findViewById(R.id.feedback_input); // 사용자 의견을 입력할 EditText 필드 초기화
        submitButton = findViewById(R.id.submit_button); // 제출 버튼 초기화

        // 제출 버튼 클릭 리스너 설정
        submitButton.setOnClickListener(v -> submitFeedback()); // 제출 버튼 클릭 시 submitFeedback 메서드 호출
    }

    private void submitFeedback() {
        String feedbackText = feedbackInput.getText().toString().trim(); // 입력된 의견 텍스트 가져오기

        if (!feedbackText.isEmpty()) {
            // Firebase Database 참조 , 파베
            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks");

            // 새로운 피드백 ID 생성 후 저장 , 파베
            String feedbackId = feedbackRef.push().getKey(); // Firebase에서 고유 피드백 ID 생성
            if (feedbackId != null) {
                feedbackRef.child(feedbackId).setValue(feedbackText) // 피드백을 Firebase에 저장
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(FeedbackActivity.this, "의견이 제출되었습니다.", Toast.LENGTH_SHORT).show(); // 성공 메시지 표시
                            feedbackInput.setText("");  // 입력 필드 초기화
                        })
                        .addOnFailureListener(e -> Toast.makeText(FeedbackActivity.this, "제출 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show()); // 실패 시 오류 메시지 표시
            }
        } else {
            Toast.makeText(this, "의견을 입력하세요.", Toast.LENGTH_SHORT).show(); // 의견이 비어있는 경우 경고 메시지 표시
        }
    }
}
