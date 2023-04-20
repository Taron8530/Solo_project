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

public class frag_salehistory_suc extends Fragment {
    private String nickname;
    private RecyclerView recyclerView;
    private main_adapter adapter;
    private ArrayList<item_model> list = new ArrayList<>();
    public frag_salehistory_suc(String nickname){
        this.nickname = nickname;
    }
    public frag_salehistory_suc(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_frag_salehistory_suc, container, false);
        recyclerView = root.findViewById(R.id.suc_recyclerview);
        adapter = new main_adapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.setlist(list);
        select_used();
        adapter.notifyDataSetChanged();
        Log.e("frag_salehistory",nickname);
        adapter.setOnItemClickListener(new main_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e("itemcl",list.get(position).getDetail()+" 눌림");
                Intent i = new Intent(getActivity(),used_info.class);
                i.putExtra("used_name",list.get(position).getusedname());
                i.putExtra("detail",list.get(position).getDetail());
                i.putExtra("nickname",list.get(position).getNickname());
                i.putExtra("price",list.get(position).getPrice());
                i.putExtra("image_names",list.get(position).getImage_names());
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
        Call<ArrayList<item_model>> call = apiInterface.used_sold_out_select(nickname);
        call.enqueue(new Callback<ArrayList<item_model>>() {
            @Override
            public void onResponse(Call<ArrayList<item_model>> call, Response<ArrayList<item_model>> response) {
                if(response.body() != null){
                    onGetResult(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<item_model>> call, Throwable t) {
                Log.e("onFailure",t.toString());
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