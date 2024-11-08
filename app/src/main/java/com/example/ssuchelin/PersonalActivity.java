package com.example.ssuchelin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalActivity extends AppCompatActivity {

    private RadioGroup saltRadioGroup, spicyRadioGroup;
    private Button btnCompleteRegistration;
    private DatabaseReference mDatabaseRef;
    private String studentId;
    private EditText etUserName, etNickName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        studentId = getIntent().getStringExtra("studentId");

        // Firebase 데이터베이스 참조 설정
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);

        saltRadioGroup = findViewById(R.id.saltRadioGroup);
        spicyRadioGroup = findViewById(R.id.spicyRadioGroup);
        btnCompleteRegistration = findViewById(R.id.btn_complete_registration);

        etUserName = findViewById(R.id.et_user_name);
        etNickName = findViewById(R.id.et_nickname);

        // "회원가입 완료" 버튼 클릭 리스너
        btnCompleteRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreferencesToFirebase();
            }
        });
    }

    // Firebase에 사용자 선택 정보 저장
    private void savePreferencesToFirebase() {
        String userName = etUserName.getText().toString();
        String nickname = etNickName.getText().toString();

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

        // 알레르기 체크박스 값 확인
        List<String> allergies = new ArrayList<>();
        CheckBox[] allergyCheckboxes = {
                findViewById(R.id.allergy_shrimp),
                findViewById(R.id.allergy_pork),
                findViewById(R.id.allergy_soybean),
                findViewById(R.id.allergy_milk),
                findViewById(R.id.allergy_rice),
                findViewById(R.id.allergy_wheat),
                findViewById(R.id.allergy_chicken),
                findViewById(R.id.allergy_peanut),
                findViewById(R.id.allergy_egg),
                findViewById(R.id.allergy_shellfish),
                findViewById(R.id.allergy_tomato),
                findViewById(R.id.allergy_octopus),
                findViewById(R.id.allergy_crab),
                findViewById(R.id.allergy_mackerel),
                findViewById(R.id.allergy_pufferfish),
                findViewById(R.id.allergy_yellowfish),
                findViewById(R.id.allergy_avocado),
                findViewById(R.id.allergy_codroe),
                findViewById(R.id.allergy_squid),
                findViewById(R.id.allergy_miso),
                findViewById(R.id.allergy_mussel),
                findViewById(R.id.allergy_roe)
        };

        for (CheckBox allergyCheckbox : allergyCheckboxes) {
            if (allergyCheckbox.isChecked()) {
                allergies.add(allergyCheckbox.getText().toString());
            }
        }

        // 사용자 선택 데이터를 Map으로 저장
        Map<String, Object> userPreferences = new HashMap<>();
        userPreferences.put("userName", userName);
        userPreferences.put("nickname", nickname);
        userPreferences.put("saltPreference", saltPreference);
        userPreferences.put("spicyPreference", spicyPreference);
        userPreferences.put("allergies", allergies);

        // Firebase에 데이터 저장
        mDatabaseRef.child("userinfo").setValue(userPreferences).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // 데이터 저장 성공 시 LoginActivity로 이동
                Toast.makeText(PersonalActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PersonalActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // PersonalActivity 종료
            } else {
                // 데이터 저장 실패 시 메시지 출력
                Toast.makeText(PersonalActivity.this, "데이터 저장에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
