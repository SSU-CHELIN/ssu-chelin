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

//랭킹화면 구현

public class RankingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RankingAdapter adapter;
    private ArrayList<User> rankingList;

    // 상위 3명 사용자 정보
    private ImageView firstPlaceImage, secondPlaceImage, thirdPlaceImage;
    private TextView firstPlaceName, firstPlaceScore, secondPlaceName, secondPlaceScore, thirdPlaceName, thirdPlaceScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // 상위 3명 View 초기화
        firstPlaceImage = findViewById(R.id.first_place_image);
        firstPlaceName = findViewById(R.id.first_place_name);
        firstPlaceScore = findViewById(R.id.first_place_score);

        secondPlaceImage = findViewById(R.id.second_place_image);
        secondPlaceName = findViewById(R.id.second_place_name);
        secondPlaceScore = findViewById(R.id.second_place_score);

        thirdPlaceImage = findViewById(R.id.third_place_image);
        thirdPlaceName = findViewById(R.id.third_place_name);
        thirdPlaceScore = findViewById(R.id.third_place_score);

        // 상위 3명 클릭 리스너 설정
        findViewById(R.id.first_place_layout).setOnClickListener(v -> openUserProfile("first_place_id"));
        findViewById(R.id.second_place_layout).setOnClickListener(v -> openUserProfile("second_place_id"));
        findViewById(R.id.third_place_layout).setOnClickListener(v -> openUserProfile("third_place_id"));

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.ranking_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingList = new ArrayList<>();
        adapter = new RankingAdapter(rankingList, this);
        recyclerView.setAdapter(adapter);

        // 데이터 로드
        loadRankingData();
    }


    //파베, Firebase Realtime Database를 사용한 데이터 가져오기 / 주의할 점 : User 객체의 FirebaseDatabase에 올바르게 저장되어있어야함, rankings 경로 아래 각 사용자 데이터가 User 클래스 구조에 맞게 저장되어 있어야 함
    private void loadRankingData() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("rankings");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rankingList.clear();
                int count = 0;

                // Firebase에서 데이터 가져오기
                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);

                    if (count == 0) {
                        displayTopUser(firstPlaceImage, firstPlaceName, firstPlaceScore, user);
                    } else if (count == 1) {
                        displayTopUser(secondPlaceImage, secondPlaceName, secondPlaceScore, user);
                    } else if (count == 2) {
                        displayTopUser(thirdPlaceImage, thirdPlaceName, thirdPlaceScore, user);
                    } else {
                        rankingList.add(user); // 4위 이후 순위 추가
                    }
                    count++;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        });
    }

    //파베, 사용자 이미지 로드 / 주의할 점 : Glide 라이브러리가 프로젝트에 포함되어 있어야 함
    private void displayTopUser(ImageView imageView, TextView nameView, TextView scoreView, User user) {
        nameView.setText(user.getName());
        scoreView.setText(String.valueOf(user.getScore()));
        Glide.with(this).load(user.getProfileImageUrl()).into(imageView); // 프로필 이미지 로드
    }

    private void openUserProfile(String userId) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }
}
