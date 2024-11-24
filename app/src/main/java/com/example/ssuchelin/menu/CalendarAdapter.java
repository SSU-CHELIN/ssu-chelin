package com.example.ssuchelin.menu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuchelin.R;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private List<String> days;
    private OnDayClickListener listener; // 클릭 리스너 인터페이스
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnDayClickListener {
        void onDayClick(String date);
    }
    public CalendarAdapter(List<String> days,OnDayClickListener listener) {
        this.days = days;
        this.listener=listener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String day = days.get(position);
        holder.dayTextView.setText(days.get(position));


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDayClick(day); // 클릭된 날짜 전달
            }
        });

    }

    public void updateDays(List<String> newDays) {
        this.days = newDays;
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.day);
        }
    }
}