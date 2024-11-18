package com.example.ssuchelin;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

// 초기 설정 화면

public class FirstSettingActivity extends AppCompatActivity {

    private Button selectedSaltButton; // 선택된 간(소금) 레벨 버튼을 저장하는 변수
    private Button selectedSpicyButton; // 선택된 매운맛 레벨 버튼을 저장하는 변수
    private List<Button> allergyButtons = new ArrayList<>(); // 알레르기 버튼 목록을 저장하는 리스트
    private List<String> selectedAllergies = new ArrayList<>(); // 선택된 알레르기 항목을 저장하는 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_setting); // 초기 설정 화면 레이아웃 설정

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // 제목 숨기기
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 기본 뒤로가기 아이콘 활성화
        }

        // 간(소금) 설정 버튼 그룹 초기화
        setupSaltButtons();

        // 매운맛 설정 버튼 그룹 초기화
        setupSpicyButtons();

        // 알레르기 버튼 그룹 초기화
        setupAllergyButtons();
    }

    // 간(소금) 설정 버튼 그룹 설정 함수
    private void setupSaltButtons() {
        int[] saltButtonIds = {R.id.salt_level_1, R.id.salt_level_2, R.id.salt_level_3, R.id.salt_level_4, R.id.salt_level_5};
        for (int id : saltButtonIds) {
            Button button = findViewById(id); // 각 버튼을 ID로 참조
            button.setOnClickListener(view -> {
                if (selectedSaltButton != null) {
                    selectedSaltButton.setBackgroundTintList(getResources().getColorStateList(R.color.default_button_color)); // 이전 선택된 버튼의 색상 초기화
                }
                button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_button_color)); // 현재 선택된 버튼의 색상 변경
                selectedSaltButton = button; // 선택된 버튼을 변수에 저장
            });
        }
    }

    // 매운맛 설정 버튼 그룹 설정 함수
    private void setupSpicyButtons() {
        int[] spicyButtonIds = {R.id.spicy_level_1, R.id.spicy_level_2, R.id.spicy_level_3, R.id.spicy_level_4, R.id.spicy_level_5};
        for (int id : spicyButtonIds) {
            Button button = findViewById(id); // 각 버튼을 ID로 참조
            button.setOnClickListener(view -> {
                if (selectedSpicyButton != null) {
                    selectedSpicyButton.setBackgroundTintList(getResources().getColorStateList(R.color.default_button_color)); // 이전 선택된 버튼의 색상 초기화
                }
                button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_button_color)); // 현재 선택된 버튼의 색상 변경
                selectedSpicyButton = button; // 선택된 버튼을 변수에 저장
            });
        }
    }

    // 알레르기 버튼 그룹 설정 함수
    private void setupAllergyButtons() {
        int[] allergyButtonIds = {
                R.id.allergy_shrimp, R.id.allergy_pork, R.id.allergy_soy, R.id.allergy_milk, R.id.allergy_rice,
                R.id.allergy_wheat, R.id.allergy_chicken, R.id.allergy_peanut, R.id.allergy_egg, R.id.allergy_shellfish,
                R.id.allergy_tomato, R.id.allergy_octopus, R.id.allergy_bonito, R.id.allergy_sulfite, R.id.allergy_fish,
                R.id.allergy_pineapple, R.id.allergy_squid, R.id.allergy_cod, R.id.allergy_quail, R.id.allergy_pollock,
                R.id.allergy_mackerel, R.id.allergy_dried_pollock, R.id.allergy_whelk, R.id.allergy_mussel, R.id.allergy_clam
        };

        for (int id : allergyButtonIds) {
            Button button = findViewById(id); // 각 알레르기 버튼을 ID로 참조
            allergyButtons.add(button); // 알레르기 버튼 목록에 추가
            button.setOnClickListener(view -> {
                if (selectedAllergies.contains(button.getText().toString())) {
                    selectedAllergies.remove(button.getText().toString()); // 선택된 알레르기 항목이 이미 있으면 제거
                    button.setBackgroundTintList(getResources().getColorStateList(R.color.default_button_color)); // 버튼 색상 초기화
                } else {
                    selectedAllergies.add(button.getText().toString()); // 선택된 알레르기 항목 추가
                    button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_button_color)); // 버튼 색상 변경
                }
            });
        }
    }

    // 뒤로가기 버튼 클릭 이벤트 처리 함수
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 이전 화면으로 돌아감
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Firebase에 데이터 저장 함수 예시 (파베 / user_id 고정되어 있음)
    private void saveDataToFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child("user_id"); // Firebase Database 참조 생성
        databaseRef.child("saltLevel").setValue(selectedSaltButton != null ? selectedSaltButton.getText().toString() : null); // 선택된 간 레벨을 Firebase에 저장
        databaseRef.child("spicyLevel").setValue(selectedSpicyButton != null ? selectedSpicyButton.getText().toString() : null); // 선택된 매운맛 레벨을 Firebase에 저장
        databaseRef.child("allergies").setValue(selectedAllergies); // 선택된 알레르기 목록을 Firebase에 저장
        Toast.makeText(this, "설정이 저장되었습니다.", Toast.LENGTH_SHORT).show(); // 성공 메시지 표시
    }
}
