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
import java.util.Comparator;
import java.util.List;


public class RankingFragment extends Fragment {
    private TextView firstPlaceName, secondPlaceName, thirdPlaceName;
    private TextView firstPlaceScore, secondPlaceScore, thirdPlaceScore;
    private RecyclerView recyclerView;
    private RankingAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

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

        // 랭킹 데이터 로드
        loadRankingData();

        return view;
    }

    private void loadRankingData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> userList = new ArrayList<>();

                // 모든 사용자 데이터 가져오기
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userName = userSnapshot.child("userinfo/userName").getValue(String.class);
                    Integer totalLike = userSnapshot.child("totalLike").getValue(Integer.class);
                    String studentId = userSnapshot.child("UserAccount/realStudentId").getValue(String.class);

                    if (totalLike == null) totalLike = 0;
                    if (studentId == null) studentId = "99999999"; // Null 대비 기본값
                    if (userName != null) {
                        User user = new User(userName, studentId, totalLike);
                        userList.add(user);
                    }
                }

                // totalLike 내림차순, 같으면 studentId 오름차순 정렬
                Collections.sort(userList, new Comparator<User>() {
                    @Override
                    public int compare(User u1, User u2) {
                        // totalLike 내림차순
                        int diff = Integer.compare(u2.getTotalLike(), u1.getTotalLike());
                        if (diff == 0) {
                            // totalLike 같으면 studentId 오름차순
                            return u1.getStudentId().compareTo(u2.getStudentId());
                        }
                        return diff;
                    }
                });

                // 순위 매기기 (동순위 허용)
                // 최대 10명까지만 처리
                int limit = Math.min(10, userList.size());
                List<UserWithRank> rankedList = new ArrayList<>();

                if (limit > 0) {
                    // 첫 번째 유저는 rank = 1
                    int currentRank = 1;
                    int prevLike = userList.get(0).getTotalLike();
                    rankedList.add(new UserWithRank(userList.get(0), currentRank));

                    for (int i = 1; i < limit; i++) {
                        User currentUser = userList.get(i);
                        int currentLike = currentUser.getTotalLike();

                        if (currentLike == prevLike) {
                            // 이전과 totalLike 동일하면 같은 rank
                            rankedList.add(new UserWithRank(currentUser, currentRank));
                        } else {
                            // 다르면 rank = i+1
                            currentRank = i + 1;
                            rankedList.add(new UserWithRank(currentUser, currentRank));
                        }
                        prevLike = currentLike;
                    }
                }

                // 상위 3명 설정
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

                // 4위부터 나머지 RecyclerView에 설정
                List<UserWithRank> remainingUsers = new ArrayList<>();
                if (rankedList.size() > 3) {
                    for (int i = 3; i < rankedList.size(); i++) {
                        remainingUsers.add(rankedList.get(i));
                    }
                }

                adapter.updateList(remainingUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 순위와 함께 User를 담을 내부 클래스
    static class UserWithRank {
        User user;
        int rank;
        UserWithRank(User user, int rank) {
            this.user = user;
            this.rank = rank;
        }
    }
}
