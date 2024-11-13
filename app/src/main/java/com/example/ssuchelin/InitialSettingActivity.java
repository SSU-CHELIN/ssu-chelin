package com.example.ssuchelin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

// 프로필, 프로필정보 바꾸기 누르고 각 버튼 누를시

public class InitialSettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_initial_setting);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 제목 숨기기 및 기본 뒤로가기 아이콘(<) 활성화
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // 제목 숨기기
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 아이콘 활성화
        }

        // 버튼 클릭 시 다른 화면으로 이동 설정
        findViewById(R.id.change_profile_picture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialSettingActivity.this, ChangeProfilePictureActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.change_nickname_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialSettingActivity.this, ChangeNicknameActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.change_contact_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialSettingActivity.this, ChangeContactActivity.class);
                startActivity(intent);
            }
        });
    }

    // 뒤로가기 버튼 클릭 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 이전 화면으로 돌아감
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
