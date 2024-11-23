package com.example.ssuchelin.ranking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuchelin.R;
import com.example.ssuchelin.user.User;
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
    private RecyclerView recyclerView;
    private RankingAdapter adapter;
    private List<User> rankingList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);


        // 상위 3명 View 초기화
        firstPlaceName = view.findViewById(R.id.first_place_name);
        secondPlaceName = view.findViewById(R.id.second_place_name);
        thirdPlaceName = view.findViewById(R.id.third_place_name);

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.ranking_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RankingAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);
        // 상위 3명 사용자 이름 가져오기
        loadRankingData();

        return view;
    }

    private void loadRankingData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList<>();
                List<User> remainingUsers = new ArrayList<>();

                // 모든 사용자 데이터 가져오기
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userName = userSnapshot.child("userinfo/userName").getValue(String.class);

                    if (userName != null) {
                        User user = new User(userName); // User 객체 생성
                        userList.add(user); // 리스트에 추가
                    }
                }
                //Toast.makeText(getContext(), "총 사용자 수: " + userList.size(), Toast.LENGTH_SHORT).show();

                // 상위 3명 설정
                if (userList.size() > 0) {
                    User first = userList.remove(userList.size() - 1);
                    firstPlaceName.setText(first.getName());
                }
                if (userList.size() > 0) {
                    User second = userList.remove(userList.size() - 1);
                    secondPlaceName.setText(second.getName());
                }
                if (userList.size() > 0) {
                    User third = userList.remove(userList.size() - 1);
                    thirdPlaceName.setText(third.getName());
                }

                // 나머지 사용자 리스트 생성
                remainingUsers.addAll(userList);

                // 나머지 데이터 RecyclerView에 전달
                adapter.updateList(remainingUsers);
                //Toast.makeText(getContext(), "RecyclerView에 전달된 데이터 수: " + userList.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
            }
        });
    }


}
