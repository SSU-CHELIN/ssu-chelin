package com.example.ssuchelin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

public class MainActivity extends AppCompatActivity {

    private static final int LOGIN_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        ImageButton loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://smartid.ssu.ac.kr/Symtra_sso/smln.asp?apiReturnUrl=ssuchelin://callback";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_REQUEST_CODE) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                if ("success".equals(uri.getQueryParameter("result"))) {
                    // 로그인 성공 - MainReviewActivity로 이동
                    Intent reviewIntent = new Intent(this, MainViewActivity.class);
                    startActivity(reviewIntent);
                    finish();
                } else {
                    // 로그인 실패
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
