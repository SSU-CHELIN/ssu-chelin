package com.example.ssuchelin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private final List<Review> reviewsList;

    // 어댑터 초기화 시 데이터 리스트를 받음
    public ReviewsAdapter(List<Review> reviewsList) {
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용해 리뷰 아이템 레이아웃을 인플레이트
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        // 각 아이템에 데이터를 바인딩
        Review review = reviewsList.get(position);

        // 데이터 설정
        holder.usernameTextView.setText(review.getUsername());
        holder.reviewTextView.setText(review.getUserReview());

        // 별점 설정 (별점 수에 맞춰 아이콘 표시)
        holder.star1.setImageResource(review.getStarCount() >= 1 ? R.drawable.star : R.drawable.star_off);
        holder.star2.setImageResource(review.getStarCount() >= 2 ? R.drawable.star : R.drawable.star_off);
        holder.star3.setImageResource(review.getStarCount() >= 3 ? R.drawable.star : R.drawable.star_off);
    }

    @Override
    public int getItemCount() {
        return reviewsList.size(); // 리뷰의 개수를 반환
    }

    // Review 객체를 담는 뷰홀더 클래스
    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView, reviewTextView;
        ImageView star1, star2, star3;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username); // 사용자 이름
            reviewTextView = itemView.findViewById(R.id.myreview); // 리뷰 텍스트
            star1 = itemView.findViewById(R.id.star1); // 첫 번째 별 이미지
            star2 = itemView.findViewById(R.id.star2); // 두 번째 별 이미지
            star3 = itemView.findViewById(R.id.star3); // 세 번째 별 이미지
        }
    }
}
