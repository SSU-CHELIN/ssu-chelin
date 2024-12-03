package com.example.ssuchelin.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.ssuchelin.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstSettingFragment extends Fragment {

    private RadioGroup saltRadioGroup, spicyRadioGroup;
    private Button btnSavePreferences;
    private DatabaseReference mDatabaseRef;
    private List<CheckBox> allergyCheckboxes = new ArrayList<>();
    private List<String> selectedAllergies = new ArrayList<>();
    private String studentId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_setting, container, false);

        // SharedPreferences에서 studentId 가져오기
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

        // Firebase 데이터베이스 참조 설정
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);

        // Toolbar 설정
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
            }
        }

        toolbar.setNavigationOnClickListener(v -> {
            if (!requireActivity().getSupportFragmentManager().isStateSaved()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // View 초기화
        saltRadioGroup = view.findViewById(R.id.saltRadioGroup);
        spicyRadioGroup = view.findViewById(R.id.spicyRadioGroup);
        btnSavePreferences = view.findViewById(R.id.btn_complete_registration);

        // 알레르기 체크박스 초기화
        setupAllergyCheckboxes(view);

        // 저장 버튼 클릭 리스너
        btnSavePreferences.setOnClickListener(v -> saveDataToFirebase());

        return view;
    }

    // 알레르기 체크박스 설정
    private void setupAllergyCheckboxes(View view) {
        int[] allergyCheckboxIds = {
                R.id.allergy_shrimp, R.id.allergy_pork, R.id.allergy_soybean, R.id.allergy_milk, R.id.allergy_rice,
                R.id.allergy_wheat, R.id.allergy_chicken, R.id.allergy_peanut, R.id.allergy_egg, R.id.allergy_shellfish,
                R.id.allergy_tomato, R.id.allergy_octopus, R.id.allergy_crab, R.id.allergy_mackerel, R.id.allergy_pufferfish,
                R.id.allergy_yellowfish, R.id.allergy_avocado, R.id.allergy_codroe, R.id.allergy_squid, R.id.allergy_miso,
                R.id.allergy_mussel, R.id.allergy_roe
        };

        for (int id : allergyCheckboxIds) {
            CheckBox checkbox = view.findViewById(id);
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
            Toast.makeText(getContext(), "학생 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 간 선택 값 확인
        int selectedSaltId = saltRadioGroup.getCheckedRadioButtonId();
        String saltPreference = "";
        if (selectedSaltId != -1) {
            RadioButton selectedSaltButton = requireView().findViewById(selectedSaltId);
            saltPreference = selectedSaltButton.getText().toString();
        }

        // 매운맛 선택 값 확인
        int selectedSpicyId = spicyRadioGroup.getCheckedRadioButtonId();
        String spicyPreference = "";
        if (selectedSpicyId != -1) {
            RadioButton selectedSpicyButton = requireView().findViewById(selectedSpicyId);
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
                Toast.makeText(getContext(), "설정이 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "데이터 저장에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
