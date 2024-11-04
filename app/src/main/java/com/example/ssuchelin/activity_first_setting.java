package com.example.ssuchelin;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

//초기설정화면

public class activity_first_setting extends AppCompatActivity {

    private Button selectedSaltButton;
    private Button selectedSpicyButton;
    private List<Button> allergyButtons = new ArrayList<>();
    private List<String> selectedAllergies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_setting);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 간 설정 버튼 설정
        setupSaltButtons();

        // 매운맛 설정 버튼 설정
        setupSpicyButtons();

        // 알레르기 버튼 설정
        setupAllergyButtons();
    }

    // 간 설정 버튼 그룹 설정
    private void setupSaltButtons() {
        int[] saltButtonIds = {R.id.salt_level_1, R.id.salt_level_2, R.id.salt_level_3, R.id.salt_level_4, R.id.salt_level_5};
        for (int id : saltButtonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(view -> {
                if (selectedSaltButton != null) {
                    selectedSaltButton.setBackgroundTintList(getResources().getColorStateList(R.color.default_button_color));
                }
                button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_button_color));
                selectedSaltButton = button;
            });
        }
    }

    // 매운맛 설정 버튼 그룹 설정
    private void setupSpicyButtons() {
        int[] spicyButtonIds = {R.id.spicy_level_1, R.id.spicy_level_2, R.id.spicy_level_3, R.id.spicy_level_4, R.id.spicy_level_5};
        for (int id : spicyButtonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(view -> {
                if (selectedSpicyButton != null) {
                    selectedSpicyButton.setBackgroundTintList(getResources().getColorStateList(R.color.default_button_color));
                }
                button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_button_color));
                selectedSpicyButton = button;
            });
        }
    }

    // 알레르기 버튼 그룹 설정
    private void setupAllergyButtons() {
        int[] allergyButtonIds = {
                R.id.allergy_shrimp, R.id.allergy_pork, R.id.allergy_soy, R.id.allergy_milk, R.id.allergy_rice,
                R.id.allergy_wheat, R.id.allergy_chicken, R.id.allergy_peanut, R.id.allergy_egg, R.id.allergy_shellfish,
                R.id.allergy_tomato, R.id.allergy_octopus, R.id.allergy_bonito, R.id.allergy_sulfite, R.id.allergy_fish,
                R.id.allergy_pineapple, R.id.allergy_squid, R.id.allergy_cod, R.id.allergy_quail, R.id.allergy_pollock,
                R.id.allergy_mackerel, R.id.allergy_dried_pollock, R.id.allergy_whelk, R.id.allergy_mussel, R.id.allergy_clam
        };

        for (int id : allergyButtonIds) {
            Button button = findViewById(id);
            allergyButtons.add(button);
            button.setOnClickListener(view -> {
                if (selectedAllergies.contains(button.getText().toString())) {
                    selectedAllergies.remove(button.getText().toString());
                    button.setBackgroundTintList(getResources().getColorStateList(R.color.default_button_color));
                } else {
                    selectedAllergies.add(button.getText().toString());
                    button.setBackgroundTintList(getResources().getColorStateList(R.color.selected_button_color));
                }
            });
        }
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

    // Firebase에 데이터 저장 함수 예시
    private void saveDataToFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child("user_id");
        databaseRef.child("saltLevel").setValue(selectedSaltButton != null ? selectedSaltButton.getText().toString() : null);
        databaseRef.child("spicyLevel").setValue(selectedSpicyButton != null ? selectedSpicyButton.getText().toString() : null);
        databaseRef.child("allergies").setValue(selectedAllergies);
        Toast.makeText(this, "설정이 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }
}
