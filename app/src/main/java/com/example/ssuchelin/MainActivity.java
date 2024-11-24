package com.example.ssuchelin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ssuchelin.menu.MenuFragment;
import com.example.ssuchelin.ranking.RankingFragment;
import com.example.ssuchelin.review.ReviewFragment;
import com.example.ssuchelin.user.ProfileViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            String openFragment = getIntent().getStringExtra("open_fragment");

            if ("MenuFragment".equals(openFragment)) {
                switchFragment(new MenuFragment());
            } else {
                switchFragment(new MenuFragment()); // 기본 프래그먼트도 MainViewFragment로 설정
            }
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.page_1) {
                switchFragment(new MenuFragment());
                return true;
            } else if (item.getItemId() == R.id.page_2) {
                switchFragment(new ReviewFragment());
                return true;
            } else if (item.getItemId() == R.id.page_3) {
                switchFragment(new RankingFragment());
                return true;
            } else if (item.getItemId() == R.id.page_4) {
                switchFragment(new ProfileViewFragment());
                return true;
            }
            return false;
        });
    }

    public void switchFragment(Fragment fragment) {
        String fragmentTag = fragment.getClass().getSimpleName();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment != null && currentFragment.getClass().getSimpleName().equals(fragmentTag)) {
            return; // 이미 표시 중인 프래그먼트라면 전환하지 않음
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, fragmentTag)
                .addToBackStack(null)
                .commit();

    }




}
