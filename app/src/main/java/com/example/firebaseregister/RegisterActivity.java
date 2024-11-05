package com.example.firebaseregister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtEmail, mEtpwd, mEtpersonName;
    private Button mBtnRegister;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("myapp");

        mEtEmail = findViewById(R.id.et_email2);
        mEtpwd = findViewById(R.id.et_password);
        mBtnRegister = findViewById(R.id.btn_next);
        mEtpersonName = findViewById(R.id.et_personname);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 처리시작
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtpwd.getText().toString();
                String strPersonName = mEtpersonName.getText().toString();

                if (strEmail.isEmpty() || strPwd.isEmpty() || strPersonName.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "모든 필드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailId(firebaseUser.getEmail());
                            account.setPassword(strPwd);
                            account.setPersonName(strPersonName);

                            // 데이터베이스에 사용자 정보 저장
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                                                // 데이터 저장 완료 후 PersonalActivity로 이동
                                                Intent intent = new Intent(RegisterActivity.this, PersonalActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "데이터베이스 저장 실패", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}