package com.example.ssuchelin.review;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryFragment extends Fragment {

    private String parentNode, subCategoryKey, subCategoryName;
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private LinearLayout subCategoryContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_category, container, false);

        // Toolbar 설정
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
            }
        }

        toolbar.setNavigationOnClickListener(v -> {
            if (!requireActivity().getSupportFragmentManager().isStateSaved()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Bundle arguments = getArguments();
        if (arguments == null) {
            Toast.makeText(getContext(), "필요한 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            return view;
        }

        parentNode = arguments.getString("parentNode", "");
        subCategoryKey = arguments.getString("subCategoryKey", "");
        subCategoryName = arguments.getString("subCategoryName", "");

        TextView subCategoryTextView = view.findViewById(R.id.sub_category_name);
        subCategoryTextView.setText(parentNode + " > " + subCategoryName);

        subCategoryTextView.setPadding(
                subCategoryTextView.getPaddingLeft(),
                subCategoryTextView.getPaddingTop(),
                subCategoryTextView.getPaddingRight(),
                dpToPx(20)
        );

        subCategoryContainer = view.findViewById(R.id.sub_category_container);

        // RecyclerView 설정
        reviewRecyclerView = new RecyclerView(getContext());
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        reviewRecyclerView.setAdapter(reviewAdapter);
        subCategoryContainer.addView(reviewRecyclerView);

        // 여기서 subCategoryKey가 "Mainmenu"면 하위 메뉴 목록을 불러오고,
        // 아니면 실제 메뉴명이라고 판단, whoWriteReview에서 리뷰 로드
        if (subCategoryKey.equals("Mainmenu")) {
            loadMainMenus();
        } else {
            // subCategoryKey가 실제 메뉴명
            loadReviewsForMenu(subCategoryKey);
        }

        return view;
    }

    // dp 값을 px로 변환하는 메서드
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }


    // Mainmenu 하위 메뉴 로드
    private void loadMainMenus() {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Category").child(parentNode).child(subCategoryKey);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subCategoryContainer.removeAllViews(); // 중복된 뷰 제거

                if (!snapshot.exists()) {
                    TextView noDataTextView = new TextView(getContext());
                    noDataTextView.setText("하위 메뉴가 없습니다.");
                    noDataTextView.setTextSize(16);
                    noDataTextView.setPadding(16, 16, 16, 16);
                    subCategoryContainer.addView(noDataTextView);
                    return;
                }

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String childNodeName = childSnapshot.getKey();
                    if (childNodeName != null) {
                        TextView childTextView = new TextView(getContext());
                        childTextView.setText("   > " + childNodeName);
                        childTextView.setTextSize(16);
                        childTextView.setPadding(32, 16, 16, 16);

                        // 메뉴 아이템 클릭 시 다시 SubCategoryFragment 호출 (여기서 childNodeName이 실제 메뉴)
                        childTextView.setOnClickListener(v -> openSubCategoryAgain(parentNode, childNodeName, childNodeName));
                        subCategoryContainer.addView(childTextView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                TextView errorTextView = new TextView(getContext());
                errorTextView.setText("하위 메뉴를 불러오지 못했습니다.");
                errorTextView.setTextSize(16);
                errorTextView.setPadding(16, 16, 16, 16);
                subCategoryContainer.addView(errorTextView);
            }
        });
    }

    // 실제 메뉴 클릭 시 다시 SubCategoryFragment로 이동
    private void openSubCategoryAgain(String parentNode, String subCategoryKey, String subCategoryName) {
        SubCategoryFragment fragment = new SubCategoryFragment();
        Bundle args = new Bundle();
        args.putString("parentNode", parentNode);
        args.putString("subCategoryKey", subCategoryKey);
        args.putString("subCategoryName", subCategoryName);
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    // 실제 메뉴에 대한 리뷰 로드
    private void loadReviewsForMenu(String menuName) {
        reviewAdapter.updateReviews(new ArrayList<>()); // 초기화
        DatabaseReference whoWriteRef = FirebaseDatabase.getInstance().getReference("Category")
                .child(parentNode)
                .child("Mainmenu")
                .child(menuName)
                .child("whoWriteReview");

        whoWriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    reviewAdapter.updateReviews(new ArrayList<>());
                    return;
                }

                List<String> studentIds = new ArrayList<>();
                for (DataSnapshot idSnap : snapshot.getChildren()) {
                    String studentId = idSnap.getKey();
                    if (!TextUtils.isEmpty(studentId)) {
                        studentIds.add(studentId);
                    }
                }

                if (studentIds.isEmpty()) {
                    reviewAdapter.updateReviews(new ArrayList<>());
                } else {
                    loadUserReviews(studentIds, menuName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "리뷰 로드 중 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserReviews(List<String> studentIds, String menuName) {
        List<Review> allReviews = new ArrayList<>();
        final int[] remaining = {studentIds.size()};
        for (String studentId : studentIds) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    // /// 수정 부분: userName 가져오기
                    String userName = userSnapshot.child("userinfo").child("userName").getValue(String.class);
                    if (userName == null) userName = studentId; // userName 없으면 studentId 사용

                    DataSnapshot myReviewData = userSnapshot.child("myReviewData");
                    for (DataSnapshot reviewSnap : myReviewData.getChildren()) {
                        String reviewMainMenu = reviewSnap.child("Mainmenu").getValue(String.class);
                        if (menuName.equals(reviewMainMenu)) {
                            String userReview = reviewSnap.child("userReview").getValue(String.class);
                            Integer starCount = reviewSnap.child("starCount").getValue(Integer.class);

                            // 작성자를 userName으로 표시
                            Review reviewObj = new Review(userName, userReview != null ? userReview : "",
                                    starCount != null ? starCount : 0);
                            allReviews.add(reviewObj);
                        }
                    }

                    remaining[0]--;
                    if (remaining[0] == 0) {
                        reviewAdapter.updateReviews(allReviews);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    remaining[0]--;
                    if (remaining[0] == 0) {
                        reviewAdapter.updateReviews(allReviews);
                    }
                }
            });
        }
    }

    // 리뷰 객체
    class Review {
        private String userName;  // /// 수정 부분: userName 필드 사용
        private String userReview;
        private int starCount;

        public Review(String userName, String userReview, int starCount) {
            this.userName = userName;
            this.userReview = userReview;
            this.starCount = starCount;
        }

        public String getUserName() { return userName; } // /// 수정 부분: userName getter
        public String getUserReview() { return userReview; }
        public int getStarCount() { return starCount; }
    }

    // RecyclerView 어댑터
    class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

        private List<Review> reviewList;

        public ReviewAdapter(List<Review> reviewList) {
            this.reviewList = reviewList;
        }

        @NonNull
        @Override
        public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav2, parent, false);
            return new ReviewViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
            Review review = reviewList.get(position);
            // /// 수정 부분: 작성자에 userName 사용
            holder.usernameTextView.setText(review.getUserName());
            holder.reviewTextView.setText(review.getUserReview());

            // 별점에 따라 별 UI 업데이트
            holder.star1.setImageResource(review.getStarCount() >= 1 ? R.drawable.star_100 : R.drawable.star_0);
            holder.star2.setImageResource(review.getStarCount() >= 2 ? R.drawable.star_100 : R.drawable.star_0);
            holder.star3.setImageResource(review.getStarCount() >= 3 ? R.drawable.star_100 : R.drawable.star_0);

        }

        @Override
        public int getItemCount() {
            return reviewList.size();
        }

        public void updateReviews(List<Review> newReviews) {
            this.reviewList = newReviews;
            notifyDataSetChanged();
        }

        class ReviewViewHolder extends RecyclerView.ViewHolder {
            TextView usernameTextView, reviewTextView;
            ImageView star1, star2, star3; // ImageView로 변경

            public ReviewViewHolder(@NonNull View itemView) {
                super(itemView);
                usernameTextView = itemView.findViewById(R.id.review_username);
                reviewTextView = itemView.findViewById(R.id.review_content);
                star1 = itemView.findViewById(R.id.review_star1);
                star2 = itemView.findViewById(R.id.review_star2);
                star3 = itemView.findViewById(R.id.review_star3);
            }
        }
    }

}
