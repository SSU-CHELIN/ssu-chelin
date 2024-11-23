package com.example.ssuchelin.review;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SubCategoryActivity extends AppCompatActivity {

    private LinearLayout subCategoryContainer; // 하위 노드를 출력할 컨테이너
    private DatabaseReference categoryRef; // Firebase 참조

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

        // 전달된 상위/중간 노드 이름과 키 가져오기
        String parentNode = getIntent().getStringExtra("parentNode");
        String subCategoryKey = getIntent().getStringExtra("subCategoryKey");
        String subCategoryName = getIntent().getStringExtra("subCategoryName");

        // 타이틀 구성
        TextView subCategoryTextView = findViewById(R.id.sub_category_name);
        subCategoryTextView.setText(parentNode + "코너 > " + subCategoryName);

        // 하위 노드 출력 컨테이너
        subCategoryContainer = findViewById(R.id.sub_category_container);

        // Firebase 참조
        categoryRef = FirebaseDatabase.getInstance().getReference("Category").child(parentNode).child(subCategoryKey);

        // 하위 노드 로드
        loadSubCategories();
    }

    private void loadSubCategories() {
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                subCategoryContainer.removeAllViews(); // 중복된 뷰 제거

                if (!snapshot.exists()) {
                    TextView noDataTextView = new TextView(SubCategoryActivity.this);
                    noDataTextView.setText("하위 메뉴가 없습니다.");
                    noDataTextView.setTextSize(16);
                    noDataTextView.setPadding(16, 16, 16, 16);
                    subCategoryContainer.addView(noDataTextView);
                    return;
                }

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String childNodeName = childSnapshot.getValue(String.class);
                    if (childNodeName != null) {
                        TextView childTextView = new TextView(SubCategoryActivity.this);
                        childTextView.setText("   > " + childNodeName);
                        childTextView.setTextSize(16);
                        childTextView.setPadding(32, 16, 16, 16);
                        subCategoryContainer.addView(childTextView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                TextView errorTextView = new TextView(SubCategoryActivity.this);
                errorTextView.setText("하위 메뉴를 불러오지 못했습니다.");
                errorTextView.setTextSize(16);
                errorTextView.setPadding(16, 16, 16, 16);
                subCategoryContainer.addView(errorTextView);
            }
        });
    }

}
