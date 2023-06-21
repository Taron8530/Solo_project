package com.example.solo_project;


        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.viewpager.widget.ViewPager;
        import androidx.viewpager2.widget.ViewPager2;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.Gravity;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.TextView;
        import android.widget.Toolbar;

        import com.google.android.material.tabs.TabLayout;
        import com.google.android.material.tabs.TabLayoutMediator;


        import org.w3c.dom.Text;

        import java.util.Arrays;
        import java.util.List;

public class FIndPasswordActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    private history_content_adapter history_content_adapter;

    private FindIdFragment findIdFragment;

    private FindPasswordFragment findPasswordFragment;
    private String nickname = "테스트 유저";
    private TabLayout tabLayout;
    private TabLayout tl;
    final List<String> tabel = Arrays.asList("이메일 찾기","비밀번호 찾기");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("이메일 비밀번호 찾기");
        setContentView(R.layout.activity_find_password);

        createFragment();

        createViewpager();

        settingTabLayout();

    }


    private void createFragment() {
        findIdFragment = new FindIdFragment();

        findPasswordFragment = new FindPasswordFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void createViewpager() {

        viewPager = (ViewPager2) findViewById(R.id.find_pw_viewpager_control);



        tl = (TabLayout) findViewById(R.id.find_pw_tablayout_control);

        history_content_adapter = new history_content_adapter(getSupportFragmentManager(), getLifecycle());

        history_content_adapter.addFragment(findIdFragment);

        history_content_adapter.addFragment(findPasswordFragment);

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

        tabLayout = (TabLayout) findViewById(R.id.find_pw_tablayout_control);

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
