package com.example.ssuchelin.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.ssuchelin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SubCategoryFragment extends Fragment {

    private LinearLayout subCategoryContainer; // 하위 노드를 출력할 컨테이너
    private DatabaseReference categoryRef; // Firebase 참조

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        // 전달된 상위/중간 노드 이름과 키 가져오기
        Bundle arguments = getArguments();
        if (arguments == null) {
            Toast.makeText(getContext(), "필요한 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            return view;
        }

        String parentNode = arguments.getString("parentNode", "");
        String subCategoryKey = arguments.getString("subCategoryKey", "");
        String subCategoryName = arguments.getString("subCategoryName", "");

        // 타이틀 구성
        TextView subCategoryTextView = view.findViewById(R.id.sub_category_name);
        subCategoryTextView.setText(parentNode + "코너 > " + subCategoryName);

        // 하위 노드 출력 컨테이너
        subCategoryContainer = view.findViewById(R.id.sub_category_container);

        // Firebase 참조
        categoryRef = FirebaseDatabase.getInstance().getReference("Category").child(parentNode).child(subCategoryKey);

        // 하위 노드 로드
        loadSubCategories();

        return view;
    }

    private void loadSubCategories() {
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
                    String childNodeName = childSnapshot.getValue(String.class);
                    if (childNodeName != null) {
                        TextView childTextView = new TextView(getContext());
                        childTextView.setText("   > " + childNodeName);
                        childTextView.setTextSize(16);
                        childTextView.setPadding(32, 16, 16, 16);
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
}
