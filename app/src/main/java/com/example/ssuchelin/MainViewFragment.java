package com.example.ssuchelin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsetsController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuchelin.food.FoodAdapter;
import com.example.ssuchelin.food.FoodItem;
import com.example.ssuchelin.week.WeekAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainViewFragment extends Fragment {

    private RecyclerView weekRecyclerView, foodRecyclerView;
    private WeekAdapter weekAdapter;
    private FoodAdapter foodAdapter;
    private List<Date> weekDates = new ArrayList<>();
    private TextView monthYearTextView;

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_view, container, false); // 프래그먼트 레이아웃 설정

        // Edge-to-Edge 화면 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController insetsController = requireActivity().getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }

        monthYearTextView = view.findViewById(R.id.month_year_text_view);
        weekRecyclerView = view.findViewById(R.id.week_recycler_view);
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        monthYearTextView.setText(String.format("<%d.%02d>", year, month));

        // 현재 날짜부터 주간 날짜 리스트 생성
        Calendar weekCalendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            weekDates.add(weekCalendar.getTime());
            weekCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        weekAdapter = new WeekAdapter(weekDates, getContext());
        weekRecyclerView.setAdapter(weekAdapter);

        foodRecyclerView = view.findViewById(R.id.food_view);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 미리 저장된 음식 데이터 생성
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(new FoodItem("김치찌개", "한국의 전통 김치찌개", R.drawable.star, 3.0f));
        foodItems.add(new FoodItem("비빔밥", "다양한 재료를 섞은 비빔밥", R.drawable.star, 2.3f));

        foodAdapter = new FoodAdapter(foodItems);
        foodRecyclerView.setAdapter(foodAdapter);

        return view;
    }


}
