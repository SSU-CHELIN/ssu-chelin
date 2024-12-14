package com.example.ssuchelin.review;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuchelin.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewsList; // updateReviews를 위해 final 제거
    private final String studentId;
    private final DatabaseReference databaseReference;
    private List<String> reviewKeyList; // 필요하다면 나중에 reviewKey추가 가능

    public ReviewAdapter(List<Review> reviewsList, List<String> reviewKeyList, String studentId) {
        this.reviewsList = reviewsList;
        this.reviewKeyList = reviewKeyList;
        this.studentId = studentId;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("User").child(studentId).child("myReviewData");
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.overview_review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewsList.get(position);

        holder.usernameTextView.setText(review.getUsername());
        holder.reviewTextView.setText(review.getUserReview());

        // 간 정도, 맵기 정도, 알레르기 정보 설정 (allergy_info대신 개별 textView사용)
        holder.saltLevelTextView.setText("간 정도: " + review.getSaltPreference());
        holder.spicyLevelTextView.setText("맵기 정도: " + review.getSpicyPreference());
        holder.allergyInfoTextView.setText("알레르기: " + review.getAllergies());

        // 별점 표시
        holder.star1.setImageResource(review.getStarCount() >= 1 ? R.drawable.star_100 : R.drawable.star_0);
        holder.star2.setImageResource(review.getStarCount() >= 2 ? R.drawable.star_100 : R.drawable.star_0);
        holder.star3.setImageResource(review.getStarCount() >= 3 ? R.drawable.star_100 : R.drawable.star_0);

        // 좋아요/싫어요 상태
        holder.likeCountText.setText(String.valueOf(review.getLikeCount()));
        holder.likeIcon.setImageResource(review.isLiked() ? R.drawable.ic_like_on : R.drawable.ic_like_off);
        holder.dislikeCountText.setText(String.valueOf(review.getDislikeCount()));
        holder.dislikeIcon.setImageResource(review.isDisliked() ? R.drawable.ic_dislike_on : R.drawable.ic_dislike_off);

        // reviewKey없이 좋아요 기능 동작 어렵다면 reviewKeyList 관리 필요
        String reviewKey = (reviewKeyList.size() > position) ? reviewKeyList.get(position) : "unknownReviewKey";

        // 좋아요 버튼 클릭
        holder.likeIcon.setOnClickListener(v -> {
            boolean currentLiked = review.isLiked();
            boolean currentDisliked = review.isDisliked();
            int currentLikeCount = review.getLikeCount();
            int currentDislikeCount = review.getDislikeCount();

            int oldDifference = currentLikeCount - currentDislikeCount;

            if (!currentLiked) {
                if (currentDisliked) {
                    currentDisliked = false;
                    currentDislikeCount = Math.max(0, currentDislikeCount - 1);
                }
                currentLiked = true;
                currentLikeCount += 1;
            } else {
                currentLiked = false;
                currentLikeCount = Math.max(0, currentLikeCount - 1);
            }

            int newDifference = currentLikeCount - currentDislikeCount;

            review.setLiked(currentLiked);
            review.setLikeCount(currentLikeCount);
            review.setDisliked(currentDisliked);
            review.setDislikeCount(currentDislikeCount);

            holder.likeIcon.setImageResource(currentLiked ? R.drawable.ic_like_on : R.drawable.ic_like_off);
            holder.likeCountText.setText(String.valueOf(currentLikeCount));
            holder.dislikeIcon.setImageResource(currentDisliked ? R.drawable.ic_dislike_on : R.drawable.ic_dislike_off);
            holder.dislikeCountText.setText(String.valueOf(currentDislikeCount));

            int likeDifference = currentLikeCount - currentDislikeCount;

            databaseReference.child(reviewKey).child("liked").setValue(currentLiked);
            databaseReference.child(reviewKey).child("likeCount").setValue(currentLikeCount);
            databaseReference.child(reviewKey).child("disliked").setValue(currentDisliked);
            databaseReference.child(reviewKey).child("dislikeCount").setValue(currentDislikeCount);
            databaseReference.child(reviewKey).child("likeDifference").setValue(likeDifference);

            int differenceChange = newDifference - oldDifference;

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);
            userRef.child("totalLike").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Integer currentTotalLike = task.getResult().getValue(Integer.class);
                    if (currentTotalLike == null) currentTotalLike = 0;
                    int newTotalLike = currentTotalLike + differenceChange;
                    userRef.child("totalLike").setValue(newTotalLike);
                }
            });
        });

        // 싫어요 버튼 클릭
        holder.dislikeIcon.setOnClickListener(v -> {
            boolean currentLiked = review.isLiked();
            boolean currentDisliked = review.isDisliked();
            int currentLikeCount = review.getLikeCount();
            int currentDislikeCount = review.getDislikeCount();

            int oldDifference = currentLikeCount - currentDislikeCount;

            if (!currentDisliked) {
                if (currentLiked) {
                    currentLiked = false;
                    currentLikeCount = Math.max(0, currentLikeCount - 1);
                }

                currentDisliked = true;
                currentDislikeCount += 1;
            } else {
                currentDisliked = false;
                currentDislikeCount = Math.max(0, currentDislikeCount - 1);
            }

            int newDifference = currentLikeCount - currentDislikeCount;

            review.setLiked(currentLiked);
            review.setLikeCount(currentLikeCount);
            review.setDisliked(currentDisliked);
            review.setDislikeCount(currentDislikeCount);

            holder.likeIcon.setImageResource(currentLiked ? R.drawable.ic_like_on : R.drawable.ic_like_off);
            holder.likeCountText.setText(String.valueOf(currentLikeCount));
            holder.dislikeIcon.setImageResource(currentDisliked ? R.drawable.ic_dislike_on : R.drawable.ic_dislike_off);
            holder.dislikeCountText.setText(String.valueOf(currentDislikeCount));

            int likeDifference = newDifference;

            databaseReference.child(reviewKey).child("liked").setValue(currentLiked);
            databaseReference.child(reviewKey).child("likeCount").setValue(currentLikeCount);
            databaseReference.child(reviewKey).child("disliked").setValue(currentDisliked);
            databaseReference.child(reviewKey).child("dislikeCount").setValue(currentDislikeCount);
            databaseReference.child(reviewKey).child("likeDifference").setValue(likeDifference);

            int differenceChange = newDifference - oldDifference;

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);
            userRef.child("totalLike").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Integer currentTotalLike = task.getResult().getValue(Integer.class);
                    if (currentTotalLike == null) currentTotalLike = 0;
                    int newTotalLike = currentTotalLike + differenceChange;
                    userRef.child("totalLike").setValue(newTotalLike);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public void updateReviews(List<Review> newReviews) {
        this.reviewsList.clear();
        this.reviewsList.addAll(newReviews);
        notifyDataSetChanged();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, reviewTextView;
        // 개별적으로 salt_level, spicy_level, allergy_info 사용
        TextView saltLevelTextView, spicyLevelTextView, allergyInfoTextView;
        ImageView star1, star2, star3;
        ImageView likeIcon;
        TextView likeCountText;
        ImageView dislikeIcon;
        TextView dislikeCountText;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.review_username);
            reviewTextView = itemView.findViewById(R.id.review_content);
            saltLevelTextView = itemView.findViewById(R.id.salt_level);
            spicyLevelTextView = itemView.findViewById(R.id.spicy_level);
            allergyInfoTextView = itemView.findViewById(R.id.allergy_info);
            star1 = itemView.findViewById(R.id.review_star1);
            star2 = itemView.findViewById(R.id.review_star2);
            star3 = itemView.findViewById(R.id.review_star3);

            likeIcon = itemView.findViewById(R.id.like_icon);
            likeCountText = itemView.findViewById(R.id.like_count_text);

            dislikeIcon = itemView.findViewById(R.id.dislike_icon);
            dislikeCountText = itemView.findViewById(R.id.dislike_count_text);
        }
    }
}
