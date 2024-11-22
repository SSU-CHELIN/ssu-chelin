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

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {
    private List<Date> dates; // 날짜 리스트
    private Context context;
    private int selectedPosition = -1; // 선택된 날짜의 포지션
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
    private OnDateClickListener onDateClickListener;


    public WeekAdapter(List<Date> dates, Context context) {
        this.dates = dates;
        this.context = context;
    }


    public void setOnDateClickListener(OnDateClickListener listener) {
        this.onDateClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // RecyclerView의 각 아이템 레이아웃을 인플레이트
        View view = LayoutInflater.from(context).inflate(R.layout.item_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Date date = dates.get(position);
        holder.dateTextView.setText(dateFormat.format(date));

        // 요일 계산
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String[] daysOfWeek = {"일", "월", "화", "수", "목", "금", "토"};
        int dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 요일 인덱스 (0: 일요일, 1: 월요일 ...)
        holder.dayOfWeekTextView.setText(daysOfWeek[dayOfWeekIndex]);

        // 오늘 날짜 설정
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        // 선택 상태에 따른 색상 설정
        if (selectedPosition == position) {
            holder.dateTextView.setBackgroundColor(Color.LTGRAY); // 선택된 배경색
            holder.dateTextView.setTextColor(Color.BLACK); // 선택된 글씨 색상
        } else {
            holder.dateTextView.setBackgroundColor(Color.TRANSPARENT); // 기본 배경색
            holder.dateTextView.setTextColor(Color.GRAY); // 기본 글씨 색상
        }

        // 클릭 이벤트 설정
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position; // 선택된 날짜 업데이트
            notifyDataSetChanged(); // RecyclerView 갱신

            // 클릭 리스너 호출
            if (onDateClickListener != null) {
                onDateClickListener.onDateClick(date);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size(); // 데이터 리스트의 크기 반환
    }

    // 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView dateTextView;
        public final TextView dayOfWeekTextView; // 요일 텍스트뷰

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            dayOfWeekTextView = itemView.findViewById(R.id.day_of_week_text_view);
        }
    }


    public interface OnDateClickListener {
        void onDateClick(Date date);
    }

}