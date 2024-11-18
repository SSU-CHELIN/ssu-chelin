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

// (프로필정보바꾸기) 프로필사진 클릭시

public class ChangeProfilePictureActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // 이미지 선택 요청 코드
    private ImageView profileImage; // 선택한 프로필 이미지를 보여주는 ImageView
    private Uri imageUri; // 선택된 이미지의 URI를 저장하는 변수

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

        // 프로필 이미지 초기화
        profileImage = findViewById(R.id.profile_image);

        // '사진 선택 및 업로드' 버튼 클릭 시 파일 선택 창을 열기 위한 클릭 리스너 설정
        findViewById(R.id.upload_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(); // 파일 선택 메서드 호출
            }
        });
    }

    /**
     * 이미지 파일을 선택할 수 있는 파일 선택기를 여는 메서드
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*"); // 이미지 파일만 선택 가능
        intent.setAction(Intent.ACTION_GET_CONTENT); // 파일 선택 창을 열기 위한 액션
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // 선택한 파일에 대해 결과를 받음
    }

    /**
     * 파일 선택기에서 이미지 선택 후 호출되는 메서드
     *
     * @param requestCode 요청 코드
     * @param resultCode 결과 코드
     * @param data 선택된 데이터 (이미지)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 이미지 선택이 성공했을 경우
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // 선택된 이미지의 URI 저장
            profileImage.setImageURI(imageUri); // 선택된 이미지를 ImageView에 표시
            //uploadImageToFirebase(); // Firebase 업로드 메서드를 호출 (현재 주석 처리됨)
        }
    }

    /*
    // Firebase에 선택한 이미지를 업로드하는 메서드
    // (주의) 현재 user_id가 고정되어 있음, 실제 앱에서는 사용자의 고유 ID를 가져와야 함
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
                            Toast.makeText(ChangeProfilePictureActivity.this, "프로필 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // 실패 시 오류 메시지 출력
                        Toast.makeText(ChangeProfilePictureActivity.this, "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
    */

    /**
     * Toolbar의 뒤로가기 버튼 클릭 이벤트 처리
     * 뒤로가기 버튼이 클릭되면 현재 Activity를 종료하고 이전 화면으로 돌아감
     *
     * @param item 메뉴 항목
     * @return 클릭 처리 여부
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // 뒤로가기 버튼이 클릭되었을 때
            finish(); // 현재 화면을 종료하여 이전 화면으로 돌아감
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
