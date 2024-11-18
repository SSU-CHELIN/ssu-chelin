package com.example.ssuchelin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// ReviewAdapter는 RecyclerView를 위한 어댑터로서, Review 객체들을 화면에 표시하는 역할을 합니다.
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    // reviews: Review 객체들을 저장하는 리스트
    private ArrayList<Review> reviews;

    // 생성자: 어댑터가 초기화될 때 Review 객체 리스트를 받아서 설정합니다.
    public ReviewAdapter(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    // ViewHolder가 처음 생성될 때 호출됩니다.
    // View를 생성하고 ViewHolder 객체를 반환하여 각 항목에 대한 레이아웃을 초기화합니다.
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // overview_review_item 레이아웃을 사용하여 뷰를 만듭니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.overview_review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    // ViewHolder에 데이터를 바인딩하는 메서드
    // 각 아이템이 화면에 표시될 때 데이터를 설정합니다.
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        // 현재 위치의 Review 객체를 가져옵니다.
        Review review = reviews.get(position);

        // 사용자 이름과 리뷰 내용을 TextView에 설정합니다.
        holder.username.setText(review.getUsername());
        holder.reviewContent.setText(review.getContent());

        // Firebase에서 데이터를 가져와서 Review 객체에 저장한 후, 여기에서 데이터를 설정합니다.
        // 예를 들어, holder.username.setText(review.getUsername());는 Firebase로부터 가져온 사용자 이름을 표시합니다.
    }

    // 아이템의 총 개수를 반환하는 메서드
    @Override
    public int getItemCount() {
        return reviews.size();
    }

    // ReviewViewHolder는 RecyclerView의 ViewHolder로서,
    // 각 리뷰 항목에 대한 뷰 요소들을 관리합니다.
    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        // username: 작성자 이름을 표시하는 TextView
        TextView username;
        // reviewContent: 리뷰 내용을 표시하는 TextView
        TextView reviewContent;

        // ViewHolder 생성자
        // itemView에서 각 TextView를 초기화합니다.
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            // R.id.username은 overview_review_item 레이아웃의 사용자 이름을 위한 TextView입니다.
            username = itemView.findViewById(R.id.username);
            // R.id.review_content은 리뷰 내용을 표시하기 위한 TextView입니다.
            reviewContent = itemView.findViewById(R.id.review_content);
        }
    }
}
