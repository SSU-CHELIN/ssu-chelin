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
import java.util.Collections;
import java.util.List;

public class RankingFragment extends Fragment {
    private TextView firstPlaceName, secondPlaceName, thirdPlaceName;
    private TextView firstPlaceScore, secondPlaceScore, thirdPlaceScore;
    private RecyclerView recyclerView;
    private RankingAdapter adapter;
    private View progressLayout; // 로딩 화면
    private View rankingLayout;  // 실제 랭킹 화면

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        // 로딩 및 랭킹 레이아웃 초기화
        progressLayout = view.findViewById(R.id.progress_layout); // 로딩 화면
        rankingLayout = view.findViewById(R.id.ranking_layout);   // 실제 랭킹 화면

        // 상위 3명 View 초기화
        firstPlaceName = view.findViewById(R.id.first_place_name);
        secondPlaceName = view.findViewById(R.id.second_place_name);
        thirdPlaceName = view.findViewById(R.id.third_place_name);
        firstPlaceScore = view.findViewById(R.id.first_place_score);
        secondPlaceScore = view.findViewById(R.id.second_place_score);
        thirdPlaceScore = view.findViewById(R.id.third_place_score);

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.ranking_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RankingAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        // 데이터 로드
        loadRankingData();

        return view;
    }

    private void loadRankingData() {
        // 로딩 화면 표시
        progressLayout.setVisibility(View.VISIBLE);
        rankingLayout.setVisibility(View.GONE);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> userList = new ArrayList<>();

                // 사용자 데이터 로드
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userName = userSnapshot.child("userinfo/userName").getValue(String.class);
                    Integer totalLike = userSnapshot.child("totalLike").getValue(Integer.class);
                    String studentId = userSnapshot.child("UserAccount/realStudentId").getValue(String.class);

                    if (totalLike == null) totalLike = 0;
                    if (studentId == null) studentId = "99999999";
                    if (userName != null) {
                        userList.add(new User(userName, studentId, totalLike));
                    }
                }

                // 정렬 및 데이터 설정
                Collections.sort(userList, (u1, u2) -> {
                    int diff = Integer.compare(u2.getTotalLike(), u1.getTotalLike());
                    if (diff == 0) {
                        return u1.getStudentId().compareTo(u2.getStudentId());
                    }
                    return diff;
                });

                updateUI(userList);

                // 로딩 화면 숨기기, 실제 UI 표시
                progressLayout.setVisibility(View.GONE);
                rankingLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show();
                progressLayout.setVisibility(View.GONE);
                rankingLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateUI(List<User> userList) {
        List<UserWithRank> rankedList = new ArrayList<>();

        // 순위 부여
        for (int i = 0; i < userList.size(); i++) {
            int rank = i + 1; // 순위는 1부터 시작
            rankedList.add(new UserWithRank(userList.get(i), rank));
        }

        // 상위 3명 업데이트
        updateTopThree(rankedList);

        // RecyclerView 업데이트
        List<UserWithRank> remainingUsers = new ArrayList<>();
        if (rankedList.size() > 3) {
            remainingUsers.addAll(rankedList.subList(3, rankedList.size()));
        }
        adapter.updateList(remainingUsers);
    }



    private void updateTopThree(List<UserWithRank> rankedList) {
        if (rankedList.size() > 0) {
            firstPlaceName.setText(rankedList.get(0).user.getName());
            firstPlaceScore.setText(String.valueOf(rankedList.get(0).user.getTotalLike()));
        } else {
            firstPlaceName.setText("-");
            firstPlaceScore.setText("0");
        }

        if (rankedList.size() > 1) {
            secondPlaceName.setText(rankedList.get(1).user.getName());
            secondPlaceScore.setText(String.valueOf(rankedList.get(1).user.getTotalLike()));
        } else {
            secondPlaceName.setText("-");
            secondPlaceScore.setText("0");
        }

        if (rankedList.size() > 2) {
            thirdPlaceName.setText(rankedList.get(2).user.getName());
            thirdPlaceScore.setText(String.valueOf(rankedList.get(2).user.getTotalLike()));
        } else {
            thirdPlaceName.setText("-");
            thirdPlaceScore.setText("0");
        }
    }


    public static class UserWithRank {
        User user;
        int rank;

        public UserWithRank(User user, int rank) {
            this.user = user;
            this.rank = rank;
        }
    }
}
