package com.example.solo_project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {
    FirebaseAuth mauth;
    private String verficationID;
    private EditText verifycode;
    private Button varify_Btn;
    private String phone_number;
    private LinearLayout Linear;
    private EditText inputPhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mauth = FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_signup);
        Button verify = findViewById(R.id.verify);
        Button next = findViewById(R.id.next0);
        Button Verification = findViewById(R.id.sendveritication);
        varify_Btn = findViewById(R.id.verify_btn);
        inputPhoneNumber = findViewById(R.id.inputnumber);
        inputPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


        EditText e_mail_signup = findViewById(R.id.e_mail_signup);
        verifycode = findViewById(R.id.verifycode);
        EditText Password = findViewById(R.id.password2);
        Linear = findViewById(R.id.Linear);

        verifycode.setVisibility(View.GONE);
        varify_Btn.setVisibility(View.GONE);
        Linear.setVisibility(View.GONE);
        Password.addTextChangedListener(new TextWatcher() {
            EditText Pass = findViewById(R.id.password);
            TextView checkPass = findViewById(R.id.checkpassword);
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(TextUtils.isEmpty(Pass.toString().trim())){
                    checkPass.setText("");
                }else if(Pass.getText().toString().trim().equals(editable.toString())){
                    checkPass.setText("두 패스워드가 일치합니다.");
                    checkPass.setTextColor(Color.parseColor("#00ff22"));
                }else{
                    checkPass.setText("두 패스워드가 일치하지 않습니다.");
                    checkPass.setTextColor(Color.parseColor("#ff0000"));
                }
            }
        });
        e_mail_signup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TextView check = findViewById(R.id.checkemail);
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(editable.toString()).matches()){
                    check.setText("이메일 형식으로 입력해주세요.");
                    check.setTextColor(Color.parseColor("#ff0000"));// 경고 메세지
                }
                else{
                    check.setText("");         //에러 메세지 제거
                    check.setTextColor(Color.parseColor("#000000"));  //테투리 흰색으로 변경
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(Signup.this,Signup_prof.class);
                String email = e_mail_signup.getText().toString();
                String PW = Password.getText().toString();
                I.putExtra("email",email);
                I.putExtra("PW",PW);
                I.putExtra("phone_number",phone_number);
                startActivity(I);

            }
        });
        Verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText n = findViewById(R.id.inputnumber);
                String number = n.getText().toString().replaceAll("-","").substring(3);
                Log.d("SignupActivity", "onClick: 전화번호"+number);
                if(TextUtils.isEmpty(n.getText().toString())){
                    Toast T = Toast.makeText(Signup.this,"번호를 입력해주세요",Toast.LENGTH_SHORT);
                    T.show();
                }else if(number.length() > 11){
                    Toast.makeText(Signup.this,"제대로 된 번호를 입력해주세요.",Toast.LENGTH_SHORT);
                }else{
                    send_verificationcode(number);
                }
            }
        });
        varify_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(verifycode.getText().toString())){
                    Toast.makeText(Signup.this,"인증번호를 입력해주세요",Toast.LENGTH_SHORT);
                }else{
                    verifycode(verifycode.getText().toString());
                }
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText E_mail = findViewById(R.id.e_mail_signup);
                String email = E_mail.getText().toString().trim();
                Button Btn = findViewById(R.id.verify);
                TextView check = findViewById(R.id.checkemail);
                if(verify.getText().toString().equals("중복 확인")){
                    if(email.equals("")){
                        Toast T = Toast.makeText(getApplicationContext(),"이메일을 입력해주세요",Toast.LENGTH_SHORT);
                        T.show();
                    }else{
                        checkemail(email);
                    }
                }else{
                    E_mail.setInputType(InputType.TYPE_CLASS_TEXT);
                    E_mail.setText("");
                    Btn.setText("중복 확인");
                    check.setText("");
                }
            }
        });
    }
    public void checkemail(String email){
        TextView check = findViewById(R.id.checkemail);
        EditText emailinput = findViewById(R.id.e_mail_signup);
        Button btn = findViewById(R.id.verify);
        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
        Call<String> call = apiInterface.check_email(email);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body().equals("가능")){
                    check.setText("사용가능한 이메일입니다");
                    check.setTextColor(Color.parseColor("#00ff22"));
                    emailinput.setInputType(InputType.TYPE_NULL);
                    btn.setText("수정");
                }else{
                    check.setText("사용불가능한 이메일입니다");
                    check.setTextColor(Color.parseColor("#FF0000"));

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            final String code = credential.getSmsCode();
            if(code != null){
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Signup.this,"인증번호 발송 실패",Toast.LENGTH_SHORT).show();
            Log.e("휴대폰인증", String.valueOf(e));
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s,token);
            verficationID = s;
        }
    };

    public void send_verificationcode(String number){
        EditText num = findViewById(R.id.inputnumber);
        EditText E = findViewById(R.id.verifycode);
        Button btn = findViewById(R.id.verify_btn);
        E.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
        Linear.setVisibility(View.VISIBLE);
        num.setInputType(InputType.TYPE_NULL);
        phone_number = number;
        PhoneAuthOptions options;
        options = PhoneAuthOptions.newBuilder(mauth)
                .setPhoneNumber("+82 10"+number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    public void verifycode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verficationID,code);
        Log.e("code",code);
        IDcredential(credential);
    }

    private void IDcredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    EditText num = findViewById(R.id.verifycode);
                    num.setInputType(InputType.TYPE_NULL);
                    Toast.makeText(Signup.this,"완료",Toast.LENGTH_SHORT).show();
                    verifycode.setVisibility(View.GONE);
                    varify_Btn.setVisibility(View.GONE);
                    Linear.setVisibility(View.GONE);
                }else{
                    Toast.makeText(Signup.this,"인증번호를 다시 확인해주세요",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(Signup.this,Login.class);
                startActivity(i);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}