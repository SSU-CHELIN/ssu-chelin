package com.example.firebaseregister;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsaintRegisterActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseRef;
    private EditText mEtStudentId, mEtPassword;
    private ImageButton mBtnRegister;
    private UsaintAuthService usaintAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usaint_register);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("myapp");
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

                usaintAuthService.authenticate(studentId, password, new UsaintAuthService.AuthCallback() {
                    @Override
                    public void onAuthSuccess(String token) {
                        UserAccount account = new UserAccount();
                        account.setIdToken(token);
                        account.setRealStudentId(studentId);

                        mDatabaseRef.child("UserAccount").child(studentId).setValue(account)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UsaintRegisterActivity.this, "U-SAINT 로그인 성공", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(UsaintRegisterActivity.this, PersonalActivity.class);
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
        });
    }
}
