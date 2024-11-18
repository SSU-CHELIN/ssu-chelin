package com.example.ssuchelin.ranking;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

// 랭킹 화면 구현

public class RankingActivity extends AppCompatActivity {

    // 상위 3명 사용자 정보
    private TextView firstPlaceName, secondPlaceName, thirdPlaceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // 상위 3명 View 초기화
        firstPlaceName = findViewById(R.id.first_place_name);
        secondPlaceName = findViewById(R.id.second_place_name);
        thirdPlaceName = findViewById(R.id.third_place_name);

        // 상위 3명 사용자 이름 가져오기
        loadRankingData();
    }

    private void loadRankingData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> userList = new ArrayList<>();

                // 모든 사용자 데이터 가져오기
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userName = userSnapshot.child("userinfo/userName").getValue(String.class);

                    if (userName != null) {
                        userList.add(userName);
                    }
                }

                // 상위 3명의 사용자 이름을 TextView에 설정
                if (userList.size() > 0) {
                    firstPlaceName.setText(userList.get(0));
                }
                if (userList.size() > 1) {
                    secondPlaceName.setText(userList.get(1));
                }
                if (userList.size() > 2) {
                    thirdPlaceName.setText(userList.get(2));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
            }
        });
    }
}