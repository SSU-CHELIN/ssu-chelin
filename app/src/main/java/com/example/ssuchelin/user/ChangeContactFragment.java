package com.example.ssuchelin.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.ssuchelin.R;

public class ChangeContactFragment extends Fragment {

    private EditText newContact;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_contact, container, false);

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

        // 새로운 연락처 입력 필드 초기화
        newContact = view.findViewById(R.id.new_contact);

        // '저장' 버튼 클릭 시 연락처 저장 메서드 호출
        view.findViewById(R.id.save_contact_button).setOnClickListener(v -> saveContact());

        return view;
    }

    /**
     * 사용자가 입력한 연락처를 저장하는 메서드
     * 연락처가 비어있지 않을 경우 Firebase에 저장하고, 성공 메시지를 보여줌
     * 비어있을 경우 "연락처를 입력하세요." 메시지를 보여줌
     */
    private void saveContact() {
        String contact = newContact.getText().toString().trim(); // 입력된 연락처를 가져와 앞뒤 공백 제거
        if (!contact.isEmpty()) {
            // Firebase 데이터베이스에 저장하는 코드 (주석처리)
            // DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child("user_id");
            // userRef.child("contact").setValue(contact);
            Toast.makeText(requireContext(), "연락처가 변경되었습니다.", Toast.LENGTH_SHORT).show(); // 성공 메시지 표시
        } else {
            Toast.makeText(requireContext(), "연락처를 입력하세요.", Toast.LENGTH_SHORT).show(); // 연락처 입력 요청 메시지 표시
        }
    }
}
