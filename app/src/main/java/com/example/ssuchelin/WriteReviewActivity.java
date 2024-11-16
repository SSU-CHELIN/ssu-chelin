package com.example.ssuchelin;

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
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WriteReviewActivity extends AppCompatActivity {

    private EditText reviewEditText;
    private TextView characterCounterTextView;
    private String Review;
    private ImageButton starButton1, starButton2, starButton3;
    private boolean isStar1On = false, isStar2On = false, isStar3On = false;
    private int starCount = 0;
    private DatabaseReference database;
    private DatabaseReference mDatabaseRef;
    private Button submitbtn;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_write_review);

            // xml 참조
            starButton1 = findViewById(R.id.starButton1);
            starButton2 = findViewById(R.id.starButton2);
            starButton3 = findViewById(R.id.starButton3);
            reviewEditText = findViewById(R.id.review_edit_text);
            submitbtn = findViewById(R.id.submit_button);
            characterCounterTextView = findViewById(R.id.character_counter);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // 수정 모드 판단
            Intent intent = getIntent();
            boolean isEditMode = intent.getBooleanExtra("editMode", false); // 수정 모드 여부
            String reviewText = intent.getStringExtra("reviewText"); // 기존 리뷰 텍스트
            int reviewId = intent.getIntExtra("reviewId", -1); // 리뷰 ID

            if (isEditMode && reviewText != null) {
                // 수정 모드일 경우 기존 데이터 설정
                reviewEditText.setText(reviewText);
                submitbtn.setText("수정하기"); // 버튼 텍스트 변경

                // 기존 별점 설정 (Intent로 전달받은 값 활용)
                starCount = intent.getIntExtra("starCount", 0); // 기존 별점 수
                updateStarButtons(); // 별점 UI 업데이트
            }

            // EditText에 TextWatcher 설정
            reviewEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // 텍스트 변경 전 호출
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // 텍스트가 변경될 때 호출
                    int textLength = s.length();
                    characterCounterTextView.setText(textLength + " / 300"); // 글자 수 업데이트
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // 텍스트가 변경된 후 호출
                }
            });


            // 클릭 리스너 설정
            starButton1.setOnClickListener(starClickListener);
            starButton2.setOnClickListener(starClickListener);
            starButton3.setOnClickListener(starClickListener);

            // Retrieve the student ID from SharedPreferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

            submitbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Review = reviewEditText.getText().toString();

                    if (isEditMode) {
                        // 수정 모드: 기존 리뷰 업데이트
                        updateUserReview(studentId, reviewId, Review);
                    } else {
                        // 추가 모드: 새로운 리뷰 저장
                        saveUserReview(studentId, Review);
                    }
                    finish();
                }
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

    private void saveUserReview(String studentId, String review) {
        String myReview = review;

        // Firebase에서 해당 사용자 경로 설정
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);

        // reviewCnt 값을 가져와서 증가시킨 후 리뷰 저장
        mDatabaseRef.child("reviewCnt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // reviewCnt 값을 가져오거나 기본값 0으로 설정
                AtomicInteger reviewCnt = new AtomicInteger(snapshot.exists() ? snapshot.getValue(Integer.class) : 0);
                reviewCnt.getAndIncrement(); // reviewCnt 값을 1 증가

                // 증가된 reviewCnt 값을 리뷰 고유 키로 사용
                String reviewKey = "review_" + reviewCnt.get();

                // 리뷰 데이터 구성
                Map<String, Object> userReview = new HashMap<>();
                userReview.put("userReview", myReview);
                userReview.put("starCount", starCount);
                userReview.put("score", score);

                // 리뷰 데이터 저장
                mDatabaseRef.child("myReviewData").child(reviewKey).setValue(userReview)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // reviewCnt 값을 업데이트하여 Firebase에 저장
                                mDatabaseRef.child("reviewCnt").setValue(reviewCnt.get()).addOnCompleteListener(cntUpdateTask -> {
                                    if (cntUpdateTask.isSuccessful()) {
                                        Toast.makeText(WriteReviewActivity.this, "작성하신 리뷰가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(WriteReviewActivity.this, "리뷰 저장에는 성공했으나, 리뷰 카운트 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
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

    private void updateUserReview(String studentId, int reviewId, String updatedReview) {
        String reviewKey = "review_" + (reviewId+1);

        // Firebase에서 해당 리뷰 경로 설정
        DatabaseReference reviewRef = FirebaseDatabase.getInstance()
                .getReference("User")
                .child(studentId)
                .child("myReviewData")
                .child(reviewKey);

        // 업데이트할 데이터 구성
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("userReview", updatedReview);
        updatedData.put("starCount", starCount);
        updatedData.put("score", score);

        // Firebase에 업데이트
        reviewRef.updateChildren(updatedData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(WriteReviewActivity.this, "리뷰가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WriteReviewActivity.this, "리뷰 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 툴바의 뒤로가기 버튼 클릭 처리
        if (item.getItemId() == android.R.id.home) {
            // 현재 Activity 종료하여 이전 화면으로 돌아감
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateStarButtons() {
        starButton1.setImageResource(starCount >= 1 ? R.drawable.star : R.drawable.star_off);
        starButton2.setImageResource(starCount >= 2 ? R.drawable.star : R.drawable.star_off);
        starButton3.setImageResource(starCount >= 3 ? R.drawable.star : R.drawable.star_off);
    }
}