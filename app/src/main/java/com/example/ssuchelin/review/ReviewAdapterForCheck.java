package com.example.ssuchelin.review;

import android.content.Intent;
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

import java.util.List;

public class ReviewAdapterForCheck extends RecyclerView.Adapter<ReviewAdapterForCheck.ReviewViewHolder> {

    private List<Review> reviewsList;
    private List<String> reviewKeys;
    private String userId;
    private DatabaseReference databaseReference;

    public ReviewAdapterForCheck(List<Review> reviewsList, List<String> reviewKeys, String userId) {
        this.reviewsList = reviewsList;
        this.reviewKeys = reviewKeys;
        this.userId = userId;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userId).child("myReviewData");
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_review.xml 사용
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewsList.get(position);
        holder.usernameTextView.setText(review.getUsername());
        holder.reviewTextView.setText(review.getUserReview());

        holder.star1.setImageResource(review.getStarCount() >= 1 ? R.drawable.star_100 : R.drawable.star_0);
        holder.star2.setImageResource(review.getStarCount() >= 2 ? R.drawable.star_100 : R.drawable.star_0);
        holder.star3.setImageResource(review.getStarCount() >= 3 ? R.drawable.star_100 : R.drawable.star_0);

        holder.saltLevelTextView.setText("간 정도: " + review.getSaltPreference());
        holder.spicyLevelTextView.setText("맵기 정도: " + review.getSpicyPreference());
        holder.allergyInfoTextView.setText("알레르기: " + review.getAllergies());

        holder.reviewDateTextView.setText("작성한 시간 : " + review.getReviewTime());
        holder.reviewMenuTextView.setText(review.getMainMenu());

        holder.likeCountText.setText(String.valueOf(review.getLikeCount()));
        holder.dislikeCountText.setText(String.valueOf(review.getDislikeCount()));
        holder.likeIcon.setImageResource(review.isLiked() ? R.drawable.ic_like_on : R.drawable.ic_like_off);
        holder.dislikeIcon.setImageResource(review.isDisliked() ? R.drawable.ic_dislike_on : R.drawable.ic_dislike_off);

        // Edit 버튼 클릭 시 EditReviewActivity로 이동
        holder.editbtn.setOnClickListener(v -> {
            String reviewKey = reviewKeys.get(position);
            Intent intent = new Intent(holder.itemView.getContext(), EditReviewActivity.class);
            intent.putExtra("review_id", reviewKey);
            intent.putExtra("student_id", userId);
            intent.putExtra("username", review.getUsername());
            // 여기서 Mainmenu나 Submenu 같은 추가 정보가 필요하다면 intent.putExtra("mainMenu", review.getMainMenu()) 등을 추가할 수 있음.
            holder.itemView.getContext().startActivity(intent);
        });

        holder.deletebtn.setOnClickListener(v->{
            String reviewKey = reviewKeys.get(position);
            databaseReference.child(reviewKey).removeValue();
            reviewsList.remove(position);
            reviewKeys.remove(position);
            notifyItemRemoved(position);
        });

        holder.likeIcon.setOnClickListener(v -> {
            toggleLikeDislike(position, true);
        });

        holder.dislikeIcon.setOnClickListener(v -> {
            toggleLikeDislike(position, false);
        });
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public void updateReviews(List<Review> newReviews) {
        reviewsList = newReviews;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, reviewTextView, saltLevelTextView, spicyLevelTextView, allergyInfoTextView;
        ImageView star1, star2, star3;
        TextView editbtn, reviewDateTextView, reviewMenuTextView, deletebtn;
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
            editbtn = itemView.findViewById(R.id.edit_button);
            deletebtn = itemView.findViewById(R.id.delete_button);
            reviewDateTextView = itemView.findViewById(R.id.review_date);
            reviewMenuTextView = itemView.findViewById(R.id.review_menu);

            likeIcon = itemView.findViewById(R.id.like_icon);
            likeCountText = itemView.findViewById(R.id.like_count_text);
            dislikeIcon = itemView.findViewById(R.id.dislike_icon);
            dislikeCountText = itemView.findViewById(R.id.dislike_count_text);
        }
    }

    private void toggleLikeDislike(int position, boolean isLikeAction) {
        Review review = reviewsList.get(position);
        String reviewKey = reviewKeys.get(position);

        boolean currentlyLiked = review.isLiked();
        boolean currentlyDisliked = review.isDisliked();
        int likeCount = review.getLikeCount();
        int dislikeCount = review.getDislikeCount();

        int oldDifference = likeCount - dislikeCount;

        if (isLikeAction) {
            if (currentlyLiked) {
                review.setLiked(false);
                review.setLikeCount(likeCount - 1);
            } else {
                review.setLiked(true);
                review.setLikeCount(likeCount + 1);
                if (currentlyDisliked) {
                    review.setDisliked(false);
                    review.setDislikeCount(dislikeCount - 1);
                }
            }{
                // 좋아요 아닌 상태
                review.setLiked(true);
                review.setLikeCount(likeCount + 1);
                // 싫어요 상태였다면 해제
                if (currentlyDisliked) {
                    review.setDisliked(false);
                    review.setDislikeCount(dislikeCount - 1);
                }
            }
        } else {
            if (currentlyDisliked) {
                review.setDisliked(false);
                review.setDislikeCount(dislikeCount - 1);
            } else {
                review.setDisliked(true);
                review.setDislikeCount(dislikeCount + 1);
                if (currentlyLiked) {
                    review.setLiked(false);
                    review.setLikeCount(likeCount - 1);
                }
            }
        }

        notifyItemChanged(position);

        int newLikeCount = review.getLikeCount();
        int newDislikeCount = review.getDislikeCount();
        int newDifference = newLikeCount - newDislikeCount;
        int differenceChange = newDifference - oldDifference;

        DatabaseReference reviewRef = databaseReference.child(reviewKey);
        reviewRef.child("liked").setValue(review.isLiked());
        reviewRef.child("likeCount").setValue(newLikeCount);
        reviewRef.child("disliked").setValue(review.isDisliked());
        reviewRef.child("dislikeCount").setValue(newDislikeCount);
        reviewRef.child("likeDifference").setValue(newDifference);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);
        userRef.child("totalLike").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Integer currentTotalLike = task.getResult().getValue(Integer.class);
                if (currentTotalLike == null) currentTotalLike = 0;
                int newTotalLike = currentTotalLike + differenceChange;
                userRef.child("totalLike").setValue(newTotalLike);
            }
        });
    }
}
