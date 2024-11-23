package com.example.ssuchelin.review;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
import java.util.concurrent.atomic.AtomicInteger;

public class WriteReviewActivity extends AppCompatActivity {

    private EditText reviewEditText;
    private TextView characterCounterTextView,mainmenu;
    private ImageButton starButton1, starButton2, starButton3;
    private boolean isStar1On = false, isStar2On = false, isStar3On = false; // 각 버튼의 상태 저장
    private int starCount = 0;
    private DatabaseReference mDatabaseRef;
    private Button submitbtn;
    private boolean isEditMode = false;
    private int reviewId = -1;
    private int score = 0; // 추가 필드: 점수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        // XML 참조
        starButton1 = findViewById(R.id.starButton1);
        starButton2 = findViewById(R.id.starButton2);
        starButton3 = findViewById(R.id.starButton3);
        reviewEditText = findViewById(R.id.review_edit_text);
        submitbtn = findViewById(R.id.submit_button);
        characterCounterTextView = findViewById(R.id.character_counter);
        mainmenu = findViewById(R.id.main_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 수정 모드 판단
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("editMode", false);
        String reviewText = intent.getStringExtra("reviewText");
        reviewId = intent.getIntExtra("reviewId", -1);
        starCount = intent.getIntExtra("starCount", 0);

        if (isEditMode) {
            reviewEditText.setText(reviewText);
            submitbtn.setText("수정하기"); // 버튼 텍스트 변경
            updateStarButtons(); // 별점 UI 업데이트
        }

        // EditText에 TextWatcher 설정
        reviewEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int textLength = s.length();
                characterCounterTextView.setText(textLength + " / 300");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 별 버튼 클릭 리스너 설정
        starButton1.setOnClickListener(starClickListener);
        starButton2.setOnClickListener(starClickListener);
        starButton3.setOnClickListener(starClickListener);

        // SharedPreferences에서 사용자 ID 가져오기
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

        // 제출 버튼 리스너
        submitbtn.setOnClickListener(view -> {
            String review = reviewEditText.getText().toString();

            if (isEditMode) {
                updateUserReview(studentId, reviewId, review);
            } else {
                saveUserReview(studentId, review);
            }
            finish();
        });
    }

    // 클릭 리스너 정의
    private final View.OnClickListener starClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.starButton1) {
                isStar1On = !isStar1On;
                starButton1.setImageResource(isStar1On ? R.drawable.star : R.drawable.star_off);
                starCount = isStar1On ? starCount + 1 : starCount - 1;
            } else if (view.getId() == R.id.starButton2) {
                isStar2On = !isStar2On;
                starButton2.setImageResource(isStar2On ? R.drawable.star : R.drawable.star_off);
                starCount = isStar2On ? starCount + 1 : starCount - 1;
            } else if (view.getId() == R.id.starButton3) {
                isStar3On = !isStar3On;
                starButton3.setImageResource(isStar3On ? R.drawable.star : R.drawable.star_off);
                starCount = isStar3On ? starCount + 1 : starCount - 1;
            }
        }
    };

    // 별 UI 업데이트
    private void updateStarButtons() {
        starButton1.setImageResource(starCount >= 1 ? R.drawable.star : R.drawable.star_off);
        starButton2.setImageResource(starCount >= 2 ? R.drawable.star : R.drawable.star_off);
        starButton3.setImageResource(starCount >= 3 ? R.drawable.star : R.drawable.star_off);
    }


    // 리뷰 저장
    private void saveUserReview(String studentId, String review) {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);
        mDatabaseRef.child("reviewCnt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                AtomicInteger reviewCnt = new AtomicInteger(snapshot.exists() ? snapshot.getValue(Integer.class) : 0);
                reviewCnt.getAndIncrement();

                String reviewKey = "review_" + reviewCnt.get();
                Map<String, Object> userReview = new HashMap<>();
                userReview.put("userReview", review);
                userReview.put("starCount", starCount);
                userReview.put("score", score);
                userReview.put("Mainmenu",mainmenu.getText().toString());

                mDatabaseRef.child("myReviewData").child(reviewKey).setValue(userReview).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mDatabaseRef.child("reviewCnt").setValue(reviewCnt.get());
                        Toast.makeText(WriteReviewActivity.this, "리뷰가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WriteReviewActivity.this, "리뷰 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(WriteReviewActivity.this, "데이터베이스 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 리뷰 업데이트
    private void updateUserReview(String studentId, int reviewId, String updatedReview) {
        String reviewKey = "review_" + (reviewId + 1);

        DatabaseReference reviewRef = FirebaseDatabase.getInstance()
                .getReference("User")
                .child(studentId)
                .child("myReviewData")
                .child(reviewKey);

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("userReview", updatedReview);
        updatedData.put("starCount", starCount);
        updatedData.put("score", score);

        reviewRef.updateChildren(updatedData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(WriteReviewActivity.this, "리뷰가 수정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(WriteReviewActivity.this, "리뷰 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
