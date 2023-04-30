package com.example.solo_project;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.solo_project.webrtc.Video_call_Activity;
import com.google.android.gms.common.api.Api;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class F_profile extends Fragment {
    TextView email_view;
    TextView nickname_view;
    TextView credit_View;
    String email;
    String nickname;
    String credit;
    Button imagebtn;
    Button logout;
    Button change_profile;
    Button creditActivity;
    final String TAG = "F_Profile";
    public F_profile(String nickname,String email){
        this.nickname = nickname;
        this.email = email;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("F_profile","onpause"+nickname+email);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_f_profile, container, false);
        email_view = root.findViewById(R.id.f_email);
        nickname_view = root.findViewById(R.id.f_nickname);
        imagebtn = root.findViewById(R.id.Sales_history);
        logout = root.findViewById(R.id.logout);
        change_profile = root.findViewById(R.id.change_profile);
        creditActivity =root.findViewById(R.id.credit_pay);
        ImageView profile = root.findViewById(R.id.f_profile);
        Log.e("onCreateView",email + " " +nickname);
        credit_View = root.findViewById(R.id.credit);
        getCredit();
        Glide.with(F_profile.this)
                .load("http://35.166.40.164/profile/"+nickname+".png")
                .circleCrop()
                .override(600,600)
                .error(R.drawable.app_icon)
                .into(profile);
        email_view.setText(email);
        nickname_view.setText(nickname);
        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(getActivity(),sale_history.class);
                I.putExtra("nickname",nickname);
                startActivity(I);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),Login.class);
                SharedPreferences sv = getActivity().getSharedPreferences("user_verify",Context.MODE_PRIVATE);
                SharedPreferences.Editor E = sv.edit();
                E.clear();
                E.commit();
                startActivity(i);
                getActivity().finish();
            }
        });
        change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Video_call_Activity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
        creditActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),CreditActivity.class);
                i.putExtra("nickname",nickname);
                getActivity().startActivity(i);
            }
        });
        return root;

    }
    private void getCredit(){
        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
        Call<Signup_model> call = apiInterface.getCredit(nickname);
        call.enqueue(new Callback<Signup_model>() {
            @Override
            public void onResponse(Call<Signup_model> call, Response<Signup_model> response) {
                Log.e("프로필 프래그먼트 Credit","요청함" +response);

                if(response.isSuccessful()){
                    Log.e("프로필프래그먼트 Credit", String.valueOf(response.body().getCredit()));
                    credit = response.body().getCredit();
                    credit_View.setText("보유 크래딧: "+ credit +"C");
                }
            }

            @Override
            public void onFailure(Call<Signup_model> call, Throwable t) {
                Log.e("프로필프래그먼트 Credit",t.toString());
            }
        });

    }
}