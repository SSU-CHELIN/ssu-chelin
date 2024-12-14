package com.example.ssuchelin.review;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class EditReviewActivity extends AppCompatActivity {

    private EditText reviewContent; // 리뷰 내용
    private Button saveButton;
    private DatabaseReference reviewRef;

    // 추가된 UI 요소들 (activity_editreview.xml에 있다고 가정)
    private TextView foodCategory, foodMainMenu, foodSubMenu;
    private ImageButton starButton1, starButton2, starButton3;

    private String studentId;
    private String reviewId;
    private String type; // type: "ddook", "dub", "yang"
    private String typeNodeName; // "뚝배기 코너", "덮밥 코너", "양식 코너"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editreview); // 레이아웃에 foodCategory, foodMainMenu 등 추가되어 있어야 함.

        reviewContent = findViewById(R.id.review_edit_text);
        saveButton = findViewById(R.id.submit_button);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("리뷰 수정");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // 추가된 UI 초기화
        foodCategory = findViewById(R.id.foodCategory);
        foodMainMenu = findViewById(R.id.foodMainMenu);
        foodSubMenu = findViewById(R.id.foodSubMenu);

        starButton1 = findViewById(R.id.starButton1);
        starButton2 = findViewById(R.id.starButton2);
        starButton3 = findViewById(R.id.starButton3);

        // 이전 화면에서 전달된 데이터 수신
        Intent intent = getIntent();
        reviewId = intent.getStringExtra("review_id");
        studentId = intent.getStringExtra("student_id");
        String username = intent.getStringExtra("username");
        // type도 Intent로 받아온다고 가정
        type = intent.getStringExtra("type");
        if(type == null) type = "ddook"; // 기본값 예시

        if ("ddook".equals(type)) typeNodeName = "뚝배기 코너";
        else if ("dub".equals(type)) typeNodeName = "덮밥 코너";
        else if ("yang".equals(type)) typeNodeName = "양식 코너";
        else typeNodeName = "알 수 없음";

        if (reviewId == null || studentId == null) {
            Toast.makeText(this, "잘못된 리뷰 ID 또는 사용자 정보입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 리뷰 정보 로딩
        reviewRef = FirebaseDatabase.getInstance()
                .getReference("User")
                .child(studentId)
                .child("myReviewData")
                .child(reviewId);

        // userReview 로딩
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

        // Mainmenu, Submenu, starCount 로딩 후 UI 업데이트
        reviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mainMenu = snapshot.child("Mainmenu").getValue(String.class);
                String subMenu = snapshot.child("Submenu").getValue(String.class);
                Integer starCountVal = snapshot.child("starCount").getValue(Integer.class);
                if(mainMenu == null) mainMenu = "알 수 없음";
                if(subMenu == null) subMenu = "알 수 없음";
                int starCount = (starCountVal == null)?0:starCountVal;

                // UI에 반영
                foodMainMenu.setText(mainMenu);
                foodSubMenu.setText(subMenu);
                updateStarImages((float) starCount);

                // Category 로딩
                loadCategoryData(mainMenu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditReviewActivity.this, "리뷰 데이터 로딩 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    // Category에서 mainMenu를 이용해 Category 표시
    private void loadCategoryData(String mainMenuName) {
        if("알 수 없음".equals(typeNodeName)) {
            foodCategory.setText("알 수 없음");
            return;
        }
        // Category/{typeNodeName}/Mainmenu/{mainMenuName}
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Category")
                .child(typeNodeName)
                .child("Mainmenu")
                .child(mainMenuName);

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 상위 상위 노드 = typeNodeName 사용
                foodCategory.setText(typeNodeName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                foodCategory.setText("알 수 없음");
            }
        });
    }

    private void updateStarImages(float avg) {
        float portion1 = avg - 0;
        float portion2 = avg - 1;
        float portion3 = avg - 2;

        starButton1.setImageResource(getStarResourceForPortion(portion1));
        starButton2.setImageResource(getStarResourceForPortion(portion2));
        starButton3.setImageResource(getStarResourceForPortion(portion3));
    }

    private int getStarResourceForPortion(float portion) {
        if (portion <= 0) {
            return R.drawable.star_0;
        } else if (portion >= 1) {
            return R.drawable.star_100;
        } else {
            float percent = portion * 100;
            float[] thresholds = {0,12.5f,25f,37.5f,50f,62.5f,75f,87.5f,100f};
            int[] drawables = {
                    R.drawable.star_0,
                    R.drawable.star_12_5,
                    R.drawable.star_25,
                    R.drawable.star_37_5,
                    R.drawable.star_50,
                    R.drawable.star_62_5,
                    R.drawable.star_75,
                    R.drawable.star_87_5,
                    R.drawable.star_100
            };

            float minDiff = Float.MAX_VALUE;
            int chosenIndex = 0;
            for (int i = 0; i < thresholds.length; i++) {
                float diff = Math.abs(percent - thresholds[i]);
                if (diff < minDiff) {
                    minDiff = diff;
                    chosenIndex = i;
                }
            }
            return drawables[chosenIndex];
        }
    }

}
