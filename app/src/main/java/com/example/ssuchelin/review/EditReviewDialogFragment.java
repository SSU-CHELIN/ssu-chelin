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
    private Review review;
    private FragmentEditReviewDialogBinding binding;
    private DatabaseReference reviewsRef;



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
            loadReviewData();
        }


        reviewsRef = FirebaseDatabase.getInstance()
                .getReference("user")
                .child(studentId)
                .child("myReviewData")
                .child(reviewId)
                .getRef();




        reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");


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

    private void saveReviewChanges() {
        String updatedReview = binding.reviewEditText.getText().toString();

        if (updatedReview.isEmpty()) {
            Toast.makeText(getContext(), "Review cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        reviewsRef.child(reviewId).child("reviewText").setValue(updatedReview).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Review updated successfully", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onReviewUpdated(updatedReview); // 업데이트 콜백 호출
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to update review", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadReviewData() {
        reviewsRef.child(reviewId).addListenerForSingleValueEvent(new ValueEventListener() {
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