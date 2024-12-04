package com.example.ssuchelin.review;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ssuchelin.R;
import com.example.ssuchelin.databinding.FragmentReviewBinding;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragment extends Fragment {
    private FragmentReviewBinding binding;
    private List<Review> reviewList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReviewBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String mainMenu = bundle.getString("mainMenu");
            String subMenu = bundle.getString("subMenu");
            String category = bundle.getString("category");
            Bitmap bitmap = bundle.getParcelable("imageBitmap");

            // 데이터를 UI에 설정

            binding.foodCategory.setText(category);
            binding.foodSubMenu.setText(subMenu);
            binding.foodMainMenu.setText(mainMenu);
            binding.foodImage.setImageBitmap(bitmap);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reviewAdapter = new ReviewAdapter(reviewList);
        binding.reviewRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.reviewRecyclerView.setAdapter(reviewAdapter);


        binding.writeReview.setOnClickListener(v -> {


            // 데이터를 Bundle에 저장
            Bundle bundle = new Bundle();
            bundle.putString("mainMenu", binding.foodMainMenu.getText().toString());
            bundle.putString("subMenu", binding.foodSubMenu.getText().toString());
            bundle.putString("category", binding.foodCategory.getText().toString());
            bundle.putParcelable("imageBitmap", (((BitmapDrawable) binding.foodImage.getDrawable()).getBitmap()));  // 이미지 데이터를 Bitmap 형태로 전달);

            // WriteReviewFragment로 데이터를 전달하면서 이동
            WriteReviewFragment writeReviewFragment = new WriteReviewFragment();
            writeReviewFragment.setArguments(bundle);


            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, writeReviewFragment)
                    .addToBackStack(null)
                    .commit();

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}


// review를 만들고 등록
//
//    private List<Review> getReviewList() {
//        List<Review> reviewList = new ArrayList<>();
//
//        reviewList
//
//
//        return reviewList;
//    }


