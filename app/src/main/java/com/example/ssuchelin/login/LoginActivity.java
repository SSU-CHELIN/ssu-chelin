package com.example.ssuchelin.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ssuchelin.MainActivity;
import com.example.ssuchelin.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isUserLoggedIn()) {
            // 로그인 상태라면 메인 화면으로 이동
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        } else {
            // 로그인 화면 표시
            setContentView(R.layout.activity_login);
            ImageButton button = findViewById(R.id.login);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    public boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false); // 기본값은 false
    }
}