package com.example.ssuchelin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.OnSuccessListener;

//(프로필정보바꾸기) 프로필사진 클릭시

public class activity_change_profile_picture extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImage;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_picture);

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
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_pictures/" + "user_id");
            storageRef.putFile(imageUri).addOnSuccessListener(new UploadTask.TaskSnapshot taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child("user_id");
                    userRef.child("profileImageUrl").setValue(uri.toString());
                    Toast.makeText(activity_change_profile_picture.this, "프로필 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> Toast.makeText(activity_change_profile_picture.this, "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
