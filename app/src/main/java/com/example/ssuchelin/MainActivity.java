package com.example.ssuchelin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
    }

    // Login 버튼 클릭 시 호출되는 메서드
    public void navigateToLogin(View view) {
        Intent intent = new Intent(this, ProfileActivity.class); // 이동할 액티비티로 변경
        startActivity(intent);
    }
}
