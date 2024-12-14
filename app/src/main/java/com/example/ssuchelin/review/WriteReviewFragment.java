package com.example.ssuchelin.review;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.ssuchelin.R;
import com.example.ssuchelin.databinding.FragmentWriteReviewBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WriteReviewFragment extends Fragment {

    private EditText reviewEditText;
    private TextView characterCounterTextView, mainMenu;
    private ImageButton starButton1, starButton2, starButton3;
    private boolean isStar1On = false, isStar2On = false, isStar3On = false;
    private int starCount = 0;
    private DatabaseReference mDatabaseRef;
    private Button submitButton;
    private boolean isEditMode = false;
    private int reviewId = -1;
    private int score = 0;
    private String type;
    private TextView foodCategory,foodMainMenu,foodSubMenu;
    private ImageView foodImage;
    private FragmentWriteReviewBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWriteReviewBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String mainMenu = bundle.getString("mainMenu");
            String subMenu = bundle.getString("subMenu");
            String category = bundle.getString("category");
            Bitmap bitmap = bundle.getParcelable("imageBitmap");

            binding.foodCategory.setText(category);
            binding.foodSubMenu.setText(subMenu);
            binding.foodMainMenu.setText(mainMenu);
            binding.foodImage.setImageBitmap(bitmap);

        }

        return binding.getRoot();
    }


    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        type = getArguments().getString("type"); //ddook,dub,yang
        if(type == "ddook") type = "뚝배기 코너";
        if(type == "dub") type = "덮밥 코너";
        if(type == "yang") type = "양식 코너";

        // XML 참조
        foodCategory = view.findViewById(R.id.foodCategory);
        foodMainMenu = view.findViewById(R.id.foodMainMenu);
        foodSubMenu = view.findViewById(R.id.foodSubMenu);
        foodImage = view.findViewById(R.id.foodImage);

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

        // XML 참조
        starButton1 = view.findViewById(R.id.starButton1);
        starButton2 = view.findViewById(R.id.starButton2);
        starButton3 = view.findViewById(R.id.starButton3);
        reviewEditText = view.findViewById(R.id.review_edit_text);
        submitButton = view.findViewById(R.id.submit_button);
        characterCounterTextView = view.findViewById(R.id.character_counter);

        // 수정 모드 판단
        Bundle arguments = getArguments();
        if (arguments != null) {
            isEditMode = arguments.getBoolean("editMode", false);
            String reviewText = arguments.getString("reviewText");
            reviewId = arguments.getInt("reviewId", -1);
            starCount = arguments.getInt("starCount", 0);

            if (isEditMode) {
                reviewEditText.setText(reviewText);
                submitButton.setText("수정하기");
                updateStarButtons();
            }
        }

        // EditText에 TextWatcher 설정
        reviewEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int textLength = s.length();
                characterCounterTextView.setText(textLength + " / 300");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 별 버튼 클릭 리스너 설정
        starButton1.setOnClickListener(starClickListener);
        starButton2.setOnClickListener(starClickListener);
        starButton3.setOnClickListener(starClickListener);

        // SharedPreferences에서 사용자 ID 가져오기
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String studentId = sharedPreferences.getString("realStudentId", "Unknown ID");

        submitButton.setOnClickListener(view1 -> {
            String review = reviewEditText.getText().toString();

            if (isEditMode) {
                // 수정 모드에서 업데이트
                updateUserReview(studentId, reviewId, review);
            } else {
                // 새 리뷰 저장
                saveUserReview(studentId, review);
            }
        });

    }

    // 클릭 리스너 정의
    private final View.OnClickListener starClickListener = view -> {
        // 첫 번째 별 클릭 시
        // starCount가 1이면 다시 0으로 만들고, 아니면 1로 설정
        if (view.getId() == R.id.starButton1) {
            if (starCount == 1) {
                starCount = 0; // 이미 1점 상태에서 다시 첫 번째 별 클릭하면 0점
            } else {
                starCount = 1; // 그렇지 않다면 1점
            }
        } else if (view.getId() == R.id.starButton2) {
            // 두 번째 별 클릭 시 항상 2점
            starCount = 2;
        } else if (view.getId() == R.id.starButton3) {
            // 세 번째 별 클릭 시 항상 3점
            starCount = 3;
        }

        // 변경된 starCount에 따라 별 UI 갱신
        updateStarButtons();
    };

    // 별 UI 업데이트
    private void updateStarButtons() {
        starButton1.setImageResource(starCount >= 1 ? R.drawable.star_100 : R.drawable.star_0);
        starButton2.setImageResource(starCount >= 2 ? R.drawable.star_100 : R.drawable.star_0);
        starButton3.setImageResource(starCount >= 3 ? R.drawable.star_100 : R.drawable.star_0);
    }

    // 리뷰 저장
    private void saveUserReview(String studentId, String review) {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User").child(studentId);
        mDatabaseRef.child("reviewCnt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                AtomicInteger reviewCnt = new AtomicInteger(snapshot.exists() ? snapshot.getValue(Integer.class) : 0);
                reviewCnt.getAndIncrement();

                String reviewKey = "review_" + reviewCnt.get();
                Map<String, Object> userReview = new HashMap<>();
                userReview.put("userReview", review);
                userReview.put("starCount", starCount);
                userReview.put("score", score);


                // 메인 메뉴와 서브 메뉴 추가
                userReview.put("Mainmenu", binding.foodMainMenu.getText().toString());
                userReview.put("Submenu", binding.foodSubMenu.getText().toString());

                // 여기 밑에 추가해줘 whoWriteReview에는 쓴 사람의 studentId를 가져와야해
                // Category/{type}/{Mainmenu}/{메인메뉴이름}/whoWriteReview/{studentId}: true
                DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Category");
                categoryRef.child(type) // type 들어가는곳
                        .child("Mainmenu")
                        .child(binding.foodMainMenu.getText().toString())
                        .child("whoWriteReview")
                        .child(studentId)
                        .setValue(true);

                mDatabaseRef.child("myReviewData").child(reviewKey).setValue(userReview).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mDatabaseRef.child("reviewCnt").setValue(reviewCnt.get());
                        Toast.makeText(getContext(), "리뷰가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "리뷰 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "데이터베이스 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 리뷰 업데이트
    private void updateUserReview(String studentId, int reviewId, String updatedReview) {
        String reviewKey = "review_" + (reviewId + 1);

        DatabaseReference reviewRef = FirebaseDatabase.getInstance()
                .getReference("User")
                .child(studentId)
                .child("myReviewData")
                .child(reviewKey);

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("userReview", updatedReview);
        updatedData.put("starCount", starCount);
        updatedData.put("score", score);

        // 메인 메뉴와 서브 메뉴 업데이트 추가
        updatedData.put("Mainmenu", binding.foodMainMenu.getText().toString());
        updatedData.put("Submenu", binding.foodSubMenu.getText().toString());

        // 여기 밑에 추가해줘 whoWriteReview에는 쓴 사람의 studentId를 가져와야해
        // Category/{type}/{Mainmenu}/{메인메뉴이름}/whoWriteReview/{studentId}: true
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String studentIdSP = sharedPreferences.getString("realStudentId", "Unknown ID");
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Category");
        categoryRef.child(type) // type 들어가는곳
                .child("Mainmenu")
                .child(binding.foodMainMenu.getText().toString())
                .child("whoWriteReview")
                .child(studentIdSP)
                .setValue(true);

        reviewRef.updateChildren(updatedData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "리뷰가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "리뷰 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
