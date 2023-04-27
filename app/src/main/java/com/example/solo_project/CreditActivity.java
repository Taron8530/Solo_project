package com.example.solo_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.webrtc.PeerConnectionFactory;

public class CreditActivity extends AppCompatActivity {
    Button credit_500;
    Button credit_1000;
    Button credit_5000;
    Button credit_10000;
    Button credit_50000;
    Button credit_100000;
    String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
        credit_500 =findViewById(R.id.credit_btn_1);
        credit_1000 = findViewById(R.id.credit_btn_2);
        credit_5000 = findViewById(R.id.credit_btn_3);
        credit_10000 = findViewById(R.id.credit_btn_4);
        credit_50000 = findViewById(R.id.credit_btn_5);
        credit_100000 = findViewById(R.id.credit_btn_6);
        nickname = getIntent().getStringExtra("nickname");
        Intent i = new Intent(CreditActivity.this,PaymentActivity.class);
        i.putExtra("nickname",nickname);
        credit_500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("itemname","500Credit");
                i.putExtra("total_amount","500");
                startActivity(i);
            }
        });
        credit_1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("itemname","1000Credit");
                i.putExtra("total_amount","1000");
                startActivity(i);
            }
        });
        credit_5000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("itemname","5000Credit");
                i.putExtra("total_amount","5000");
                startActivity(i);
            }
        });
        credit_10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("itemname","10000Credit");
                i.putExtra("total_amount","10000");
                startActivity(i);
            }
        });
        credit_50000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("itemname","50000Credit");
                i.putExtra("total_amount","50000");
                startActivity(i);
            }
        });
        credit_100000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i.putExtra("itemname","100000Credit");
                i.putExtra("total_amount","100000");
                startActivity(i);
            }
        });
    }
}