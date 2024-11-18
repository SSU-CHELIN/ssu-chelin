package com.example.ssuchelin.menu;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuchelin.R;
import com.example.ssuchelin.menu.food.FoodAdapter;
import com.example.ssuchelin.menu.food.FoodItem;
import com.example.ssuchelin.menu.week.WeekAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExampleActivity extends AppCompatActivity {

    private RecyclerView weekRecyclerView, foodRecyclerView;
    private WeekAdapter weekAdapter;
    private FoodAdapter foodAdapter;
    private List<Date> weekDates = new ArrayList<>();
    private List<FoodItem> foodItems = new ArrayList<>();
    private TextView monthYearTextView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        weekRecyclerView = findViewById(R.id.week_recycler_view);
        foodRecyclerView = findViewById(R.id.food_view);
        monthYearTextView = findViewById(R.id.month_year_text_view);

        // RecyclerView 설정
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 어댑터 설정
        foodAdapter = new FoodAdapter(foodItems);
        foodRecyclerView.setAdapter(foodAdapter);

        // 날짜 리스트 생성
        generateWeekDates();

        // WeekAdapter 설정 및 클릭 리스너
        weekAdapter = new WeekAdapter(weekDates, this);
        weekAdapter.setOnDateClickListener(date -> {
            String selectedDate = dateFormat.format(date);
            fetchMenuForDate(selectedDate);
        });
        weekRecyclerView.setAdapter(weekAdapter);

        // 현재 날짜의 메뉴 가져오기
        String today = dateFormat.format(new Date());
        fetchMenuForDate(today);


        // 월/년 표시 업데이트
        updateMonthYearTextView();
    }

    private void fetchMenuForDate(String date) {
        new com.example.ssuchelin.menu.FetchMenuTask(this, fetchedItems -> {
            if (fetchedItems.isEmpty()) {
                Toast.makeText(ExampleActivity.this, "메뉴가 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                foodItems.clear();
                foodItems.addAll(fetchedItems);
                foodAdapter.notifyDataSetChanged();
                Toast.makeText(ExampleActivity.this, "메뉴 업데이트 완료", Toast.LENGTH_SHORT).show();
            }
        }).execute(date);
    }

    private void generateWeekDates() {
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            weekDates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void updateMonthYearTextView() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        monthYearTextView.setText(String.format("<%d.%02d>", year, month));
    }
}
