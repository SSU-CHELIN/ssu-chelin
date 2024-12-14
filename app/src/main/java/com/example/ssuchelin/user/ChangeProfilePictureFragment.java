package com.example.ssuchelin.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.bumptech.glide.Glide;
import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * 프로필 사진 변경 프래그먼트
 *
 * 요구사항:
 * - 사진 선택 및 업로드 버튼 클릭 -> 이미지 선택 후 Firebase Storage 업로드
 * - 업로드 완료시 User/{studentId}/userinfo/picture 에 다운로드 URL 저장
 * - Firebase에서 picture 필드 로드하여 ImageView에 표시
 */
public class ChangeProfilePictureFragment extends Fragment {

    private static final String TAG = "ChangeProfilePicture";
    private ImageView profileImage;
    private Uri imageUri;
    private DatabaseReference userInfoRef;
    private StorageReference storageRef;

    // 실제 로직에서는 studentId를 로그인 시점에서 SharedPreferences나 다른 방식으로 가져와야 함.
    // 여기서는 예시로 "20212902" 등의 실제 학번을 넣어 사용할 수 있음.
    private String studentId = "20212902"; // 예: 실제 학번을 여기나 onCreate에서 동적으로 가져옴.

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData(); // 선택된 이미지의 URI
                    profileImage.setImageURI(imageUri); // ImageView에 선택 이미지 표시
                    uploadImageToFirebase(); // Firebase Storage 업로드 메서드 호출
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

        profileImage = view.findViewById(R.id.profile_image);
        view.findViewById(R.id.upload_image_button).setOnClickListener(v -> openFileChooser());

        // Firebase 참조 초기화
        userInfoRef = FirebaseDatabase.getInstance().getReference("User").child(studentId).child("userinfo");
        storageRef = FirebaseStorage.getInstance().getReference("profile_pictures/" + studentId);

        // Firebase에서 기존 프로필 이미지 로드
        loadProfileImageFromFirebase();

        return view;
    }

    /**
     * 이미지 파일 선택기 열기
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Firebase에서 프로필 이미지 로드
     * userinfo/picture 필드에서 URL 가져와 표시
     */
    private void loadProfileImageFromFirebase() {
        userInfoRef.child("picture").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileImageUrl = snapshot.getValue(String.class);
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(profileImageUrl)
                            .placeholder(R.drawable.none_pro)
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.none_pro); // 기본 이미지
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load profile image: " + error.getMessage());
                profileImage.setImageResource(R.drawable.none_pro); // 기본 이미지
            }
        });
    }

    /**
     * Firebase Storage에 선택한 이미지 업로드 후 이미지 URL DB에 저장
     */
    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference imageRef = storageRef.child("profile_image.jpg");
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        saveImageUrlToFirebase(downloadUrl);
                        Toast.makeText(getContext(), "프로필 사진이 업로드되었습니다.", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to upload image: " + e.getMessage());
                        Toast.makeText(getContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "이미지를 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Firebase DB (User/{studentId}/userinfo/picture)에 이미지 URL 저장
     */
    private void saveImageUrlToFirebase(String imageUrl) {
        userInfoRef.child("picture").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Profile image URL saved successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save profile image URL: " + e.getMessage()));
    }
}
