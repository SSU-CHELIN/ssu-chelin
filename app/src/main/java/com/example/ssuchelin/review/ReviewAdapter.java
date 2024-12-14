package com.example.ssuchelin.review;

import android.content.Intent; /// 수정 부분: Intent 사용을 위해 import
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewsList;
    private List<String> reviewKeys;
    private String userId;
    private DatabaseReference databaseReference;

    public ReviewAdapter(List<Review> reviewsList, List<String> reviewKeys, String userId) {
        this.reviewsList = reviewsList;
        this.reviewKeys = reviewKeys;
        this.userId = userId;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userId).child("myReviewData");
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_review.xml 사용
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.overview_review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewsList.get(position);
        holder.usernameTextView.setText(review.getUsername());
        holder.reviewTextView.setText(review.getUserReview());

        // 별점 설정
        holder.star1.setImageResource(review.getStarCount() >= 1 ? R.drawable.star_100 : R.drawable.star_0);
        holder.star2.setImageResource(review.getStarCount() >= 2 ? R.drawable.star_100 : R.drawable.star_0);
        holder.star3.setImageResource(review.getStarCount() >= 3 ? R.drawable.star_100 : R.drawable.star_0);

        holder.saltLevelTextView.setText("간 정도: " + review.getSaltPreference());
        holder.spicyLevelTextView.setText("맵기 정도: " + review.getSpicyPreference());
        holder.allergyInfoTextView.setText("알레르기: " + review.getAllergies());
        holder.reviewDateTextView.setText("작성한 시간 : " + review.getReviewTime());
        holder.reviewMenuTextView.setText(review.getMainMenu());

        // 좋아요/싫어요 상태 표시
        holder.likeCountText.setText(String.valueOf(review.getLikeCount()));
        holder.dislikeCountText.setText(String.valueOf(review.getDislikeCount()));
        holder.likeIcon.setImageResource(review.isLiked() ? R.drawable.ic_like_on : R.drawable.ic_like_off);
        holder.dislikeIcon.setImageResource(review.isDisliked() ? R.drawable.ic_dislike_on : R.drawable.ic_dislike_off);

//        // /// 수정 부분: 수정 버튼 클릭 시 EditReviewActivity로 이동
//        holder.editbtn.setOnClickListener(v -> {
//            String reviewKey = reviewKeys.get(position);
//
//            Intent intent = new Intent(holder.itemView.getContext(), EditReviewActivity.class);
//            intent.putExtra("review_id", reviewKey); // 리뷰 ID 전달
//            intent.putExtra("student_id", userId); // 사용자 ID 전달
//            intent.putExtra("username", review.getUsername()); // 사용자 이름 전달
//            holder.itemView.getContext().startActivity(intent);
//        });
//        holder.deletebtn.setOnClickListener(v->{
//            String reviewKey = reviewKeys.get(position);
//            databaseReference.child(reviewKey).removeValue();
//            reviewsList.remove(position);
//            notifyItemRemoved(position);
//        });

        // 좋아요 버튼 클릭
        holder.likeIcon.setOnClickListener(v -> {
            toggleLikeDislike(position, true);
        });

        // 싫어요 버튼 클릭
        holder.dislikeIcon.setOnClickListener(v -> {
            toggleLikeDislike(position, false);
        });
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public void updateReviews(List<Review> newReviews, List<String> newReviewKeys) {
        if (newReviews == null || newReviewKeys == null || newReviews.size() != newReviewKeys.size()) {
            Log.e("ReviewAdapter", "Data mismatch: reviews and keys sizes do not match");
            return;
        }

        this.reviewsList = newReviews;
        this.reviewKeys = newReviewKeys;
        notifyDataSetChanged();
    }


    class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, reviewTextView, saltLevelTextView, spicyLevelTextView, allergyInfoTextView;
        ImageView star1, star2, star3;
        TextView reviewDateTextView, reviewMenuTextView;
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
//            editbtn = itemView.findViewById(R.id.edit_button);
//            deletebtn = itemView.findViewById(R.id.delete_button);
            reviewDateTextView = itemView.findViewById(R.id.review_date);
            reviewMenuTextView = itemView.findViewById(R.id.review_menu);

            likeIcon = itemView.findViewById(R.id.like_icon);
            likeCountText = itemView.findViewById(R.id.like_count_text);
            dislikeIcon = itemView.findViewById(R.id.dislike_icon);
            dislikeCountText = itemView.findViewById(R.id.dislike_count_text);
        }
    }

    private void toggleLikeDislike(int position, boolean isLikeAction) {
        Log.d("ReviewAdapter", "Position: " + position);
        Log.d("ReviewAdapter", "reviewsList size: " + (reviewsList != null ? reviewsList.size() : "null"));
        Log.d("ReviewAdapter", "reviewKeys size: " + (reviewKeys != null ? reviewKeys.size() : "null"));

        // 리뷰 키 및 데이터 확인
        if (reviewKeys == null || position >= reviewKeys.size() || reviewsList == null || position >= reviewsList.size()) {
            Log.e("ReviewAdapter", "Invalid position or null data");
            return;
        }

        String reviewKey = reviewKeys.get(position);
        Review review = reviewsList.get(position);

        if (reviewKey == null || review == null) {
            Log.e("ReviewAdapter", "reviewKey or review is null");
            return;
        }

        // 기존 상태 확인
        boolean currentlyLiked = review.isLiked();
        boolean currentlyDisliked = review.isDisliked();
        int likeCount = review.getLikeCount();
        int dislikeCount = review.getDislikeCount();

        // 상태 변경
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

        // 데이터 업데이트
        int newLikeCount = review.getLikeCount();
        int newDislikeCount = review.getDislikeCount();
        int newDifference = newLikeCount - newDislikeCount;

        reviewsList.set(position, review); // 리스트 업데이트
        notifyItemChanged(position);      // UI 업데이트

        // Firebase 업데이트
        DatabaseReference reviewRef = databaseReference.child(reviewKey);
        reviewRef.child("liked").setValue(review.isLiked());
        reviewRef.child("likeCount").setValue(newLikeCount);
        reviewRef.child("disliked").setValue(review.isDisliked());
        reviewRef.child("dislikeCount").setValue(newDislikeCount);
        reviewRef.child("likeDifference").setValue(newDifference);

        // 사용자 총 좋아요 수 업데이트
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);
        userRef.child("totalLike").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer currentTotalLike = snapshot.getValue(Integer.class);
                if (currentTotalLike == null) currentTotalLike = 0;
                userRef.child("totalLike").setValue(currentTotalLike + (newDifference - oldDifference));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReviewAdapter", "Failed to update totalLike: " + error.getMessage());
            }
        });
    }

}
