package com.example.solo_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class TestGoogleLoginActivity extends AppCompatActivity {
    private GoogleSignInClient googleSignInClient;
    private SignInButton login;
    private String TAG = "TEST";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_google_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("336778946478-iv0apki0rc65q5o00o34tlost9qunb6u.apps.googleusercontent.com")   //클라이언트 ID 보내기
                .requestEmail()
                .requestProfile()
                .build();
        login = findViewById(R.id.googleLoginBtn);
        googleSignInClient = GoogleSignIn.getClient(TestGoogleLoginActivity.this,gso);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }
    protected void signIn(){
        Intent i = googleSignInClient.getSignInIntent();
        startActivityForResult(i,100);


    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if(requestCode == 100){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            handleSignInResult(task);

        }
        super.startActivityForResult(intent, requestCode);
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String email = account.getEmail();
            Log.d(TAG, "handleSignInResult: "+email);
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
}