

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

                mDatabaseRef.child(studentId).child("UserAccount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // 기존 사용자: U-SAINT 재인증 진행
                            usaintAuthService.authenticate(studentId, password, new UsaintAuthService.AuthCallback() {
                                @Override
                                public void onAuthSuccess(String token) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(RegisterActivity.this, "U-SAINT 재인증 성공", Toast.LENGTH_SHORT).show();
                                        saveStudentIdToPreferences(studentId);
                                        saveLoginState();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.putExtra("open_fragment", "MenuFragment");
                                        startActivity(intent);
                                        finish();
                                    });
                                }

                                @Override
                                public void onAuthFailure(String message) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(RegisterActivity.this, "U-SAINT 인증 실패: " + message, Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        } else {
                            // 신규 사용자: U-SAINT 인증 후 DB 저장
                            usaintAuthService.authenticate(studentId, password, new UsaintAuthService.AuthCallback() {
                                @Override
                                public void onAuthSuccess(String token) {
                                    runOnUiThread(() -> {
                                        UserAccount account = new UserAccount();
                                        account.setIdToken(token);
                                        account.setRealStudentId(studentId);

                                        saveStudentIdToPreferences(studentId);

                                        mDatabaseRef.child(studentId).child("UserAccount").setValue(account)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this, "U-SAINT 로그인 성공", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(RegisterActivity.this, PersonalActivity.class);
                                                        intent.putExtra("studentId", studentId);
                                                        saveLoginState();
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "데이터베이스 저장 실패", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    });
                                }

                                @Override
                                public void onAuthFailure(String message) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(RegisterActivity.this, "U-SAINT 인증 실패: " + message, Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "데이터베이스 오류: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        });
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

    public void saveLoginState() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true); // 로그인 상태 저장
        editor.apply();
    }
}
