package com.example.ssuchelin.user;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ssuchelin.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//(프로필정보바꾸기) 닉네임변경 클릭시

public class ChangeNicknameActivity extends AppCompatActivity {

    private EditText newNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 제목 숨기기 및 기본 뒤로가기 아이콘(<) 활성화
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // 제목 숨기기
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 아이콘 활성화
        }

        newNickname = findViewById(R.id.new_nickname);

        findViewById(R.id.save_nickname_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNickname();
            }
        });
    }

    private void saveNickname() {
        String nickname = newNickname.getText().toString().trim();
        if (!nickname.isEmpty()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child("user_id"); //파베
            userRef.child("nickname").setValue(nickname); //현재 user_id가 고정되어있음, 실제 앱에서는 고유 ID를 가져와서 해당 ID에 맞는 닉네임 저장해야함
            Toast.makeText(this, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    // 뒤로가기 버튼 클릭 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 이전 화면으로 돌아감
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
