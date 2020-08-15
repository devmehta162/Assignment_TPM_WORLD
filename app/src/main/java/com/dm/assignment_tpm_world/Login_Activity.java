package com.dm.assignment_tpm_world;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login_Activity extends AppCompatActivity {
    EditText editText;
    Button button;
    String mVerificationId;
    private FirebaseAuth Auth;
    private TextView resend;
    private static final int NotificID = 01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        editText = findViewById(R.id.code);
        button = findViewById(R.id.btn);
        resend = findViewById(R.id.resend);

        Auth = FirebaseAuth.getInstance();

        final String number = getIntent().getStringExtra("phoneNo");
        sendVerificationCode(number);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(number);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editText.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editText.setText("Invalid");
                    editText.requestFocus();
                    return;
                }
                verifyCode(code);
            }


        });

    }

    private void verifyCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void sendVerificationCode(String number) {
        new CountDownTimer(60000, 1000) {


            @Override
            public void onTick(long millisUntilFinished) {
                resend.setText("" + millisUntilFinished / 1000);
                resend.setEnabled(false);
            }

            @Override
            public void onFinish() {

                resend.setText("Resend");
                resend.setEnabled(true);
            }
        }.start();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            String code = credential.getSmsCode();
            if (code != null) {
                editText.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e instanceof FirebaseAuthInvalidCredentialsException) {

                Toast.makeText(getApplicationContext(), "Invalid Number", Toast.LENGTH_LONG).show();
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(getApplicationContext(), "The SMS quota for the project has been exceeded", Toast.LENGTH_LONG).show();

            }

            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {


            mVerificationId = verificationId;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

//
                            Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                            startActivity(intent);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                            addNotification();
                            // displayNotification();


//                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(Login_Activity.this);
//                            notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(Login_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }
                });
    }

    private void addNotification() {

        Notification notification = new Notification.Builder(Login_Activity.this)
                .setContentTitle("PDF Viewer")
                .setContentText("Succesfully Signed in")
                .setSmallIcon(R.drawable.ic_baseline_message_24)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(NotificID, notification);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Login_Activity.this, SignInPage_Activity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
