package com.example.ssuchelin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private TextView studentInfoTextView;
    private Button fetchDataButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        studentInfoTextView = findViewById(R.id.student_info_text_view);
        fetchDataButton = findViewById(R.id.fetch_data_button);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        // Set button click listener to fetch data from Firebase
        fetchDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });
    }

    private void fetchData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder userData = new StringBuilder();

                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String studentId = studentSnapshot.getKey();
                    DataSnapshot userAccountSnapshot = studentSnapshot.child("UserAccount");
                    DataSnapshot userInfoSnapshot = studentSnapshot.child("userinfo");

                    if (userAccountSnapshot.exists() && userInfoSnapshot.exists()) {
                        String realStudentId = userAccountSnapshot.child("realStudentId").getValue(String.class);
                        String nickname = userInfoSnapshot.child("nickname").getValue(String.class);
                        String saltPreference = userInfoSnapshot.child("saltPreference").getValue(String.class);
                        String spicyPreference = userInfoSnapshot.child("spicyPreference").getValue(String.class);
                        String userName = userInfoSnapshot.child("userName").getValue(String.class);

                        userData.append("Student ID: ").append(realStudentId).append("\n")
                                .append("Nickname: ").append(nickname).append("\n")
                                .append("Salt Preference: ").append(saltPreference).append("\n")
                                .append("Spicy Preference: ").append(spicyPreference).append("\n")
                                .append("User Name: ").append(userName).append("\n\n");
                    }
                }
                studentInfoTextView.setText(userData.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RegisterActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }
}
