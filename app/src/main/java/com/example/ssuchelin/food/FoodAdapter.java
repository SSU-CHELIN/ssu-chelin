package com.example.ssuchelin.food;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import com.example.ssuchelin.R;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private List<FoodItem> foodItems;

    public FoodAdapter(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.foodNameTextView.setText(foodItem.getMainMenu());
        holder.foodSubTextView.setText(foodItem.getSubMenu());
        holder.foodImageView.setImageResource(foodItem.getImageResId()); // 이미지 설정
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView foodImageView;
        public TextView foodNameTextView;
        public TextView foodSubTextView;
        public ImageView star1,star2,star3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImageView = itemView.findViewById(R.id.food_image_view);
            foodNameTextView = itemView.findViewById(R.id.food_main_text_view);
            foodSubTextView = itemView.findViewById(R.id.food_sub_text_view);

            star1 = itemView.findViewById(R.id.star_1);
            star2 = itemView.findViewById(R.id.star_2);
            star3 = itemView.findViewById(R.id.star_3);
        }
    }

    private void setStarRating(ViewHolder holder, float rating) {
        ImageView[] stars = { holder.star1, holder.star2, holder.star3 };
        int fullStars = (int) rating; // 채워진 별 개수
        boolean halfStar = rating - fullStars >= 0.5; // 반 별 여부

        // 채워진 별
        for (int i = 0; i < fullStars; i++) {
            stars[i].setImageResource(R.drawable.star);
        }

        // 반 별이 있으면 다음 별을 반별로 설정
        if (halfStar && fullStars < stars.length) {
            stars[fullStars].setImageResource(R.drawable.star_empty); // 반 별 이미지 추가 가능
        }

        // 나머지는 빈 별로 설정
        for (int i = fullStars + (halfStar ? 1 : 0); i < stars.length; i++) {
            stars[i].setImageResource(R.drawable.star_empty);
        }
    }
}