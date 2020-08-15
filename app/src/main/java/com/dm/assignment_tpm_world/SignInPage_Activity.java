package com.dm.assignment_tpm_world;

import androidx.annotation.NonNull;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInPage_Activity extends AppCompatActivity {
    private EditText number;
    private Button next;
    private Toast backToast;
    private long backPressedTime;
    private String userName;
    private String userEmail;
    private GoogleSignInClient mGoogleSignInClient;
    private Button GoogleLoginBtn;
    public static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page_);

        GoogleLoginBtn = findViewById(R.id.btn_googleSignIn);
        mAuth = FirebaseAuth.getInstance();
        ActivityCompat.requestPermissions(SignInPage_Activity.this, new String[]{Manifest.permission.SEND_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        number = findViewById(R.id.enter_PhoneNumber);
        next = findViewById(R.id.btn_next);

        GoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        CreateRequest();
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
    private void CreateRequest() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //   Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(SignInPage_Activity.this, MainActivity.class);
                            intent.putExtra("name",userName);
                            intent.putExtra("email",userEmail);
                            startActivity(intent);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            userName = signInAccount.getGivenName();
            userEmail = signInAccount.getEmail();

            //  Toast.makeText(this, userName+userEmail, Toast.LENGTH_SHORT).show();
        }
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