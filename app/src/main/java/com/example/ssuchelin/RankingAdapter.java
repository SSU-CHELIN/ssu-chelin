package com.example.ssuchelin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

// 랭킹 4-10위 사용자를 동적으로 할당하여 표시하는 Adapter
public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private ArrayList<User> rankingList; // 사용자 랭킹 목록을 저장하는 리스트
    private Context context; // Context 객체, Activity와 연결하는데 사용

    // 생성자: 랭킹 리스트와 Context를 받아 Adapter를 초기화
    public RankingAdapter(ArrayList<User> rankingList, Context context) {
        this.rankingList = rankingList;
        this.context = context;
    }

    // 목록 업데이트 메서드: 새로운 사용자 리스트로 목록을 업데이트
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<User> newList) {
        rankingList = newList; // 새로운 사용자 리스트로 랭킹 리스트 업데이트
        notifyDataSetChanged(); // 데이터 변경을 RecyclerView에 알림
    }

    // ViewHolder 객체를 생성하는 메서드, XML 레이아웃을 객체로 변환
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item, parent, false);
        return new ViewHolder(view);
    }

    // ViewHolder에 데이터를 바인딩하는 메서드
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = rankingList.get(position); // 현재 위치의 사용자 데이터 가져오기
        holder.rankPosition.setText(String.valueOf(position + 4)); // 4위부터 시작하여 순위 표시
        holder.rankName.setText(user.getName()); // 사용자의 이름을 설정
        holder.rankScore.setText(String.valueOf(user.getScore())); // 사용자의 점수를 설정

        // 아이템 클릭 시 사용자 프로필 화면으로 이동
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserProfileActivity.class); // UserProfileActivity로 이동하는 Intent 생성
            intent.putExtra("user_id", user.getId()); // 사용자 ID를 Intent에 추가
            context.startActivity(intent); // Activity 시작
        });
    }

    // 아이템 수 반환
    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    // ViewHolder 클래스: 랭킹 아이템의 각 뷰를 참조하는 역할
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankPosition, rankName, rankScore; // 순위, 이름, 점수를 표시하는 TextView

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankPosition = itemView.findViewById(R.id.rank_position); // 순위를 표시하는 TextView 초기화
            rankName = itemView.findViewById(R.id.rank_name); // 이름을 표시하는 TextView 초기화
            rankScore = itemView.findViewById(R.id.rank_score); // 점수를 표시하는 TextView 초기화
        }
    }
}
