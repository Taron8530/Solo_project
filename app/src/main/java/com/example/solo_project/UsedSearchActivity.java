package com.example.solo_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsedSearchActivity extends AppCompatActivity {
    private SearchView searchView;
    RecyclerView recyclerView;
    main_adapter adapter;
    ArrayList<item_model> list = new ArrayList<>();
    TextView search_except;
    String nickname;
    Switch aSwitch;
    String TAG = "UsedSearchActivity";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("검색");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_used_search);
        searchView = findViewById(R.id.used_search_widget);
        searchView.setIconified(false);
        search_except = findViewById(R.id.search_except);
        aSwitch = findViewById(R.id.filter_item);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ArrayList<item_model> remove_list = new ArrayList<>();
                if(b) {
                    if (searchView.getQuery().toString().trim().length() > 0) {
                        for (int i = 0;list.size() > i;i++) {
                            if (list.get(i).getSold_out().equals("1")) {
                                remove_list.add(list.get(i));
                                Log.d(TAG, "onCheckedChanged: "+remove_list);
//                                list.remove(list.get(i));
                            }
                        }
                        list.removeAll(remove_list);
                        adapter.notifyDataSetChanged();
                        if (list.size() <= 0) {
                            recyclerView.setVisibility(View.GONE);
                            search_except.setVisibility(View.VISIBLE);
                        }
                    }else{
                        Toast.makeText(UsedSearchActivity.this,"검색어를 입력해주세요!",Toast.LENGTH_SHORT).show();
                        aSwitch.setChecked(false);
                    }
                }else{
                    select_used(String.valueOf(searchView.getQuery()));
                }

            }
        });
        setRecyclerView();
        nickname = getIntent().getStringExtra("nickname");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setTitle(query);
                select_used(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    public void setRecyclerView(){
        recyclerView = findViewById(R.id.search_item_recyclerview);
        adapter = new main_adapter(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));
        recyclerView.setAdapter(adapter);
        adapter.setlist(list);
        adapter.setOnItemClickListener(new main_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e("itemcl",list.get(position).getDetail()+" 눌림");
                Intent i = new Intent(UsedSearchActivity.this,used_info.class);
                i.putExtra("used_name",list.get(position).getusedname());
                i.putExtra("detail",list.get(position).getDetail());
                i.putExtra("nickname",list.get(position).getNickname());
                i.putExtra("price",list.get(position).getPrice());
                i.putExtra("image_size",list.get(position).getImage_size());
                i.putExtra("num",list.get(position).getNum());
                i.putExtra("my_nickname",nickname);
                i.putExtra("image_names",list.get(position).getImage_names());
                startActivity(i);
            }
        });

    }
    private void select_used(String comment)
    {
        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<item_model>> call = apiInterface.used_search(comment);
        call.enqueue(new Callback<ArrayList<item_model>>() {
            @Override
            public void onResponse(Call<ArrayList<item_model>> call, Response<ArrayList<item_model>> response) {
                if(response.body() != null){
                    if(response.body().size() <= 0){
                        recyclerView.setVisibility(View.GONE);
                        search_except.setVisibility(View.VISIBLE);
                    }else{
                        recyclerView.setVisibility(View.VISIBLE);
                        search_except.setVisibility(View.GONE);
                        ArrayList<item_model> list = response.body();
                        Collections.sort(list,new Used_Item_Comparator());

                        onGetResult(list);
                    }

                }
            }

            @Override
            public void onFailure(Call<ArrayList<item_model>> call, Throwable t) {
                Log.e("에러 에러", String.valueOf(t));
            }
        });
    }
    private void onGetResult(ArrayList<item_model> lists)
    {
        list = lists;
        adapter.setlist(list);
        Log.e("접근 완료",list.toString());
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
class Used_Item_Comparator implements Comparator<item_model> {
    @Override
    public int compare(item_model f1, item_model f2) {
        return f1.getSold_out().compareTo(f2.getSold_out());
    }
}