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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class F_profile extends Fragment {
    TextView email_view;
    TextView nickname_view;
    String email;
    String nickname;
    ImageView imagebtn;
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
        ImageView profile = root.findViewById(R.id.f_profile);
        Log.e("onCreateView",email + " " +nickname);
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
        return root;

    }
}