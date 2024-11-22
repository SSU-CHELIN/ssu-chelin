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

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.ssuchelin.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MenuFragment extends Fragment {
    private Calendar calendar;
    ImageButton prev,next;
    EditText monthSelectButton;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater,@Nullable ViewGroup container,@Nullable
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize calendar to current date
        calendar = Calendar.getInstance();

        // Find views
        ImageButton prevButton = view.findViewById(R.id.prev_button);
        ImageButton nextButton = view.findViewById(R.id.next_button);
        monthSelectButton = view.findViewById(R.id.month_select_button);

        // Set initial month
        updateMonthDisplay();

        // Set click listeners
        prevButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1); // Decrement month
            updateMonthDisplay();
        });

        nextButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1); // Increment month
            updateMonthDisplay();
        });
    }
    private void updateMonthDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String monthYear = dateFormat.format(calendar.getTime());
        monthSelectButton.setText(monthYear); // Update EditText with new month
    }
}

