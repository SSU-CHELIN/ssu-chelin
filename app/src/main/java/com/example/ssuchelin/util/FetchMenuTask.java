package com.example.ssuchelin.menu;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.ssuchelin.R;
import com.example.ssuchelin.menu.food.FoodItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class  FetchMenuTask extends AsyncTask<String, Void, List<FoodItem>> {

    private final Context context;
    private final OnMenuFetchedListener listener;

    public interface OnMenuFetchedListener {
        void onMenuFetched(List<FoodItem> foodItems);
    }

    public FetchMenuTask(Context context, OnMenuFetchedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<FoodItem> doInBackground(String... params) {
        String date = params[0];
        String menuUrl = "https://soongguri.com/main.php?mkey=2&w=3&l=1";
        List<FoodItem> items = new ArrayList<>();

        try {
            Document document = Jsoup.connect(menuUrl)
                    .data("sdt", date)
                    .data("jun", "-1")
                    .post();

            // td 요소 가져오기
            Elements menuElements = document.select("td[style*=text-align:left]");
            if (menuElements.size() >= 4) { // 최소 4개의 td가 있는지 확인
                String secondMenu = menuElements.get(1).text(); // 두 번째 td
                String thirdMenu = menuElements.get(2).text(); // 세 번째 td
                String fourthMenu = menuElements.get(3).text(); // 네 번째 td

                // 가져온 데이터를 FoodItem으로 추가
                items.add(new FoodItem(secondMenu, "Description for " + secondMenu, R.drawable.star, 3.0f));
                items.add(new FoodItem(thirdMenu, "Description for " + thirdMenu, R.drawable.star, 4.0f));
                items.add(new FoodItem(fourthMenu, "Description for " + fourthMenu, R.drawable.star, 2.5f));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    protected void onPostExecute(List<FoodItem> result) {
        if (result.isEmpty()) {
            Toast.makeText(context, "메뉴 데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
        listener.onMenuFetched(result);
    }
}