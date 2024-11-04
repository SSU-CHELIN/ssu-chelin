package com.example.ssuchelin;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//(프로필정보바꾸기) 닉네임변경 클릭시

public class activity_change_nickname extends AppCompatActivity {

    private EditText newNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);

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
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child("user_id");
            userRef.child("nickname").setValue(nickname);
            Toast.makeText(this, "닉네임이 변경되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
