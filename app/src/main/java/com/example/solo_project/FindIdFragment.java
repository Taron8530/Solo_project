package com.example.solo_project;

import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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

public class FindIdFragment extends Fragment {
    private View root;
    private Button sendCode;
    private Button verifyCode;
    private EditText putNumber;
    private EditText putCode;
    private Button findEmail;
    private TextView comment;
    private TextView showEmail;
    private FirebaseAuth mauth;
    private String phone_number;
    private String verficationID;
    private boolean check = false;
    private String verifyPhoneNumber;
    private String TAG = "FindEmailFragment";
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
        root = inflater.inflate(R.layout.fragment_find_id, container, false);
        init_view();
        return root;
    }
    public void init_view(){
        sendCode = root.findViewById(R.id.send_code);
        verifyCode = root.findViewById(R.id.CertificationBtn);
        putNumber = root.findViewById(R.id.put_number);
        putCode = root.findViewById(R.id.putCertificationNumber);
        findEmail = root.findViewById(R.id.find_email);
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
        findEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check){
                    ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
                    Call<Signup_model> call = apiInterface.getEmail(putNumber.getText().toString().replaceAll("-","").substring(3));
                    call.enqueue(new Callback<Signup_model>() {
                        @Override
                        public void onResponse(Call<Signup_model> call, Response<Signup_model> response) {
                            if(response.isSuccessful()){
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("이메일 찾기");
                                if (response.body().getE_mail().trim().equals("")){
                                    builder.setMessage("이 전화번호로 가입된 이메일이 없습니다.");
                                }else{
                                    builder.setMessage("이 번호로 가입된 이메일 입니다. \n" +response.body().getE_mail());
                                }
                                builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().finish();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Signup_model> call, Throwable t) {
                            Log.d(TAG, "onFailure: "+t);
                        }
                    });
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"전화번호 인증을 먼저 해주세요!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void send_verificationcode(String number){
        putCode.setVisibility(View.VISIBLE);
        verifyCode.setVisibility(View.VISIBLE);
        putCode.setInputType(InputType.TYPE_NULL);
        phone_number = number;
        PhoneAuthOptions options;
        options = PhoneAuthOptions.newBuilder(mauth)
                .setPhoneNumber("+82 10"+number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(getActivity())
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
                    Toast.makeText(getActivity().getApplicationContext(),"완료",Toast.LENGTH_SHORT).show();
                    putCode.setVisibility(View.GONE);
                    verifyCode.setVisibility(View.GONE);
                    check = true;
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"인증번호를 다시 확인해주세요",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}