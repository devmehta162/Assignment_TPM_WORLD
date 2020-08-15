package com.dm.assignment_tpm_world;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignInPage_Activity extends AppCompatActivity {
    private EditText number;
    private Button next;
    private Toast backToast;
    private long backPressedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page_);

        ActivityCompat.requestPermissions(SignInPage_Activity.this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);

        number = findViewById(R.id.enter_PhoneNumber);
        next = findViewById(R.id.btn_next);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phoneNo = number.getText().toString().trim();
                if (TextUtils.isEmpty(number.getText().toString())) {
                    Toast.makeText(SignInPage_Activity.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (number.getText().toString().replace(" ", "").length() != 10) {
                    Toast.makeText(SignInPage_Activity.this, "Enter Correct Mobile Number", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SignInPage_Activity.this, Login_Activity.class);
                    intent.putExtra("phoneNo", phoneNo);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity();
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(SignInPage_Activity.this, "Press back again to exit", Toast.LENGTH_LONG);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}