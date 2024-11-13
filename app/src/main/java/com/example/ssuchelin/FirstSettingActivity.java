package com.example.ssuchelin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//초기설정화면

public class FirstSettingActivity extends BaseActivity {

    private RadioGroup saltRadioGroup, spicyRadioGroup;
    private Button btnSavePreferences;
    private DatabaseReference mDatabaseRef;
    private List<CheckBox> allergyCheckboxes = new ArrayList<>();
    private List<String> selectedAllergies = new ArrayList<>();
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_first_setting);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        studentId = sharedPreferences.getString("realStudentId", "Unknown ID");


        // Firebase 데이터베이스 참조 설정
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // View 초기화
        saltRadioGroup = findViewById(R.id.saltRadioGroup);
        spicyRadioGroup = findViewById(R.id.spicyRadioGroup);
        btnSavePreferences = findViewById(R.id.btn_complete_registration);

        // 알레르기 체크박스 초기화
        setupAllergyCheckboxes();

        // 저장 버튼 클릭 리스너
        btnSavePreferences.setOnClickListener(view -> saveDataToFirebase());

    }

    // 알레르기 체크박스 설정
    private void setupAllergyCheckboxes() {
        int[] allergyCheckboxIds = {
                R.id.allergy_shrimp, R.id.allergy_pork, R.id.allergy_soybean, R.id.allergy_milk, R.id.allergy_rice,
                R.id.allergy_wheat, R.id.allergy_chicken, R.id.allergy_peanut, R.id.allergy_egg, R.id.allergy_shellfish,
                R.id.allergy_tomato, R.id.allergy_octopus, R.id.allergy_crab, R.id.allergy_mackerel, R.id.allergy_pufferfish,
                R.id.allergy_yellowfish, R.id.allergy_avocado, R.id.allergy_codroe, R.id.allergy_squid, R.id.allergy_miso,
                R.id.allergy_mussel, R.id.allergy_roe
        };

        for (int id : allergyCheckboxIds) {
            CheckBox checkbox = findViewById(id);
            allergyCheckboxes.add(checkbox);
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String allergy = buttonView.getText().toString();
                if (isChecked) {
                    selectedAllergies.add(allergy);
                } else {
                    selectedAllergies.remove(allergy);
                }
            });
        }
    }

    // Firebase에 데이터 저장
    private void saveDataToFirebase() {
        if (studentId == null) {
            Toast.makeText(this, "학생 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 간 선택 값 확인
        int selectedSaltId = saltRadioGroup.getCheckedRadioButtonId();
        String saltPreference = "";
        if (selectedSaltId != -1) {
            RadioButton selectedSaltButton = findViewById(selectedSaltId);
            saltPreference = selectedSaltButton.getText().toString();
        }

        // 매운맛 선택 값 확인
        int selectedSpicyId = spicyRadioGroup.getCheckedRadioButtonId();
        String spicyPreference = "";
        if (selectedSpicyId != -1) {
            RadioButton selectedSpicyButton = findViewById(selectedSpicyId);
            spicyPreference = selectedSpicyButton.getText().toString();
        }

        // Firebase에 저장할 데이터 생성
        Map<String, Object> userPreferences = new HashMap<>();
        userPreferences.put("saltPreference", saltPreference);
        userPreferences.put("spicyPreference", spicyPreference);
        userPreferences.put("allergies", selectedAllergies);

        // 데이터 저장
        mDatabaseRef.child("userinfo").updateChildren(userPreferences).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(FirstSettingActivity.this, "설정이 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(FirstSettingActivity.this, "데이터 저장에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 뒤로가기 버튼 클릭 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 이전 화면으로 돌아감
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}