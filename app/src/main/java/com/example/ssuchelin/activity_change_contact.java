package com.example.ssuchelin;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//(프로필정보바꾸기) 연락처변경 클릭시

public class activity_change_contact extends AppCompatActivity {

    private EditText newContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_contact);

        newContact = findViewById(R.id.new_contact);

        findViewById(R.id.save_contact_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });
    }

    private void saveContact() {
        String contact = newContact.getText().toString().trim();
        if (!contact.isEmpty()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child("user_id");
            userRef.child("contact").setValue(contact);
            Toast.makeText(this, "연락처가 변경되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "연락처를 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
