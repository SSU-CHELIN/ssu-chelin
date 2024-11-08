package com.example.ssuchelin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginCallBackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri =getIntent().getData();

        if (uri != null && uri.toString().startsWith("https://saint.ssu.ac.kr/webSSO/sso.jsp")) {
            // 로그인 성공 여부 확인
            String token = uri.getQueryParameter("token");
            if (token != null) {
                Intent successIntent = new Intent(this, MainViewActivity.class);
                successIntent.putExtra("token", token);  // 필요시 토큰 전달
                startActivity(successIntent);
            } else {
                Intent errorIntent = new Intent(this, MainActivity.class);
                startActivity(errorIntent);

            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}