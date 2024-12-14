package com.example.ssuchelin.menu;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import com.example.ssuchelin.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarDialogFragment extends DialogFragment {

    private CalendarView calendarView;
    private String selectedDate;

    // Listener 인터페이스 정의
    public interface OnDateSelectedListener {
        void onDateSelected(Date date) throws ParseException;
    }

    private OnDateSelectedListener listener;

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_calendar, container, false);

        calendarView = view.findViewById(R.id.dialogCalendarView);
        Button btnSelectDate = view.findViewById(R.id.btnSelectDate);

        // 날짜 선택 리스너
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            month += 1; // 월은 0부터 시작하므로 보정
            selectedDate = String.format("%04d-%02d-%02d", year, month, dayOfMonth);
        });

        btnSelectDate.setOnClickListener(v -> {
            if (listener != null && selectedDate != null) {
                try {
                    // 선택된 날짜를 String -> Date로 변환
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = sdf.parse(selectedDate); // selectedDate가 "yyyy-MM-dd" 형식임을 가정

                    // 날짜 리스너 호출
                    listener.onDateSelected(date);  // Date 객체를 전달
                } catch (ParseException e) {
                    e.printStackTrace();  // 예외 처리
                }
            }
            dismiss(); // Dialog 닫기
        });

        return view;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}