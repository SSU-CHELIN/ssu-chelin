//package com.example.ssuchelin.menu;
//
//import android.annotation.SuppressLint;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowInsetsController;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.ssuchelin.R;
//import com.example.ssuchelin.menu.food.FoodAdapter;
//import com.example.ssuchelin.menu.food.FoodItem;
//import com.example.ssuchelin.menu.week.WeekAdapter;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//public class MenuFragment extends Fragment {
//
//    private RecyclerView weekRecyclerView, foodRecyclerView;
//    private WeekAdapter weekAdapter;
//    private FoodAdapter foodAdapter;
//    private List<Date> weekDates = new ArrayList<>();
//    private List<FoodItem> foodItems = new ArrayList<>();
//    private TextView monthYearTextView;
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//
//    @SuppressLint("DefaultLocale")
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_menu, container, false); // 프래그먼트 레이아웃 설정
//
//
//        monthYearTextView = view.findViewById(R.id.month_year_text_view);
//        weekRecyclerView = view.findViewById(R.id.week_recycler_view);
//        weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//
//
//
//        weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        foodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//
//        // 현재 날짜 가져오기
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        monthYearTextView.setText(String.format("<%d.%02d>", year, month));
//
//        // 현재 날짜부터 주간 날짜 리스트 생성
//        Calendar weekCalendar = Calendar.getInstance();
//        for (int i = 0; i < 7; i++) {
//            weekDates.add(weekCalendar.getTime());
//            weekCalendar.add(Calendar.DAY_OF_YEAR, 1);
//        }
//
//        // WeekAdapter 설정 및 날짜 클릭 리스너 추가
//        weekAdapter = new WeekAdapter(weekDates, getContext());
//        weekAdapter.setOnDateClickListener(date -> {
//            String selectedDate = dateFormat.format(date);
//            fetchMenuForDate(selectedDate);
//        });
//        weekRecyclerView.setAdapter(weekAdapter);
//
//        // FoodAdapter 초기 설정
//        foodAdapter = new FoodAdapter(foodItems);
//        foodRecyclerView.setAdapter(foodAdapter);
//
//        return view;
//    }
//    private void fetchMenuForDate(String date) {
//        new FetchMenuTask().execute(date);
//    }
//
//    private class FetchMenuTask extends AsyncTask<String, Void, List<FoodItem>> {
//
//        @Override
//        protected List<FoodItem> doInBackground(String... params) {
//            String date = params[0];
//            String menuUrl = "https://soongguri.com/main.php?mkey=2&w=3&l=1";
//            List<FoodItem> items = new ArrayList<>();
//
//            try {
//                Document document = Jsoup.connect(menuUrl)
//                        .data("sdt", date)
//                        .data("jun", "-1")
//                        .post();
//
//                // td 요소 가져오기
//                Elements menuElements = document.select("td[style*=text-align:left]");
//                if (menuElements.size() >= 4) { // 최소 4개의 td가 있는지 확인
//                    String secondMenu = menuElements.get(1).text(); // 두 번째 td
//                    String thirdMenu = menuElements.get(2).text(); // 세 번째 td
//                    String fourthMenu = menuElements.get(3).text(); // 네 번째 td
//
//                    // 가져온 데이터를 FoodItem으로 추가
//                    items.add(new FoodItem(secondMenu, "Description for " + secondMenu, R.drawable.star, 3.0f));
//                    items.add(new FoodItem(thirdMenu, "Description for " + thirdMenu, R.drawable.star, 4.0f));
//                    items.add(new FoodItem(fourthMenu, "Description for " + fourthMenu, R.drawable.star, 2.5f));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return items;
//        }
//
//        @Override
//        protected void onPostExecute(List<FoodItem> result) {
//            if (result.isEmpty()) {
//                Toast.makeText(getContext(), "메뉴 데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
//            } else {
//                // FoodAdapter 데이터 업데이트 및 새로고침
//                foodItems.clear();
//                foodItems.addAll(result);
//                foodAdapter.notifyDataSetChanged();
//                Toast.makeText(getContext(), "메뉴가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//
//
//
//}
package com.example.ssuchelin.menu;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ssuchelin.R;
import com.example.ssuchelin.menu.food.FoodAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MenuFragment extends Fragment {
    private CalendarView calendarView;
    private ImageView imageView1, imageView2, imageView3;
    private TextView menuText1, menuText2, menuText3;

    //    @Override
//    protected void onCreateView(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        calendarView = findViewById(R.id.calendarView);

    //        Button buttonFetchMenu = findViewById(R.id.buttonFetchMenu);
//
//        // 기본 날짜 설정
//
//        // 날짜 변경 시 업데이트
//        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
//            month += 1;
//            selectedDate = String.format("%04d%02d%02d", year, month, dayOfMonth);
//        });
//
//        // 버튼 클릭 시 메뉴와 이미지 로드
//        buttonFetchMenu.setOnClickListener(v -> new FetchMenuTask().execute(selectedDate));
//    }
//
//    /**
//     * 주차(jun) 계산 함수
//     * @param date 선택한 날짜
//     * @return jun 값 (현재 주: 0, 다음 주: +1, 이전 주: -1)
//     */
//    private int calculateJunValue(String date) {
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
//            Date selectedDate = sdf.parse(date);
//
//            Calendar selectedCal = Calendar.getInstance();
//            selectedCal.setTime(selectedDate);
//
//            Calendar currentCal = Calendar.getInstance();
//
//            // 현재 주차와 선택 주차를 계산
//            int currentWeek = currentCal.get(Calendar.WEEK_OF_YEAR);
//            int selectedWeek = selectedCal.get(Calendar.WEEK_OF_YEAR);
//
//            return selectedWeek - currentWeek;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0; // 기본값으로 현재 주
//        }
//    }
//
//    private class FetchMenuTask extends AsyncTask<String, Void, List<String>> {
//
//        @Override
//        protected List<String> doInBackground(String... params) {
//            String date = params[0];
//            int junValue = calculateJunValue(date); // 동적으로 jun 값을 계산
//            String menuUrl = "https://soongguri.com/main.php?mkey=2&w=3&l=1";
//
//            List<String> menuTexts = new ArrayList<>();
//
//            try {
//                // 메뉴 데이터 크롤링
//                Document document = Jsoup.connect(menuUrl)
//                        .data("sdt", date)
//                        .data("jun", String.valueOf(junValue)) // jun 값 추가
//                        .post();
//
//                Elements menuElements = document.select("td[style*=text-align:left]");
//                for (int i = 1; i <= 3; i++) { // 두 번째부터 네 번째 td 추출
//                    if (menuElements.size() > i) {
//                        menuTexts.add(menuElements.get(i).text());
//                    } else {
//                        menuTexts.add("No data");
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                menuTexts.add("Error fetching menu: " + e.getMessage());
//            }
//            return menuTexts;
//        }
//
//        @Override
//        protected void onPostExecute(List<String> menus) {
//            // 메뉴 데이터 설정
//            menuText1.setText(menus.get(0));
//            menuText2.setText(menus.get(1));
//            menuText3.setText(menus.get(2));
//
//            // 이미지 URL 생성 및 로드
//            String baseUrl = "https://soongguri.com/menu/menu_file/";
//            Glide.with(MainActivity.this).load(baseUrl + selectedDate + "_1_2.jpg").into(imageView1);
//            Glide.with(MainActivity.this).load(baseUrl + selectedDate + "_1_3.jpg").into(imageView2);
//            Glide.with(MainActivity.this).load(baseUrl + selectedDate + "_1_4.jpg").into(imageView3);
//
//            Toast.makeText(MainActivity.this, "Menu and Images Loaded", Toast.LENGTH_SHORT).show();
//        }
//    }
    private Calendar currentCalendar;
    private CalendarAdapter calendarAdapter;
    private TextView monthYearText;
    private String selectedDate;


    private RecyclerView calendarRecyclerView;
    private RecyclerView foodRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearText);
        ImageButton nextButton = view.findViewById(R.id.nextButton);
        ImageButton prevButton = view.findViewById(R.id.prevButton);
        imageView1 = view.findViewById(R.id.imageView1);
        imageView2 = view.findViewById(R.id.imageView2);
        imageView3 = view.findViewById(R.id.imageView3);
        menuText1 = view.findViewById(R.id.menuText1);
        menuText2 = view.findViewById(R.id.menuText2);
        menuText3 = view.findViewById(R.id.menuText3);


        currentCalendar = Calendar.getInstance();
        selectedDate = getFormattedDate(currentCalendar); // "YYYYMMDD" 형식
        // RecyclerView 초기화
        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        calendarAdapter = new CalendarAdapter(getDaysOfMonth(currentCalendar), clickedDate -> {
            selectedDate = clickedDate;
            new FetchMenuTask().execute((selectedDate));
        });
        calendarRecyclerView.setAdapter(calendarAdapter);

        new FetchMenuTask().execute((selectedDate));

        updateMonthYearText();

        // Next 버튼
        nextButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar(calendarRecyclerView);
        });

        // Prev 버튼
        prevButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar(calendarRecyclerView);
        });

        return view;
    }

    private void updateCalendar(RecyclerView recyclerView) {
        calendarAdapter = new CalendarAdapter(getDaysOfMonth(currentCalendar), selectedDate -> {
            // 날짜 클릭 이벤트 처리
            Toast.makeText(getContext(), "Clicked: " + selectedDate, Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(calendarAdapter);
        updateMonthYearText();
    }

    // RecyclerView에 해당 월의 날짜 갱신
    private void updateCalendar() {
        calendarAdapter.updateDays(getDaysOfMonth(currentCalendar));
        calendarAdapter.notifyDataSetChanged();
        monthYearText.setText(getMonthYearFromCalendar(currentCalendar));
    }

    // 해당 월의 날짜 리스트 생성
    private List<String> getDaysOfMonth(Calendar calendar) {
        List<String> days = new ArrayList<>();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            days.add(String.format("%04d%02d%02d", year, month + 1, day));
        }

        return days;
    }

    // 월과 연도를 "MMMM YYYY" 형식으로 반환
    private String getMonthYearFromCalendar(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return formatter.format(calendar.getTime());
    }

    private void updateMonthYearText() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM", Locale.getDefault());
        monthYearText.setText(sdf.format(currentCalendar.getTime()));
    }



    private class FetchMenuTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            String date = params[0];
            int junValue = calculateJunValue(date); // 동적으로 jun 값을 계산
            String menuUrl = "https://soongguri" +
                    ".com/main.php?mkey=2&w=3&l=1";

            List<String> menuTexts = new ArrayList<>();

            try {
                // 메뉴 데이터 크롤링
                Document document = Jsoup.connect(menuUrl)
                        .data("sdt", date)
                        .data("jun", String.valueOf(junValue)) // jun 값 추가
                        .post();

                Elements menuElements = document.select("td[style*=text-align:left]");
                for (int i = 1; i <= 3; i++) { // 두 번째부터 네 번째 td 추출
                    if (menuElements.size() > i) {
                        menuTexts.add(menuElements.get(i).text());
                    } else {
                        menuTexts.add("No data");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                menuTexts.add("Error fetching menu: " + e.getMessage());
            }
            return menuTexts;
        }

        @Override
        protected void onPostExecute(List<String> menus) {
            // 메뉴 데이터 설정
            menuText1.setText(menus.get(0));
            menuText2.setText(menus.get(1));
            menuText3.setText(menus.get(2));

            // 이미지 URL 생성 및 로드
            String baseUrl = "https://soongguri.com/menu/menu_file/";
            Glide.with(MenuFragment.this).load(baseUrl + selectedDate + "_1_2.jpg").into(imageView1);
            Glide.with(MenuFragment.this).load(baseUrl + selectedDate + "_1_3.jpg").into(imageView2);
            Glide.with(MenuFragment.this).load(baseUrl + selectedDate + "_1_4.jpg").into(imageView3);

            Toast.makeText(requireContext(), "Menu and Images Loaded", Toast.LENGTH_SHORT).show();
        }
    }
    private String getFormattedDate(Calendar calendar) {
        return String.format("%04d%02d%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, // Calendar.MONTH는 0부터 시작
                calendar.get(Calendar.DAY_OF_MONTH));
    }


    /**
     * 주차(jun) 계산 함수
     *
     * @param date 선택한 날짜
     * @return jun 값 (현재 주: 0, 다음 주: +1, 이전 주: -1)
     */
    private int calculateJunValue(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Date selectedDate = sdf.parse(date);

            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);

            Calendar currentCal = Calendar.getInstance();

            // 현재 주차와 선택 주차를 계산
            int currentWeek = currentCal.get(Calendar.WEEK_OF_YEAR);
            int selectedWeek = selectedCal.get(Calendar.WEEK_OF_YEAR);

            return selectedWeek - currentWeek;
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // 기본값으로 현재 주
        }
    }


}
