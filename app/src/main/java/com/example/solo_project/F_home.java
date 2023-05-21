package com.example.solo_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class F_home extends Fragment {

    RecyclerView recyclerView;
    main_adapter adapter;
    ArrayList<item_model> list = new ArrayList<>();
    View root;
    String nickname;
    int page = 1;
    ProgressBar progressBar;
    TextView search;

    private Spinner spinner;
    private ArrayAdapter<CharSequence> sort_adapter;
    public F_home(String nickname){
        this.nickname = nickname;
    }
    public F_home(){}
    @Override
    public void onStart() {
        super.onStart();
        select_used(page);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_f_home, container, false);
        // Inflate the layout for this fragment
        recyclerView = root.findViewById(R.id.home_recyclerview);
        progressBar = root.findViewById(R.id.progressbar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        Log.e("onCreateView", "닉네임은:"+nickname);
        adapter = new main_adapter(getContext());
        recyclerView.setAdapter(adapter);
        adapter.setlist(list);
        search = root.findViewById(R.id.search_icon);

        search.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          Intent i = new Intent(getActivity().getApplicationContext(),UsedSearchActivity.class);
                                          i.putExtra("nickname",nickname);
                                          startActivity(i);
                                      }
                                  }
        );

        set_sort_dropdown(); //스피너 설정
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(recyclerView.canScrollVertically(-1)){
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (lastVisibleItem == list.size() -1  && dy > 0) {
                    if(recyclerView.canScrollVertically(-1)){
                        Log.e("F_home", String.valueOf(page));
                        progressBar.setVisibility(View.VISIBLE);
                        select_used(page);
                    }
                }
            }
        });


        adapter.setOnItemClickListener(new main_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e("itemcl",list.get(position).getDetail()+" 눌림");
                Intent i = new Intent(getActivity(),used_info.class);
                i.putExtra("used_name",list.get(position).getusedname());
                i.putExtra("detail",list.get(position).getDetail());
                i.putExtra("nickname",list.get(position).getNickname());
                i.putExtra("price",list.get(position).getPrice());
                i.putExtra("image_size",list.get(position).getImage_size());
                i.putExtra("num",list.get(position).getNum());
                i.putExtra("my_nickname",nickname);
                i.putExtra("image_names",list.get(position).getImage_names());
                getActivity().startActivity(i);
            }
        });
        return root;
    }
    private void select_used(int page)
    {
        Log.e("F_home", "select_used_page: "+page );
        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<item_model>> call = apiInterface.select_used(page);
        call.enqueue(new Callback<ArrayList<item_model>>() {
            @Override
            public void onResponse(Call<ArrayList<item_model>> call, Response<ArrayList<item_model>> response) {
                if(response.body() != null){
                    onGetResult(response.body());

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
        for(int i = 0;i<lists.size();i++){
            list.add(lists.get(i));
        }
        page += 1;
        adapter.setlist(list);
        progressBar.setVisibility(View.GONE);
        Log.e("접근 완료",list.toString());
        Log.e("접근 완료",lists.toString());
        adapter.notifyDataSetChanged();
    }
    private void getList(ArrayList<item_model> lists){
        page = 2;
        list = lists;
        adapter.notifyDataSetChanged();
    }
    private void set_sort_dropdown(){
        Spinner spinner = root.findViewById(R.id.sort);

        // 스피너에 표시할 데이터 어댑터 설정
        ArrayAdapter<CharSequence> sadapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_options, android.R.layout.simple_spinner_item);

        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sadapter);

        // 선택된 아이템 핸들링
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getActivity(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                if(selectedItem.equals("최신순")){
                    Collections.sort(list,new Used_item_DateComparator());
                    adapter.notifyDataSetChanged();
                }else if(selectedItem.equals("높은가격순")){
                    Collections.sort(list,new Used_item_MaxPriceComparator());
                    adapter.notifyDataSetChanged();
                }else {
                    Collections.sort(list,new Used_item_MinPriceComparator());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택이 취소될 때 동작
            }
        });
    }
}
class Used_item_MaxPriceComparator implements Comparator<item_model> {
    @Override
    public int compare(item_model f1, item_model f2) {
        if (Integer.parseInt(f1.getPrice().replaceAll(",","").trim()) > Integer.parseInt(f2.getPrice().replaceAll(",","").trim())) {
            return -1;
        } else if (Integer.parseInt(f1.getPrice().replaceAll(",","").trim()) < Integer.parseInt(f2.getPrice().replaceAll(",","").trim())) {
            return 1;
        }
        return 0;
    }
}
class Used_item_MinPriceComparator implements Comparator<item_model> {
    @Override
    public int compare(item_model f1, item_model f2) {
        if (Integer.parseInt(f1.getPrice().replaceAll(",","").trim()) <Integer.parseInt(f2.getPrice().replaceAll(",","").trim())) {
            return -1;
        } else if (Integer.parseInt(f1.getPrice().replaceAll(",","").trim()) > Integer.parseInt(f2.getPrice().replaceAll(",","").trim())) {
            return 1;
        }
        return 0;
    }
}

class Used_item_DateComparator implements Comparator<item_model> {
    @Override
    public int compare(item_model f1, item_model f2) {
        return f2.getDate().compareTo(f1.getDate());
    }
}