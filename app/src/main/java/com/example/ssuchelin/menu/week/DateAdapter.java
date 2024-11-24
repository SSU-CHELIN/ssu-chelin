package com.example.ssuchelin.menu.week;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuchelin.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private List<String> dateList;
    private OnDateClickListener onDateClickListener;

    public interface OnDateClickListener {
        void onDateClick(String date);
    }

    public DateAdapter(List<String> dateList, OnDateClickListener listener) {
        this.dateList = dateList;
        this.onDateClickListener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        String date = dateList.get(position);
        holder.dateTextView.setText(date);

        // 클릭 이벤트 처리
        holder.itemView.setOnClickListener(v -> {
            if (onDateClickListener != null) {
                onDateClickListener.onDateClick(date);
            }
        });
    }
    @Override
    public int getItemCount() {
        return dateList != null ? dateList.size() : 0;
    }
    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.day);
        }
    }

}