package com.example.solo_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class F_home extends Fragment {

    RecyclerView recyclerView;
    main_adapter adapter;
    ArrayList<item_model> list = new ArrayList<>();
    View root;
    String nickname;
    public F_home(String nickname){
        this.nickname = nickname;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_f_home, container, false);
        // Inflate the layout for this fragment
        recyclerView = root.findViewById(R.id.home_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));

        adapter = new main_adapter();
        recyclerView.setAdapter(adapter);
        adapter.setlist(list);
        select_used();
        adapter.notifyDataSetChanged();
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
                getActivity().startActivity(i);
            }
        });
        return root;
    }
    private void select_used()
    {
        ApiInterface apiInterface = Apiclient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<item_model>> call = apiInterface.select_used();
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
        list = lists;
        adapter.setlist(list);
        Log.e("접근 완료",list.toString());
        Log.e("접근 완료",lists.toString());
        adapter.notifyDataSetChanged();
    }
}