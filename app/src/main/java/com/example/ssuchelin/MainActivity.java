package com.example.ssuchelin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ssuchelin.menu.MenuFragment;
import com.example.ssuchelin.ranking.RankingFragment;
import com.example.ssuchelin.review.EditReviewDialogFragment;
import com.example.ssuchelin.review.OverviewReviewsFragment;
import com.example.ssuchelin.user.ProfileViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements EditReviewDialogFragment.OnReviewUpdatedListener {

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
                switchFragment(new OverviewReviewsFragment());
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

    @Override
    public void onReviewUpdated(String updatedReviewText) {
        // 수정된 리뷰에 대한 처리 로직
        Toast.makeText(this, "Updated Review: " + updatedReviewText, Toast.LENGTH_SHORT).show();
    }




}
