package com.example.solo_project;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class FindPasswordFragment extends Fragment {
    private View root;
    private Button sendCode;
    private Button verifyCode;
    private EditText putNumber;
    private EditText putCode;
    private FirebaseAuth mauth;
    private String verficationID;
    private boolean check = false;
    private boolean passwordCheck = false;
    private String verifyPhoneNumber;
    private String TAG = "FindEmailFragment";
    public EditText putPassword;
    public EditText checkPassword;
    private Button submit;
    private TextView checkPass;
    private String phoneNumber;

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
            Toast.makeText(getActivity().getApplicationContext(),"인증번호 발송 실패",Toast.LENGTH_SHORT).show();
            Log.e("휴대폰인증", String.valueOf(e));
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s,token);
            verficationID = s;
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_find_password, container, false);
        // Inflate the layout for this fragment
        init_view();
        return root;
    }
    public void init_view(){
        sendCode = root.findViewById(R.id.send_code);
        verifyCode = root.findViewById(R.id.CertificationBtn);
        putNumber = root.findViewById(R.id.put_number);
        putCode = root.findViewById(R.id.putCertificationNumber);
        putPassword = root.findViewById(R.id.find_password_put_password);
        checkPassword = root.findViewById(R.id.find_password_put_password_check);
        submit = root.findViewById(R.id.find_password_submit);
        checkPass = root.findViewById(R.id.find_password_checkpass);
        mauth = FirebaseAuth.getInstance();
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPhoneNumber = putNumber.getText().toString().replaceAll("-","").substring(3);
                Log.d("SignupActivity", "onClick: 전화번호"+verifyPhoneNumber);
                if(TextUtils.isEmpty(verifyPhoneNumber)){
                    Toast T = Toast.makeText(getActivity().getApplicationContext(),"번호를 입력해주세요",Toast.LENGTH_SHORT);
                    T.show();
                }else if(verifyPhoneNumber.length() > 11){
                    Toast.makeText(getActivity().getApplicationContext(),"제대로 된 번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    send_verificationcode(verifyPhoneNumber);
                }
            }
        });
        putNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(verifyCode.getText().toString())){
                    Toast.makeText(getActivity().getApplicationContext(),"인증번호를 입력해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    verifycode(putCode.getText().toString());
                }
            }
        });
        checkPassword.addTextChangedListener(new TextWatcher() {
            EditText putPassword = root.findViewById(R.id.find_password_put_password);
            TextView checkpass = root.findViewById(R.id.find_password_checkpass);

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "afterTextChanged: "+passwordCheck);
                if(TextUtils.isEmpty(putPassword.getText().toString().trim())){
                    checkpass.setText("");
                }else if(putPassword.getText().toString().trim().equals(editable.toString())){
                    checkpass.setText("두 패스워드가 일치합니다.");
                    passwordCheck = true;
                    checkpass.setTextColor(Color.parseColor("#00ff22"));
                }else{
                    passwordCheck = false;
                    checkpass.setText("두 패스워드가 일치하지 않습니다.");
                    checkpass.setTextColor(Color.parseColor("#ff0000"));
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
                Call<Signup_model> call = apiInterface.updatePassword(phoneNumber,checkPassword.getText().toString());
                call.enqueue(new Callback<Signup_model>() {
                    @Override
                    public void onResponse(Call<Signup_model> call, Response<Signup_model> response) {
                        if(response.isSuccessful()){
                            Log.d(TAG, "onResponse: "+response.body().getResponse());
                            if(response.body().getResponse().equals("ok")){
                                Toast.makeText(getActivity().getApplicationContext(),"완료",Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }else{
                                Toast.makeText(getActivity().getApplicationContext(),"잠시 후 다시 시도해주세요!.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Signup_model> call, Throwable t) {

                    }
                });
            }
        });
    }
    public void send_verificationcode(String number){
        putCode.setVisibility(View.VISIBLE);
        verifyCode.setVisibility(View.VISIBLE);
        putCode.setInputType(InputType.TYPE_NULL);
        PhoneAuthOptions options;
        phoneNumber = number;
        options = PhoneAuthOptions.newBuilder(mauth)
                .setPhoneNumber("+82 10"+number)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(getActivity())                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
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
                    Toast.makeText(getActivity().getApplicationContext(),"완료",Toast.LENGTH_SHORT).show();

                    putCode.setVisibility(View.GONE); // 인증번호 입력칸 숨기기
                    verifyCode.setVisibility(View.GONE); //인증번호 확인버튼 숨기기
                    putNumber.setVisibility(View.GONE);
                    sendCode.setVisibility(View.GONE);
                    putPassword.setVisibility(View.VISIBLE);
                    checkPassword.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.VISIBLE);
                    checkPass.setVisibility(View.VISIBLE);

                    check = true;
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"인증번호를 다시 확인해주세요",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}