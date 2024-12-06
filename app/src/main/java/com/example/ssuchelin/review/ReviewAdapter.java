package com.example.ssuchelin.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final List<Review> reviewsList;
    private final List<String> reviewKeyList;
    private final DatabaseReference databaseReference;
    private final String studentId;

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
        holder.reviewPreferences.setText("간 정도 : " + review.getSaltPreference() + " / 맵기 정도 : " + review.getSpicyPreference() + " / 알레르기 : " + review.getAllergies());

        // 별점 표시
        holder.star1.setImageResource(review.getStarCount() >= 1 ? R.drawable.star : R.drawable.star_off);
        holder.star2.setImageResource(review.getStarCount() >= 2 ? R.drawable.star : R.drawable.star_off);
        holder.star3.setImageResource(review.getStarCount() >= 3 ? R.drawable.star : R.drawable.star_off);

        // 좋아요 상태
        holder.likeCountText.setText(String.valueOf(review.getLikeCount()));
        holder.likeIcon.setImageResource(review.isLiked() ? R.drawable.ic_like_on : R.drawable.ic_like_off);

        // 싫어요 상태
        holder.dislikeCountText.setText(String.valueOf(review.getDislikeCount()));
        holder.dislikeIcon.setImageResource(review.isDisliked() ? R.drawable.ic_dislike_on : R.drawable.ic_dislike_off);

        // 좋아요 버튼 클릭
        holder.likeIcon.setOnClickListener(v -> {
            boolean currentLiked = review.isLiked();
            boolean currentDisliked = review.isDisliked();
            int currentLikeCount = review.getLikeCount();
            int currentDislikeCount = review.getDislikeCount();
            String reviewKey = reviewKeyList.get(position);

            // 변경 전 차이 계산 (oldDifference)
            int oldDifference = currentLikeCount - currentDislikeCount;

            if (!currentLiked) {
                // 좋아요 켤 때 싫어요 켜져있으면 끄기
                if (currentDisliked) {
                    currentDisliked = false;
                    currentDislikeCount = Math.max(0, currentDislikeCount - 1);
                }

                currentLiked = true;
                currentLikeCount += 1;
            } else {
                // 좋아요 꺼기
                currentLiked = false;
                currentLikeCount = Math.max(0, currentLikeCount - 1);
            }

            // 변경 후 차이 계산 (newDifference)
            int newDifference = currentLikeCount - currentDislikeCount;

            review.setLiked(currentLiked);
            review.setLikeCount(currentLikeCount);
            review.setDisliked(currentDisliked);
            review.setDislikeCount(currentDislikeCount);

            holder.likeIcon.setImageResource(currentLiked ? R.drawable.ic_like_on : R.drawable.ic_like_off);
            holder.likeCountText.setText(String.valueOf(currentLikeCount));

            holder.dislikeIcon.setImageResource(currentDisliked ? R.drawable.ic_dislike_on : R.drawable.ic_dislike_off);
            holder.dislikeCountText.setText(String.valueOf(currentDislikeCount));

            // likeDifference 계산
            int likeDifference = currentLikeCount - currentDislikeCount;

            // Firebase 업데이트
            databaseReference.child(reviewKey).child("liked").setValue(currentLiked);
            databaseReference.child(reviewKey).child("likeCount").setValue(currentLikeCount);
            databaseReference.child(reviewKey).child("disliked").setValue(currentDisliked);
            databaseReference.child(reviewKey).child("dislikeCount").setValue(currentDislikeCount);
            databaseReference.child(reviewKey).child("likeDifference").setValue(likeDifference);

            // totalLike 업데이트
            // totalLike = oldTotalLike + (newDifference - oldDifference)
            int differenceChange = newDifference - oldDifference;

            // studentId는 어댑터나 상위 클래스에서 받아온 값
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);

            // 현재 totalLike를 가져와서 differenceChange만큼 더해 업데이트
            userRef.child("totalLike").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer currentTotalLike = snapshot.getValue(Integer.class);
                    if (currentTotalLike == null) currentTotalLike = 0;
                    int newTotalLike = currentTotalLike + differenceChange;
                    userRef.child("totalLike").setValue(newTotalLike);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });

        // 싫어요 버튼 클릭
        holder.dislikeIcon.setOnClickListener(v -> {
            boolean currentLiked = review.isLiked();
            boolean currentDisliked = review.isDisliked();
            int currentLikeCount = review.getLikeCount();
            int currentDislikeCount = review.getDislikeCount();
            String reviewKey = reviewKeyList.get(position);

            // 변경 전 차이
            int oldDifference = currentLikeCount - currentDislikeCount;

            if (!currentDisliked) {
                // 싫어요 켤 때 좋아요 켜져있으면 끄기
                if (currentLiked) {
                    currentLiked = false;
                    currentLikeCount = Math.max(0, currentLikeCount - 1);
                }

                currentDisliked = true;
                currentDislikeCount += 1;
            } else {
                // 싫어요 꺼기
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

            // likeDifference 업데이트
            int likeDifference = newDifference;

            // Firebase 업데이트
            databaseReference.child(reviewKey).child("liked").setValue(currentLiked);
            databaseReference.child(reviewKey).child("likeCount").setValue(currentLikeCount);
            databaseReference.child(reviewKey).child("disliked").setValue(currentDisliked);
            databaseReference.child(reviewKey).child("dislikeCount").setValue(currentDislikeCount);
            databaseReference.child(reviewKey).child("likeDifference").setValue(likeDifference);

            int differenceChange = newDifference - oldDifference;

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);
            userRef.child("totalLike").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer currentTotalLike = snapshot.getValue(Integer.class);
                    if (currentTotalLike == null) currentTotalLike = 0;
                    int newTotalLike = currentTotalLike + differenceChange;
                    userRef.child("totalLike").setValue(newTotalLike);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });

        // 수정 버튼
        holder.editbtn.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "수정 클릭됨: " + review.getUserReview(), Toast.LENGTH_SHORT).show();

            FragmentActivity activity = (FragmentActivity) holder.itemView.getContext();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();

            WriteReviewFragment writeReviewFragment = new WriteReviewFragment();

            Bundle bundle = new Bundle();
            bundle.putBoolean("editMode", true);
            bundle.putString("reviewText", review.getUserReview());
            bundle.putInt("reviewId", position);
            bundle.putFloat("starCount", review.getStarCount());
            writeReviewFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, writeReviewFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, reviewTextView, reviewPreferences;
        ImageView star1, star2, star3;
        TextView editbtn;

        ImageView likeIcon;
        TextView likeCountText;

        ImageView dislikeIcon;
        TextView dislikeCountText;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.review_username);
            reviewTextView = itemView.findViewById(R.id.review_content);
            reviewPreferences = itemView.findViewById(R.id.review_preferences);
            star1 = itemView.findViewById(R.id.review_star1);
            star2 = itemView.findViewById(R.id.review_star2);
            star3 = itemView.findViewById(R.id.review_star3);
            editbtn = itemView.findViewById(R.id.edit_button);

            likeIcon = itemView.findViewById(R.id.like_icon);
            likeCountText = itemView.findViewById(R.id.like_count_text);

            dislikeIcon = itemView.findViewById(R.id.dislike_icon);
            dislikeCountText = itemView.findViewById(R.id.dislike_count_text);
        }
    }
}
