package com.example.ssuchelin.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ssuchelin.MainActivity;
import com.example.ssuchelin.user.PersonalActivity;
import com.example.ssuchelin.R;
import com.example.ssuchelin.user.UserAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
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
                    Toast.makeText(RegisterActivity.this, "모든 필드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Firebase에서 이미 등록된 학번인지 확인
                mDatabaseRef.child(studentId).child("UserAccount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            saveStudentIdToPreferences(studentId);
                            // 학번이 이미 존재하는 경우 MainActivity로 이동
                            Toast.makeText(RegisterActivity.this, "이미 등록된 사용자입니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.putExtra("open_fragment", "MenuFragment"); // 어떤 프래그먼트를 열지 전달
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

                                    // 새로운 사용자의 학번을 SharedPreferences에 저장
                                    saveStudentIdToPreferences(studentId);

                                    mDatabaseRef.child(studentId).child("UserAccount").setValue(account)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterActivity.this, "U-SAINT 로그인 성공", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(RegisterActivity.this, PersonalActivity.class);
                                                    intent.putExtra("studentId", studentId);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "데이터베이스 저장 실패", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                                @Override
                                public void onAuthFailure(String message) {
                                    Toast.makeText(RegisterActivity.this, "U-SAINT 인증 실패: " + message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RegisterActivity.this, "데이터베이스 오류: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void saveStudentIdToPreferences(String studentId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("realStudentId", studentId);
        editor.apply();
    }
}
