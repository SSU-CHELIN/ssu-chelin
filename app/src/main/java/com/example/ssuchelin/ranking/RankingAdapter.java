package com.example.ssuchelin.ranking;

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

//랭킹 4-10위 동적할당?

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private ArrayList<User> rankingList;
    private Context context;

    public RankingAdapter(ArrayList<User> rankingList, Context context) {
        this.rankingList = rankingList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = rankingList.get(position);
        holder.rankPosition.setText(String.valueOf(position + 4));
        holder.rankName.setText(user.getName());
        holder.rankScore.setText(String.valueOf(user.getScore()));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("user_id", user.getId());
            context.startActivity(intent);
        });
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
