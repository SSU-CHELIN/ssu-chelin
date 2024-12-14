package com.example.ssuchelin.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.ssuchelin.R;
import com.example.ssuchelin.login.LoginActivity;
import com.example.ssuchelin.review.CheckReviewsFragment;
import com.example.ssuchelin.review.FeedbackFragment;
import com.example.ssuchelin.review.WriteReviewFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProfileViewFragment extends Fragment {

    private DatabaseReference database;
    private TextView studentIdTextView, usernameTextView, userNicknameTextView, rankingTextView;
    private TextView changeInitialSettings, changeFirstSettings, checkReviews, privacyPolicy, contactUs, logout;
    private ImageView star1, star2, star3;

    private View loadingLayout;
    private View profileContentLayout;

    private String studentId;
    private String nickname;
    private int dataLoadCount = 0;

    private float averageStar;

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_view, container, false);

        studentIdTextView = view.findViewById(R.id.student_id);
        usernameTextView = view.findViewById(R.id.username);
        userNicknameTextView = view.findViewById(R.id.user_nickname);
        rankingTextView = view.findViewById(R.id.ranking);

        changeInitialSettings = view.findViewById(R.id.change_initial_settings);
        changeFirstSettings = view.findViewById(R.id.change_first_settings);
        checkReviews = view.findViewById(R.id.check_reviews);
        privacyPolicy = view.findViewById(R.id.privacy_policy);
        contactUs = view.findViewById(R.id.contact_us);
        logout = view.findViewById(R.id.logout);

        star1 = view.findViewById(R.id.star1);
        star2 = view.findViewById(R.id.star2);
        star3 = view.findViewById(R.id.star3);

        loadingLayout = view.findViewById(R.id.profile_loading_layout);
        profileContentLayout = view.findViewById(R.id.profile_content_layout);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        studentId = sharedPreferences.getString("realStudentId", "Unknown ID");
        studentIdTextView.setText(studentId);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoading(true);

        fetchUserInfoFromDatabase();
        fetchUserReviewsForAverageStar();
        fetchUserRanking();
        setButtonListeners();
    }

    private void showLoading(boolean isLoading) {
        if (loadingLayout != null && profileContentLayout != null) {
            loadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            profileContentLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }

    private void fetchUserInfoFromDatabase() {
        database = FirebaseDatabase.getInstance().getReference("User").child(studentId).child("userinfo");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String username = snapshot.child("userName").getValue(String.class);
                if (username != null) {
                    usernameTextView.setText(username);
                } else {
                    usernameTextView.setText("Unknown User");
                }

                nickname = snapshot.child("nickname").getValue(String.class);
                if (nickname == null) nickname = "닉네임없음";
                userNicknameTextView.setText(nickname);

                checkDataLoadComplete();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getActivity(), "데이터베이스 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                checkDataLoadComplete();
            }
        });
    }

    private void fetchUserReviewsForAverageStar() {
        DatabaseReference myReviewRef = FirebaseDatabase.getInstance().getReference("User").child(studentId).child("myReviewData");
        myReviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalStars = 0;
                int reviewCount = 0;
                for (DataSnapshot reviewSnap : snapshot.getChildren()) {
                    Integer starCountVal = reviewSnap.child("starCount").getValue(Integer.class);
                    if (starCountVal != null) {
                        totalStars += starCountVal;
                        reviewCount++;
                    }
                }
                if (reviewCount > 0) {
                    averageStar = totalStars / reviewCount;
                } else {
                    averageStar = 0;
                }
                updateStarImages(averageStar);
                checkDataLoadComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                averageStar = 0;
                updateStarImages(averageStar);
                checkDataLoadComplete();
            }
        });
    }

    private void fetchUserRanking() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserRank> userRankList = new ArrayList<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String uid = userSnap.getKey();
                    if (uid == null) continue;
                    Integer userTotalLike = userSnap.child("totalLike").getValue(Integer.class);
                    if (userTotalLike == null) userTotalLike = 0;

                    userRankList.add(new UserRank(uid, userTotalLike));
                }

                Collections.sort(userRankList, new Comparator<UserRank>() {
                    @Override
                    public int compare(UserRank o1, UserRank o2) {
                        return Integer.compare(o2.totalLike, o1.totalLike);
                    }
                });

                int rank = 1;
                for (UserRank ur : userRankList) {
                    if (ur.userId.equals(studentId)) {
                        rankingTextView.setText("랭킹 : " + rank + "위");
                        break;
                    }
                    rank++;
                }

                checkDataLoadComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                rankingTextView.setText("랭킹 : ?");
                checkDataLoadComplete();
            }
        });
    }

    private void checkDataLoadComplete() {
        dataLoadCount++;
        if (dataLoadCount >= 3) {
            showLoading(false);
        }
    }

    private void updateStarImages(float avg) {
        float portion1 = avg - 0;
        float portion2 = avg - 1;
        float portion3 = avg - 2;

        star1.setImageResource(getStarResourceForPortion(portion1));
        star2.setImageResource(getStarResourceForPortion(portion2));
        star3.setImageResource(getStarResourceForPortion(portion3));
    }

    private int getStarResourceForPortion(float portion) {
        if (portion <= 0) {
            return R.drawable.star_0;
        } else if (portion >= 1) {
            return R.drawable.star_100;
        } else {
            float percent = portion * 100;
            float[] thresholds = {0,12.5f,25f,37.5f,50f,62.5f,75f,87.5f,100f};
            int[] drawables = {
                    R.drawable.star_0,
                    R.drawable.star_12_5,
                    R.drawable.star_25,
                    R.drawable.star_37_5,
                    R.drawable.star_50,
                    R.drawable.star_62_5,
                    R.drawable.star_75,
                    R.drawable.star_87_5,
                    R.drawable.star_100
            };

            float minDiff = Float.MAX_VALUE;
            int chosenIndex = 0;
            for (int i = 0; i < thresholds.length; i++) {
                float diff = Math.abs(percent - thresholds[i]);
                if (diff < minDiff) {
                    minDiff = diff;
                    chosenIndex = i;
                }
            }
            return drawables[chosenIndex];
        }
    }

    private void setButtonListeners() {
        changeInitialSettings.setOnClickListener(v -> {
            switchFragment(new InitialSettingFragment());
        });

        changeFirstSettings.setOnClickListener(v -> {
            switchFragment(new FirstSettingFragment());
        });

        checkReviews.setOnClickListener(v -> {
            switchFragment(new CheckReviewsFragment());
        });

        contactUs.setOnClickListener(v -> {
            switchFragment(new FeedbackFragment());
        });

        logout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인", (dialog, which) -> logout())
                    .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void switchFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    static class UserRank {
        String userId;
        int totalLike;

        UserRank(String userId, int totalLike) {
            this.userId = userId;
            this.totalLike = totalLike;
        }
    }
}
