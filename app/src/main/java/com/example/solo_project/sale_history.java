package com.example.solo_project;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class sale_history extends AppCompatActivity {

    private ViewPager2 viewPager;

    private history_content_adapter history_content_adapter;

    private frag_salehistory_ing frag_salehistory_ing;

    private frag_salehistory_suc frag_salehistory_suc;
    private String nickname;
    private TabLayout tabLayout;
    private TextView back;
    private TabLayout tl;
    final List<String> tabel = Arrays.asList("판매중","판매완료");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("거래 목록");
        setContentView(R.layout.activity_sale_history);
        Intent i = getIntent();
        nickname = i.getStringExtra("nickname");
        back = findViewById(R.id.sale_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        createFragment();

        createViewpager();

        settingTabLayout();

    }


    private void createFragment() {

        frag_salehistory_ing = new frag_salehistory_ing(nickname);

        frag_salehistory_suc = new frag_salehistory_suc(nickname);


    }

    private void createViewpager() {

        viewPager = (ViewPager2) findViewById(R.id.viewpager_control);



        tl = (TabLayout) findViewById(R.id.tablayout_control);

        history_content_adapter = new history_content_adapter(getSupportFragmentManager(), getLifecycle());

        history_content_adapter.addFragment(frag_salehistory_ing);

        history_content_adapter.addFragment(frag_salehistory_suc);

        viewPager.setAdapter(history_content_adapter);

        new TabLayoutMediator(tl, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView Tx = new TextView(getApplicationContext());
                Tx.setGravity(Gravity.CENTER);
                Tx.setText(tabel.get(position));
                tab.setCustomView(Tx);

            }
        }).attach();

    }

    private void settingTabLayout() {

        tabLayout = (TabLayout) findViewById(R.id.tablayout_control);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override

            public void onTabSelected(TabLayout.Tab tab) {

                int pos = tab.getPosition();

                switch (pos) {

                    case 0:

                        viewPager.setCurrentItem(0);

                        break;

                    case 1:

                        viewPager.setCurrentItem(1);

                        break;

                    case 2:

                        viewPager.setCurrentItem(2);

                        break;

                }

            }

            @Override

            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override

            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

    }
}
