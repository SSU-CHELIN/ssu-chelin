package com.example.ssuchelin.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.ssuchelin.R;

public class ChangeProfilePictureFragment extends Fragment {

    private static final String TAG = "ChangeProfilePictureFragment";
    private ImageView profileImage; // 선택한 프로필 이미지를 보여주는 ImageView
    private Uri imageUri; // 선택된 이미지의 URI를 저장하는 변수

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData(); // 선택된 이미지의 URI 저장
                    profileImage.setImageURI(imageUri); // 선택된 이미지를 ImageView에 표시
                    // uploadImageToFirebase(); // Firebase 업로드 메서드를 호출 (필요 시 구현)
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 레이아웃 설정
        View view = inflater.inflate(R.layout.fragment_change_profile_picture, container, false);

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
        // 프로필 이미지 초기화
        profileImage = view.findViewById(R.id.profile_image);

        // '사진 선택 및 업로드' 버튼 클릭 시 파일 선택 창을 열기 위한 클릭 리스너 설정
        view.findViewById(R.id.upload_image_button).setOnClickListener(v -> openFileChooser());

        return view;
    }

    /**
     * 이미지 파일을 선택할 수 있는 파일 선택기를 여는 메서드
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*"); // 이미지 파일만 선택 가능
        intent.setAction(Intent.ACTION_GET_CONTENT); // 파일 선택 창을 열기 위한 액션
        imagePickerLauncher.launch(intent); // 선택한 파일에 대해 결과를 받음
    }

    /*
    // Firebase에 선택한 이미지를 업로드하는 메서드
    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // Firebase Storage에 프로필 사진을 저장할 경로 설정
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_pictures/" + "user_id");

            // 파일 업로드 및 성공/실패 리스너 추가
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // 업로드 성공 시, 다운로드 URL을 가져옴
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Firebase Database에 다운로드 URL 저장
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child("user_id");
                            userRef.child("profileImageUrl").setValue(uri.toString());

                            // 성공 메시지 출력
                            Toast.makeText(getContext(), "프로필 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // 실패 시 오류 메시지 출력
                        Toast.makeText(getContext(), "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
    */
}
