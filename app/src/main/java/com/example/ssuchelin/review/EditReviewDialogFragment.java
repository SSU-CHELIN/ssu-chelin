package com.example.ssuchelin.review;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ssuchelin.R;
import com.example.ssuchelin.databinding.FragmentEditReviewDialogBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditReviewDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditReviewDialogFragment extends DialogFragment {


    private String reviewId;
    private String studentId;
    private FragmentEditReviewDialogBinding binding;
    private DatabaseReference reviewsRef;
    private int starCount = 0;




    // TODO: Rename and change types of parameters

    public interface OnReviewUpdatedListener {
        void onReviewUpdated(String updatedReviewText);
    }

    private OnReviewUpdatedListener listener;


    public static EditReviewDialogFragment newInstance(String reviewId, String studentId) {
        EditReviewDialogFragment fragment = new EditReviewDialogFragment();
        Bundle args = new Bundle();
        args.putString("review_id", reviewId);
        args.putString("student_id", studentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnReviewUpdatedListener) {
            listener = (OnReviewUpdatedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnReviewUpdatedListener");
        }
    }
    public EditReviewDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEditReviewDialogBinding.inflate(inflater, container, false);


        if (getArguments() != null) {
            reviewId = getArguments().getString("review_id");
            studentId = getArguments().getString("student_id");
        }


        // 리뷰 정보 로딩
        reviewsRef = FirebaseDatabase.getInstance()
                .getReference("User")
                .child(studentId)
                .child("myReviewData")
                .child(reviewId);

        // userReview 로딩
        reviewsRef.child("userReview").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String existingContent = task.getResult().getValue(String.class);
                if (existingContent != null) {
                    binding.reviewEditText.setText(existingContent);
                }
            } else {
                Toast.makeText(requireContext(), "리뷰 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        reviewsRef.child("starCount").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Integer starCount = task.getResult().getValue(Integer.class);
                if (starCount != null) {
                    // starButton 설정
                    updateStarButtons();
                }
            } else {
                Toast.makeText(requireContext(), "별점 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        binding.starButton1.setOnClickListener(starClickListener);
        binding.starButton2.setOnClickListener(starClickListener);
        binding.starButton3.setOnClickListener(starClickListener);


        // 리뷰 수정
        binding.reviewEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int textLength = s.length();
                binding.characterCounter.setText(textLength + " / 300");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });




        binding.submitButton.setOnClickListener(v -> saveReviewChanges());





        return binding.getRoot();
    }

    private final View.OnClickListener starClickListener = view -> {
        // 첫 번째 별 클릭 시
        // starCount가 1이면 다시 0으로 만들고, 아니면 1로 설정
        if (view.getId() == R.id.starButton1) {
            if (starCount == 1) {
                starCount = 0; // 이미 1점 상태에서 다시 첫 번째 별 클릭하면 0점
            } else {
                starCount = 1; // 그렇지 않다면 1점
            }
        } else if (view.getId() == R.id.starButton2) {
            // 두 번째 별 클릭 시 항상 2점
            starCount = 2;
        } else if (view.getId() == R.id.starButton3) {
            // 세 번째 별 클릭 시 항상 3점
            starCount = 3;
        }

        // 변경된 starCount에 따라 별 UI 갱신
        updateStarButtons();
    };

    private void updateStarButtons() {
        binding.starButton1.setImageResource(starCount >= 1 ? R.drawable.star_100 : R.drawable.star_0);
        binding.starButton2.setImageResource(starCount >= 2 ? R.drawable.star_100 : R.drawable.star_0);
        binding.starButton3.setImageResource(starCount >= 3 ? R.drawable.star_100 : R.drawable.star_0);
    }

    private void saveReviewChanges() {
        String updatedReview = binding.reviewEditText.getText().toString();
        Integer updatedStarCount = starCount; // starCount는 버튼 이벤트로 업데이트된 최신 값

        if (updatedReview.isEmpty()) {
            Toast.makeText(getContext(), "Review cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // userReview와 starCount를 동시에 업데이트
        reviewsRef.child("userReview").setValue(updatedReview).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // userReview 저장 성공
                reviewsRef.child("starCount").setValue(updatedStarCount).addOnCompleteListener(starTask -> {
                    if (starTask.isSuccessful()) {
                        Toast.makeText(getContext(), "Review and star count updated successfully", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            // 콜백으로 수정된 데이터 전달
                            listener.onReviewUpdated(updatedReview);
                            listener.onReviewUpdated(updatedStarCount.toString());
                        }
                        dismiss(); // 다이얼로그 종료
                    } else {
                        Toast.makeText(getContext(), "Failed to update star count", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Failed to update review", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadReviewData() {
        reviewsRef.child("userReview").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String reviewText = snapshot.child("userReview").getValue(String.class);
                    binding.reviewEditText.setText(reviewText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load review", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

        // 다이얼로그 크기 설정
        if (getDialog() != null && getDialog().getWindow() != null) {
            // 원하는 크기 비율로 설정 (예: 가로 90%, 세로 WRAP_CONTENT)
            getDialog().getWindow().setLayout(
                    (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.9), // 가로 크기: 화면의 90%
                    ViewGroup.LayoutParams.WRAP_CONTENT // 세로 크기: 내용에 맞춤
            );
        }
    }


}