package com.example.ssuchelin.review;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ssuchelin.R;

public class SubCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 아이콘 활성화
        }

        toolbar.setNavigationOnClickListener(v -> finish()); // 뒤로가기 버튼 클릭 리스너

        // 전달된 하위 카테고리 이름과 키 가져오기
        String subCategoryKey = getIntent().getStringExtra("subCategoryKey");
        String subCategoryName = getIntent().getStringExtra("subCategoryName");

        // 디버그 로그 추가
        Log.d("ReceivedData", "Key: " + subCategoryKey + ", Name: " + subCategoryName);

        // 하위 카테고리 이름을 텍스트뷰에 표시
        TextView subCategoryTextView = findViewById(R.id.sub_category_name);
        subCategoryTextView.setText("> " + subCategoryName);
    }
}
