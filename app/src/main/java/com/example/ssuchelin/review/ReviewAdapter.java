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
import com.example.ssuchelin.databinding.ItemReviewBinding;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final List<Review> reviewsList;

    // 어댑터 초기화 시 데이터 리스트를 받음
    public ReviewAdapter(List<Review> reviewsList) {
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용해 리뷰 아이템 레이아웃을 인플레이트
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
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
        holder.star1.setImageResource(review.getScore() >= 1 ? R.drawable.star : R.drawable.star_off);
        holder.star2.setImageResource(review.getScore() >= 2 ? R.drawable.star : R.drawable.star_off);
        holder.star3.setImageResource(review.getScore() >= 3 ? R.drawable.star : R.drawable.star_off);

        // 간 정도, 맵기 정도, 알레르기 정보 설정
        holder.saltLevelTextView.setText("간 정도: " + review.getSaltPreference());
        holder.spicyLevelTextView.setText("맵기 정도: " + review.getSpicyPreference());
        holder.allergyInfoTextView.setText("알레르기: " + review.getAllergies());

        // edit_button 클릭 이벤트
        holder.editbtn.setOnClickListener(v -> {
            // 수정 버튼 클릭 시 필요한 작업 수행
            Toast.makeText(holder.itemView.getContext(), "수정 클릭됨: " + review.getUserReview(), Toast.LENGTH_SHORT).show();

            FragmentActivity activity = (FragmentActivity) holder.itemView.getContext();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();

            WriteReviewFragment writeReviewFragment = new WriteReviewFragment();

            // 프래그로 바꾸면서 수정된 부분
            // 데이터 전달을 위한 번들 생성
            Bundle bundle = new Bundle();
            bundle.putBoolean("editMode", true); // 수정 모드 활성화
            bundle.putString("reviewText", review.getUserReview());
            bundle.putInt("reviewId", position);
            bundle.putFloat("starCount", review.getScore()); // 기존 별점 전달
            writeReviewFragment.setArguments(bundle);

            // 프래그먼트를 교체
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, writeReviewFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return reviewsList.size(); // 리뷰의 개수를 반환
    }

    // RecyclerView 데이터를 업데이트하는 메서드
    public void updateReviews(List<Review> newReviews) {
        reviewsList.clear(); // 기존 데이터 삭제
        reviewsList.addAll(newReviews); // 새로운 데이터 추가
        notifyDataSetChanged(); // RecyclerView에 변경 사항 알림
    }

    // Review 객체를 담는 뷰홀더 클래스
    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView, reviewTextView, saltLevelTextView, spicyLevelTextView, allergyInfoTextView;
        ImageView star1, star2, star3;
        TextView editbtn;

        ItemReviewBinding binding;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.review_username); // 사용자 이름
            reviewTextView = itemView.findViewById(R.id.review_content); // 리뷰 텍스트
            star1 = itemView.findViewById(R.id.review_star1); // 첫 번째 별 이미지
            star2 = itemView.findViewById(R.id.review_star2); // 두 번째 별 이미지
            star3 = itemView.findViewById(R.id.review_star3); // 세 번째 별 이미지
            editbtn = itemView.findViewById(R.id.edit_button); // 수정 버튼
            saltLevelTextView = itemView.findViewById(R.id.salt_level); // 간 정도
            spicyLevelTextView = itemView.findViewById(R.id.spicy_level); // 맵기 정도
            allergyInfoTextView = itemView.findViewById(R.id.allergy_info); // 알레르기 정보
        }
        public void bind(Review review) {
            binding.reviewUsername.setText(review.getUsername());
            binding.reviewContent.setText(review.getUserReview());
            binding.reviewStar1.setImageResource(review.getScore() >= 1 ? R.drawable.star : R.drawable.star_off);
            binding.reviewStar2.setImageResource(review.getScore() >= 2 ? R.drawable.star : R.drawable.star_off);
            binding.reviewStar3.setImageResource(review.getScore() >= 3 ? R.drawable.star : R.drawable.star_off);
        }

    }
}
