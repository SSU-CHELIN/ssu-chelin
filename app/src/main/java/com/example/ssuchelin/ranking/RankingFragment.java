package com.example.ssuchelin.ranking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RankingFragment extends Fragment {

    // 상위 3명 사용자 정보
    private TextView firstPlaceName, secondPlaceName, thirdPlaceName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);


        // 상위 3명 View 초기화
        firstPlaceName = view.findViewById(R.id.first_place_name);
        secondPlaceName = view.findViewById(R.id.second_place_name);
        thirdPlaceName = view.findViewById(R.id.third_place_name);

        // 상위 3명 사용자 이름 가져오기
        loadRankingData();

        return view;
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
