package com.example.ssuchelin.review;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FeedbackFragment extends Fragment {

    private EditText feedbackInput;
    private Button submitButton;
    private DatabaseReference mDatabaseRef;
    private String studentId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        // Retrieve the student ID from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

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
        // 의견 입력 필드와 제출 버튼 초기화
        feedbackInput = view.findViewById(R.id.feedback_input);
        submitButton = view.findViewById(R.id.submit_button);

        // 제출 버튼 클릭 리스너 설정
        submitButton.setOnClickListener(v -> submitFeedback());

        return view;
    }

    private void submitFeedback() {
        if (studentId == null || studentId.equals("Unknown ID")) {
            Toast.makeText(getActivity(), "학생 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String feedbackText = feedbackInput.getText().toString();

        if (feedbackText.isEmpty()) {
            Toast.makeText(getActivity(), "피드백 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);

        // 카운터 값을 가져와서 feedback1, feedback2 순으로 저장
        mDatabaseRef.child("Feedback").child("cnt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final long currentCount = snapshot.exists() && snapshot.getValue(Long.class) != null
                        ? snapshot.getValue(Long.class)
                        : 0;

                // 새로운 feedback 키 생성 (feedback1, feedback2 ...)
                String feedbackKey = "feedback" + (currentCount + 1);

                // 데이터를 저장할 해시맵 생성
                Map<String, Object> feedbackData = new HashMap<>();
                feedbackData.put(feedbackKey, feedbackText);

                // 데이터를 Firebase에 저장
                mDatabaseRef.child("Feedback").updateChildren(feedbackData)
                        .addOnSuccessListener(aVoid -> {
                            // 카운터 값 업데이트
                            mDatabaseRef.child("Feedback").child("cnt").setValue(currentCount + 1)
                                    .addOnSuccessListener(aVoid1 -> Toast.makeText(getActivity(), "피드백이 저장되었습니다.", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "카운터 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .addOnFailureListener(e -> Toast.makeText(getActivity(), "피드백 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                requireActivity().onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "피드백 저장 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
