package com.example.ssuchelin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsaintRegisterActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseRef;
    private EditText mEtStudentId, mEtPassword;
    private ImageButton mBtnRegister;
    private UsaintAuthService usaintAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usaint_register);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User");
        mEtStudentId = findViewById(R.id.et_student_id);
        mEtPassword = findViewById(R.id.et_password);
        mBtnRegister = findViewById(R.id.btn_register);
        usaintAuthService = new UsaintAuthService();

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String studentId = mEtStudentId.getText().toString();
                String password = mEtPassword.getText().toString();

                if (studentId.isEmpty() || password.isEmpty()) {
                    Toast.makeText(UsaintRegisterActivity.this, "모든 필드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Firebase에서 이미 등록된 학번인지 확인
                mDatabaseRef.child(studentId).child("UserAccount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // 학번이 이미 존재하는 경우 MainActivity로 이동
                            Toast.makeText(UsaintRegisterActivity.this, "이미 등록된 사용자입니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UsaintRegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // 새로운 사용자일 경우 인증 및 회원가입 진행
                            usaintAuthService.authenticate(studentId, password, new UsaintAuthService.AuthCallback() {
                                @Override
                                public void onAuthSuccess(String token) {
                                    UserAccount account = new UserAccount();
                                    account.setIdToken(token);
                                    account.setRealStudentId(studentId);

                                    mDatabaseRef.child(studentId).child("UserAccount").setValue(account)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(UsaintRegisterActivity.this, "U-SAINT 로그인 성공", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(UsaintRegisterActivity.this, PersonalActivity.class);
                                                    intent.putExtra("studentId", studentId);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(UsaintRegisterActivity.this, "데이터베이스 저장 실패", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                                @Override
                                public void onAuthFailure(String message) {
                                    Toast.makeText(UsaintRegisterActivity.this, "U-SAINT 인증 실패: " + message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(UsaintRegisterActivity.this, "데이터베이스 오류: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
