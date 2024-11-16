package com.example.ssuchelin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsetsController;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class MainViewActivity extends AppCompatActivity {

    private RecyclerView weekRecyclerView, foodRecyclerView;
    private WeekAdapter weekAdapter;
    private FoodAdapter foodAdapter;
    private List<Date> weekDates = new ArrayList<>();
    private TextView monthYearTextView;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view_activity);  // XML 파일 적용

        // Edge-to-Edge 화면 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //bottomNavigationView.setSelectedItemId(R.id.page_1); // 첫 번째 버튼을 활성화

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.page_1) {
                return true; // 현재 페이지이므로 아무 작업도 하지 않음
            } else if (item.getItemId() == R.id.page_2) {
                Intent intent2 = new Intent(this, ReviewActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent2);
                finish();
                return true;
            } else if (item.getItemId() == R.id.page_3) {
                Intent intent3 = new Intent(this, RankingActivity.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent3);
                finish();
                return true;
            } else if (item.getItemId() == R.id.page_4) {
                Intent intent4 = new Intent(this, ProfileViewActivity.class);
                intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent4);
                finish();
                return true;
            }
            return false;
        });

        monthYearTextView = findViewById(R.id.month_year_text_view);
        weekRecyclerView = findViewById(R.id.week_recycler_view);
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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

        weekAdapter = new WeekAdapter(weekDates, this);
        weekRecyclerView.setAdapter(weekAdapter);

        foodRecyclerView = findViewById(R.id.food_view);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 미리 저장된 음식 데이터 생성
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(new FoodItem("김치찌개", "한국의 전통 김치찌개", R.drawable.star, 3.0f));
        foodItems.add(new FoodItem("비빔밥", "다양한 재료를 섞은 비빔밥", R.drawable.star, 2.3f));

        foodAdapter = new FoodAdapter(foodItems);
        foodRecyclerView.setAdapter(foodAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_1);
    }
}
