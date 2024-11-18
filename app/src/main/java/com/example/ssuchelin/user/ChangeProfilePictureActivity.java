package com.example.ssuchelin.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ssuchelin.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//(프로필정보바꾸기) 프로필사진 클릭시

public class ChangeProfilePictureActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImage;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_picture);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 제목 숨기기 및 기본 뒤로가기 아이콘(<) 활성화
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // 제목 숨기기
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 아이콘 활성화
        }

        profileImage = findViewById(R.id.profile_image);

        findViewById(R.id.upload_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            //uploadImageToFirebase();
        }
    }

    //크아악 파베 / user_id 고정되어있음, 실제 앱에서는 ID에 맞게 프로필 이미지 저장해야함
    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // Firebase Storage에서 프로필 사진 저장 경로 지정
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
                            Toast.makeText(ChangeProfilePictureActivity.this, "프로필 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // 실패 시 오류 메시지 출력
                        Toast.makeText(ChangeProfilePictureActivity.this, "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}


