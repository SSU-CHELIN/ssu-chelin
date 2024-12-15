package com.example.ssuchelin.ranking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuchelin.R;
import com.example.ssuchelin.user.User;
import com.example.ssuchelin.user.UserProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    // UserWithRank 리스트로 변경
    private ArrayList<RankingFragment.UserWithRank> rankingList;
    private Context context;

    public RankingAdapter(ArrayList<RankingFragment.UserWithRank> rankingList, Context context) {
        this.rankingList = rankingList;
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<RankingFragment.UserWithRank> newList) {
        rankingList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.ViewHolder holder, int position) {
        RankingFragment.UserWithRank uwr = rankingList.get(position);
        User user = uwr.user;

        holder.rankPosition.setText(String.valueOf(uwr.rank));
        holder.rankName.setText(user.getName());
        holder.rankScore.setText(String.valueOf(user.getTotalLike()));

//        holder.itemView.setOnClickListener(view -> {
//            Intent intent = new Intent(context, UserProfileActivity.class);
//            // user.getStudentId()를 user_id로 전달
//            intent.putExtra("user_id", user.getStudentId());
//            context.startActivity(intent);
//        });
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankPosition, rankName, rankScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankPosition = itemView.findViewById(R.id.rank_position);
            rankName = itemView.findViewById(R.id.rank_name);
            rankScore = itemView.findViewById(R.id.rank_score);
        }
    }
}
