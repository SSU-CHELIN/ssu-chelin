package com.example.ssuchelin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;

// 랭킹 화면을 구현하는 Activity
public class RankingActivity extends AppCompatActivity {

    private RecyclerView recyclerView; // 나머지 사용자 목록을 표시하는 RecyclerView
    private RankingAdapter adapter; // RecyclerView의 Adapter
    private ArrayList<User> rankingList; // 전체 랭킹 사용자 목록을 저장하는 리스트

    // 상위 3명의 사용자 정보를 표시하기 위한 뷰
    private ImageView firstPlaceImage, secondPlaceImage, thirdPlaceImage; // 상위 3명의 프로필 이미지를 표시하는 ImageView
    private TextView firstPlaceName, firstPlaceScore, secondPlaceName, secondPlaceScore, thirdPlaceName, thirdPlaceScore; // 상위 3명의 이름과 점수를 표시하는 TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // 상위 3명 View 초기화
        firstPlaceImage = findViewById(R.id.first_place_image); // 1위 사용자의 프로필 이미지 뷰
        firstPlaceName = findViewById(R.id.first_place_name); // 1위 사용자의 이름 뷰
        firstPlaceScore = findViewById(R.id.first_place_score); // 1위 사용자의 점수 뷰

        secondPlaceImage = findViewById(R.id.second_place_image); // 2위 사용자의 프로필 이미지 뷰
        secondPlaceName = findViewById(R.id.second_place_name); // 2위 사용자의 이름 뷰
        secondPlaceScore = findViewById(R.id.second_place_score); // 2위 사용자의 점수 뷰

        thirdPlaceImage = findViewById(R.id.third_place_image); // 3위 사용자의 프로필 이미지 뷰
        thirdPlaceName = findViewById(R.id.third_place_name); // 3위 사용자의 이름 뷰
        thirdPlaceScore = findViewById(R.id.third_place_score); // 3위 사용자의 점수 뷰

        // 상위 3명 클릭 리스너 설정
        findViewById(R.id.first_place_layout).setOnClickListener(v -> openUserProfile("first_place_id")); // 첫 번째 사용자 클릭 시 프로필 화면으로 이동
        findViewById(R.id.second_place_layout).setOnClickListener(v -> openUserProfile("second_place_id")); // 두 번째 사용자 클릭 시 프로필 화면으로 이동
        findViewById(R.id.third_place_layout).setOnClickListener(v -> openUserProfile("third_place_id")); // 세 번째 사용자 클릭 시 프로필 화면으로 이동

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.ranking_recycler_view); // 나머지 사용자 목록을 표시하는 RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 세로로 나열되는 목록 형식 설정
        rankingList = new ArrayList<>(); // 랭킹 리스트 초기화
        adapter = new RankingAdapter(rankingList, this); // 어댑터 초기화
        recyclerView.setAdapter(adapter); // 어댑터 설정

        // 데이터 로드
//        loadRankingData(); // 파이어베이스에서 랭킹 데이터를 불러오는 함수 호출
    }
/*
    // Firebase Realtime Database에서 데이터를 가져와 추천수 기준으로 정렬하고 상위 3명과 나머지 순위를 표시하는 함수
    private void loadRankingData() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("rankings"); // Firebase 데이터베이스에서 "rankings" 경로 참조

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rankingList.clear(); // 이전 데이터를 제거

                // Firebase에서 데이터 가져오기
                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class); // 각 사용자 데이터를 User 객체로 변환
                    if (user != null) {
                        rankingList.add(user); // 사용자 정보를 랭킹 리스트에 추가
                    }
                }

                // 추천수 기준으로 정렬 (추천수가 높은 순으로)
                Collections.sort(rankingList, (u1, u2) -> Integer.compare(u2.getScore(), u1.getScore()));

                // 상위 3명 표시
                if (rankingList.size() > 0) displayTopUser(firstPlaceImage, firstPlaceName, firstPlaceScore, rankingList.get(0)); // 1위 사용자 표시
                if (rankingList.size() > 1) displayTopUser(secondPlaceImage, secondPlaceName, secondPlaceScore, rankingList.get(1)); // 2위 사용자 표시
                if (rankingList.size() > 2) displayTopUser(thirdPlaceImage, thirdPlaceName, thirdPlaceScore, rankingList.get(2)); // 3위 사용자 표시

                // 상위 3명을 제외한 4위 이후 사용자 추가
                ArrayList<User> subList = new ArrayList<>(rankingList.subList(Math.min(3, rankingList.size()), rankingList.size())); // 4위부터 나머지 사용자
                adapter.updateList(subList); // 어댑터에 데이터 업데이트
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 데이터베이스 접근 오류 처리
            }
        });
    }
*/
    // 상위 3명의 사용자 정보를 설정하는 함수
    private void displayTopUser(ImageView imageView, TextView nameView, TextView scoreView, User user) {
        nameView.setText(user.getName()); // 사용자 이름 설정
        scoreView.setText(String.valueOf(user.getScore())); // 사용자 점수 설정
        Glide.with(this).load(user.getProfileImageUrl()).into(imageView); // 사용자 프로필 이미지 로드
    }

    // 특정 사용자의 프로필 화면으로 이동하는 함수
    private void openUserProfile(String userId) {
        Intent intent = new Intent(this, UserProfileActivity.class); // UserProfileActivity로 이동하는 Intent 생성
        intent.putExtra("user_id", userId); // 사용자 ID를 Intent에 추가
        startActivity(intent); // Activity 시작
    }
}
