package com.example.ssuchelin.menu;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ssuchelin.R;
import com.example.ssuchelin.databinding.FragmentMenuBinding;
import com.example.ssuchelin.review.ReviewFragment;
import com.example.ssuchelin.review.WriteReviewFragment;

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
    private ImageView imageView1, imageView2, imageView3;
    private TextView menuText1, menuText2, menuText3,subMenuText1,subMenuText2,subMenuText3;
    private TextView categoryText1,categoryText2,categoryText3;
    private Calendar currentCalendar;
    private CalendarAdapter calendarAdapter;
    private TextView monthYearText;
    private LinearLayout food1,food2,food3;
    private String selectedDate;
    private String allergyInfo1,allergyInfo2,allergyInfo3;
    private Button allergyButton1,allergyButton2,allergyButton3;
    private RecyclerView calendarRecyclerView;
    FragmentMenuBinding binding;
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        binding = FragmentMenuBinding.inflate(inflater, container, false);


        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearText);
        ImageButton nextButton = view.findViewById(R.id.nextButton);
        ImageButton prevButton = view.findViewById(R.id.prevButton);
        food1 = view.findViewById(R.id.food1);
        food2 = view.findViewById(R.id.food2);
        food3 = view.findViewById(R.id.food3);
        imageView1 = view.findViewById(R.id.foodImage1);
        imageView2 = view.findViewById(R.id.foodImage2);
        imageView3 = view.findViewById(R.id.foodImage3);
        menuText1 = view.findViewById(R.id.foodMainMenu1);
        menuText2 = view.findViewById(R.id.foodMainMenu2);
        menuText3 = view.findViewById(R.id.foodMainMenu3);
        subMenuText1=view.findViewById(R.id.foodSubMenu1);
        subMenuText2=view.findViewById(R.id.foodSubMenu2);
        subMenuText3=view.findViewById(R.id.foodSubMenu3);
        categoryText1=view.findViewById(R.id.foodCategory1);
        categoryText2=view.findViewById(R.id.foodCategory2);
        categoryText3=view.findViewById(R.id.foodCategory3);
        allergyButton1=view.findViewById(R.id.foodAllergy1);
        allergyButton2=view.findViewById(R.id.foodAllergy2);
        allergyButton3=view.findViewById(R.id.foodAllergy3);


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




        food1.setOnClickListener(v->{

            String mainMenu = menuText1.getText().toString();
            String subMenu = subMenuText1.getText().toString();
            String category = categoryText1.getText().toString();



            Bitmap bitmap = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();



            // 데이터를 Bundle에 저장
            Bundle bundle = new Bundle();
            bundle.putString("mainMenu", mainMenu);
            bundle.putString("subMenu", subMenu);
            bundle.putString("category", category);
            bundle.putString("type","ddook");
            bundle.putParcelable("imageBitmap", bitmap);  // 이미지 데이터를 Bitmap 형태로 전달


            // WriteReviewFragment로 데이터를 전달하면서 이동
            ReviewFragment reviewFragment = new ReviewFragment();
            reviewFragment.setArguments(bundle);

            // FragmentTransaction을 사용하여 Fragment 이동
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, reviewFragment)
                    .addToBackStack(null) // 뒤로 가기 스택에 추가
                    .commit();

        });

        allergyButton1.setOnClickListener(v->{

            Bundle bundle = new Bundle();

            bundle.putString("Allergy",allergyInfo1);

            AllergyFragment allergyFragment = new AllergyFragment();
            allergyFragment.setArguments(bundle);


            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, allergyFragment)
                    .addToBackStack(null)
                    .commit();

        });

        food2.setOnClickListener(v->{

            String mainMenu = menuText2.getText().toString();
            String subMenu = subMenuText2.getText().toString();
            String category = categoryText2.getText().toString();



            Bitmap bitmap = ((BitmapDrawable) imageView2.getDrawable()).getBitmap();



            // 데이터를 Bundle에 저장
            Bundle bundle = new Bundle();
            bundle.putString("mainMenu", mainMenu);
            bundle.putString("subMenu", subMenu);
            bundle.putString("category", category);
            bundle.putString("type","dub");
            bundle.putParcelable("imageBitmap", bitmap);  // 이미지 데이터를 Bitmap 형태로 전달


            // WriteReviewFragment로 데이터를 전달하면서 이동
            ReviewFragment reviewFragment = new ReviewFragment();
            reviewFragment.setArguments(bundle);

            // FragmentTransaction을 사용하여 Fragment 이동
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, reviewFragment)
                    .addToBackStack(null) // 뒤로 가기 스택에 추가
                    .commit();

        });

        allergyButton2.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("Allergy",allergyInfo2);
            AllergyFragment allergyFragment = new AllergyFragment();
            allergyFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, allergyFragment)
                    .addToBackStack(null)
                    .commit();
        });

        food3.setOnClickListener(v->{

            String mainMenu = menuText3.getText().toString();
            String subMenu = subMenuText3.getText().toString();
            String category = categoryText3.getText().toString();



            Bitmap bitmap = ((BitmapDrawable) imageView3.getDrawable()).getBitmap();


            // 파싱 결과를 Toast로 확인
            Toast.makeText(getContext(), "Main Menu: " + mainMenu, Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Sub Menu: " + subMenu, Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Category: " + category, Toast.LENGTH_SHORT).show();


            // 데이터를 Bundle에 저장
            Bundle bundle = new Bundle();
            bundle.putString("mainMenu", mainMenu);
            bundle.putString("subMenu", subMenu);
            bundle.putString("category", category);
            bundle.putString("type","yang");
            bundle.putParcelable("imageBitmap", bitmap);  // 이미지 데이터를 Bitmap 형태로 전달


            // ReviewFragment로 데이터를 전달하면서 이동
            ReviewFragment reviewFragment = new ReviewFragment();
            reviewFragment.setArguments(bundle);

            // FragmentTransaction을 사용하여 Fragment 이동
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, reviewFragment)
                    .addToBackStack(null) // 뒤로 가기 스택에 추가
                    .commit();

        });


        allergyButton3.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("Allergy",allergyInfo3);
            AllergyFragment allergyFragment = new AllergyFragment();
            allergyFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, allergyFragment)
                    .addToBackStack(null)
                    .commit();
        });




        return view;
    }


    // RecyclerView에 해당 월의 날짜 갱신
    private void updateCalendar(RecyclerView recyclerView) {
        calendarAdapter.updateDays(getDaysOfMonth(currentCalendar));
        calendarAdapter.notifyDataSetChanged();
        monthYearText.setText(getMonthYearFromCalendar(currentCalendar));
        recyclerView.setAdapter(calendarAdapter);
        updateMonthYearText();
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
            if (menus.isEmpty()) {
                Toast.makeText(getContext(), "메뉴 데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
            } else {
                // 카테고리와 메인 메뉴 설정
                categoryText1.setText(parseCategory(menus.get(0)));
                menuText1.setText(parseMainMenu(menus.get(0)));
                subMenuText1.setText(String.join(", ", parseSubMenu(menus.get(0)))); // 한글 이름만 서브 메뉴에 설정
                allergyInfo1 = parseAllergyInfo(menus.get(0));


                categoryText2.setText(parseCategory(menus.get(1)));
                menuText2.setText(parseMainMenu(menus.get(1)));
                subMenuText2.setText(String.join(", ", parseSubMenu(menus.get(1))));
                allergyInfo2 = parseAllergyInfo(menus.get(1));


                categoryText3.setText(parseCategory(menus.get(2)));
                menuText3.setText(parseMainMenu(menus.get(2)));
                subMenuText3.setText(String.join(", ", parseSubMenu(menus.get(2))));
                allergyInfo3 = parseAllergyInfo(menus.get(2));

                // 이미지 로드
                String baseUrl = "https://soongguri.com/menu/menu_file/";
                Glide.with(MenuFragment.this).load(baseUrl + selectedDate + "_1_2.jpg").into(imageView1);
                Glide.with(MenuFragment.this).load(baseUrl + selectedDate + "_1_3.jpg").into(imageView2);
                Glide.with(MenuFragment.this).load(baseUrl + selectedDate + "_1_4.jpg").into(imageView3);

                Toast.makeText(requireContext(), "Menu and Images Loaded", Toast.LENGTH_SHORT).show();
            }
        }




        private String parseCategory(String data) {
            int startIdx = data.indexOf("[");
            int endIdx = data.indexOf("]");
            if (startIdx != -1 && endIdx != -1) {
                // Extract category and trim spaces
                String category = data.substring(startIdx + 1, endIdx).trim();
                return category.replaceAll("[^a-zA-Z0-9가-힣 ]", ""); // Remove unwanted characters
            }
            return "Category 없음";
        }


        private String parseMainMenu(String data) {
            // Main Menu는 '★' 이후, '-' 앞까지 (숫자 포함)
            int startIdx = data.indexOf("★");
            int endIdx = data.indexOf(" -", startIdx);
            if (startIdx != -1 && endIdx != -1) {
                // Extract and trim the main menu name
                String mainMenu = data.substring(startIdx + 1, endIdx).trim();

                // Remove unwanted characters (example: stars, special symbols)
                mainMenu = mainMenu.replaceAll("[^a-zA-Z0-9가-힣 ]", ""); // Remove all non-alphanumeric characters except spaces
                return mainMenu;
            }
            return "메인 메뉴 정보 없음";
        }


        private int findNumberEndIdx(String text) {
            for (int i = 0; i < text.length(); i++) {
                // 숫자와 소수점만 허용하고, 그 외의 문자가 나오면 끝으로 처리
                if (!Character.isDigit(text.charAt(i)) && text.charAt(i) != '.') {
                    return i; // 숫자나 소수점이 끝나는 위치
                }
            }
            return -1; // 숫자가 끝나지 않은 경우
        }

        private List<String> parseSubMenu(String data) {
            List<String> subMenu = new ArrayList<>();

            // '★' 이후부터 메인 메뉴 끝까지
            int mainMenuEndIdx = data.indexOf("★") + "★".length() + parseMainMenu(data).length();
            int allergyStartIdx = data.indexOf("*알러지유발식품:");

            // mainMenuEndIdx와 allergyStartIdx 유효성 검사
            if (mainMenuEndIdx < 0 || mainMenuEndIdx > data.length()) {
                return subMenu;
            }
            if (allergyStartIdx != -1 && mainMenuEndIdx > allergyStartIdx) {
                return subMenu;
            }

            // Allergy 정보가 시작되기 전까지 서브 메뉴로 처리
            String subMenuText = data.substring(mainMenuEndIdx, allergyStartIdx != -1 ? allergyStartIdx : data.length()).trim();

            if (!subMenuText.isEmpty()) {
                String[] items = subMenuText.split("[,\\s]+"); // 쉼표 또는 공백으로 분리
                for (String item : items) {
                    if (!item.isEmpty()) {
                        item = item.trim();
                        if (item.matches(".*[가-힣].*")) { // 한글 문자가 포함된 항목만 추가
                            subMenu.add(item);
                        }
                    }
                }
            }
            return subMenu;
        }


        private boolean containsEnglish(String text) {
            // 영어가 포함되어 있는지 체크하는 함수
            for (char c : text.toCharArray()) {
                if (Character.isAlphabetic(c) && Character.isLowerCase(c)) {  // 소문자 알파벳만 영어로 간주
                    return true;
                }
            }
            return false;
        }

        private String parseAllergyInfo(String data) {
            int startIdx = data.indexOf("*알러지유발식품:");
            if (startIdx != -1) {
                return data.substring(startIdx).trim();
            }
            return "알러지 정보 없음";
        }

        private String parseOriginInfo(String data) {
            int startIdx = data.indexOf("*원산지:");
            if (startIdx != -1) {
                return data.substring(startIdx).trim();
            }
            return "원산지 정보 없음";
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



    private void transferTo(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

    }




}
