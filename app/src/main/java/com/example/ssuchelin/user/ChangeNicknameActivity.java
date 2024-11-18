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

// (프로필정보바꾸기) 닉네임변경 클릭시

public class ChangeNicknameActivity extends AppCompatActivity {

    // 사용자가 새로 입력한 닉네임을 담는 EditText 필드
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

        // 새로운 닉네임 입력 필드 초기화
        newNickname = findViewById(R.id.new_nickname);

        // '저장' 버튼 클릭 시 닉네임 저장 메서드 호출
        findViewById(R.id.save_nickname_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNickname();
            }
        });
    }

    /**
     * 사용자가 입력한 닉네임을 저장하는 메서드
     * 닉네임이 비어있지 않을 경우 Firebase에 저장하고, 성공 메시지를 보여줌
     * 비어있을 경우 "닉네임을 입력하세요." 메시지를 보여줌
     */
    private void saveNickname() {
        String nickname = newNickname.getText().toString().trim(); // 입력된 닉네임을 가져와 앞뒤 공백 제거
        if (!nickname.isEmpty()) {
            // Firebase 데이터베이스에 저장하는 코드 (주석처리)
            // DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child("user_id"); //파베
            // userRef.child("nickname").setValue(nickname); // 현재 user_id가 고정되어 있음, 실제 앱에서는 사용자의 고유 ID를 가져와 해당 ID에 맞는 닉네임을 저장해야 함
            Toast.makeText(this, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show(); // 성공 메시지 표시
        } else {
            Toast.makeText(this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show(); // 닉네임 입력 요청 메시지 표시
        }
    }

    /**
     * Toolbar의 뒤로가기 버튼 클릭 이벤트 처리
     * 뒤로가기 버튼이 클릭되면 현재 Activity를 종료하고 이전 화면으로 돌아감
     *
     * @param item 메뉴 항목
     * @return 클릭 처리 여부
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // 뒤로가기 버튼이 클릭되었을 때
            finish(); // 현재 화면을 종료하여 이전 화면으로 돌아감
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
