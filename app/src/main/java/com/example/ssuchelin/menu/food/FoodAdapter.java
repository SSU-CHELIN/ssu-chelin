package com.example.ssuchelin.menu.food;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.ssuchelin.R;
import com.example.ssuchelin.review.ReviewActivity;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private List<Food> foodItems;
    private OnItemClickListener listener;

    public FoodAdapter(List<Food> foodItems) {
        this.foodItems = foodItems;
    }


    public interface OnItemClickListener {
        void onItemClick(Food food);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    //    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Food foodItem = foodItems.get(position);
//        holder.foodNameTextView.setText(foodItem.getMainMenu());
//        holder.foodSubTextView.setText(foodItem.getSubMenu());
//        holder.foodImageView.setImageResource(foodItem.getImageResId());// 이미지 설정
//
//        Food food = foodItems.get(position);
//        holder.bind(food);
//
//        holder.itemView.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onItemClick(food);
//            }
//            Intent intent = new Intent(v.getContext(), ReviewActivity.class);
//            intent.putExtra("food_item", (CharSequence) food); // food 정보를 전달
//            v.getContext().startActivity(intent);
//        });
//
//    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodItems.get(position);
        holder.foodImageView.setImageResource(food.getImageResId());
        holder.foodNameTextView.setText(food.getMainMenu());
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView foodImageView;
        public TextView foodNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImageView = itemView.findViewById(R.id.food_image);
            foodNameTextView = itemView.findViewById(R.id.food_text);
        }

        public void bind(Food foodItem) {
            foodNameTextView.setText(foodItem.getMainMenu());
            foodImageView.setImageResource(foodItem.getImageResId());

        }
    }


    public void updateFoodList(List<Food> newFoodList) {
        this.foodItems = newFoodList;
        notifyDataSetChanged(); // RecyclerView 갱신
    }

}