package com.example.ssuchelin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.example.ssuchelin.R;

// 프로필, 프로필정보 바꾸기 누르고 각 버튼 누를시

public class InitialSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setting); // 초기 설정 화면 레이아웃 설정

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar); // Toolbar 객체 초기화
        setSupportActionBar(toolbar); // Toolbar를 앱의 액션바로 설정

        // 제목 숨기기 및 기본 뒤로가기 아이콘(<) 활성화
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Toolbar 제목 숨기기
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 기본 뒤로가기 아이콘 활성화
        }

        // 버튼 클릭 시 다른 화면으로 이동 설정
        findViewById(R.id.change_profile_picture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialSettingActivity.this, ChangeProfilePictureActivity.class); // 프로필 사진 변경 화면으로 이동
                startActivity(intent); // 인텐트를 통해 화면 전환
            }
        });

        findViewById(R.id.change_nickname_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialSettingActivity.this, ChangeNicknameActivity.class); // 닉네임 변경 화면으로 이동
                startActivity(intent); // 인텐트를 통해 화면 전환
            }
        });

        findViewById(R.id.change_contact_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialSettingActivity.this, ChangeContactActivity.class); // 연락처 변경 화면으로 이동
                startActivity(intent); // 인텐트를 통해 화면 전환
            }
        });
    }

    // 뒤로가기 버튼 클릭 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // 뒤로가기 버튼 클릭 시
            finish(); // 현재 액티비티 종료 (이전 화면으로 돌아감)
            return true;
        }
        return super.onOptionsItemSelected(item); // 기본 이벤트 처리
    }
}
