package com.example.ssuchelin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.core.view.WindowCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewActivity extends BaseActivity {

    private DatabaseReference database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentLayout(R.layout.activity_profile_view);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        // 현재 로그인한 사용자의 UID 가져오기
        String userId = auth.getCurrentUser().getUid();

        if (userId != null) {
            fetchUserData(userId);
        }
    }

    private void fetchUserData(String userId) {
        // 데이터베이스에서 사용자의 학번과 이름 가져오기
        database.child("User").child(userId).child("UserAccount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String realStudentId = snapshot.child("realStudentId").getValue(String.class);
                String userName = snapshot.child("userinfo").child("userName").getValue(String.class);

                ((TextView) findViewById(R.id.student_id)).setText(realStudentId != null ? realStudentId : "Unknown ID");
                ((TextView) findViewById(R.id.username)).setText(userName != null ? userName : "Unknown Name");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // 에러 처리
                error.toException().printStackTrace();
            }
        });
    }
}
