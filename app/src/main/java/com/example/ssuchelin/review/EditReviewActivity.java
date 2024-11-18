package com.example.ssuchelin.review;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ssuchelin.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// 리뷰 수정 화면을 구현하는 클래스
public class EditReviewActivity extends AppCompatActivity {

    private EditText reviewContent; // 리뷰 내용을 입력할 EditText
    private Button saveButton; // 저장 버튼
    private DatabaseReference reviewRef; // Firebase 데이터베이스 참조 객체, 리뷰 데이터를 수정하는 데 사용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_review); // 리뷰 수정 화면 레이아웃 XML을 설정

//        reviewContent = findViewById(R.id.review_content_edit); // 리뷰 내용을 입력받을 EditText 초기화
//        saveButton = findViewById(R.id.save_button); // 저장 버튼 초기화

        // 이전 화면에서 전달된 리뷰 ID를 받아옴
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        String reviewId = getIntent().getStringExtra("review_id");
        reviewRef = FirebaseDatabase.getInstance().getReference("reviews/user_id").child(reviewId); // 파베 , Firebase Database Reference 초기화

        // Firebase 데이터베이스에서 해당 리뷰의 경로를 참조, 'reviews/user_id' 경로 아래 reviewId로 리뷰를 찾음
        reviewRef = FirebaseDatabase.getInstance().getReference("reviews/user_id").child(reviewId);

        // 저장 버튼 클릭 리스너 설정
        saveButton.setOnClickListener(v -> {
            String newContent = reviewContent.getText().toString().trim(); // 입력된 새로운 리뷰 내용 가져오기
            if (!newContent.isEmpty()) { // 내용이 비어 있지 않은 경우에만 저장 시도
                // Firebase 데이터베이스에 새로운 리뷰 내용을 저장
                reviewRef.child("content").setValue(newContent).addOnSuccessListener(aVoid -> {
                    // 성공적으로 저장되면 메시지를 표시하고 화면을 닫음
                    Toast.makeText(EditReviewActivity.this, "리뷰가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    // 저장 실패 시 실패 메시지 표시
                    Toast.makeText(EditReviewActivity.this, "수정 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                // 내용이 비어 있는 경우 경고 메시지 표시
                Toast.makeText(EditReviewActivity.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


