package com.example.solo_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class F_chating extends Fragment {
    private String nickname; //닉네임
    private ArrayList<chat_room_item> list;
    private RecyclerView recyclerView;
    private chat_room_adapter adapter;
    private DBHelper myDb;
    private View root;
    public F_chating(String nickname){
        this.nickname = nickname;
    }
    public F_chating(){}
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Log.e(TAG,nickname);
        root = inflater.inflate(R.layout.fragment_f_chating, container, false);
        Log.e("로그 확인!","root 할당됨");// 뷰 변수
        recyclerView = root.findViewById(R.id.chat_room_recyclerview);
        Log.e("로그 확인!",String.valueOf(recyclerView));
        adapter = new chat_room_adapter();
        Log.e("로그 확인!","어뎁터 할당됨");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        recyclerView.setAdapter(adapter);
        myDb = new DBHelper(getContext());
        list_select();
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new chat_room_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e("itemcl",list.get(position).getRoom_name()+" 눌림");
                Intent i = new Intent(getActivity(),chating.class);
                i.putExtra("my_nickname",nickname);
                i.putExtra("sender",list.get(position).getRoom_name());
                i.putExtra("room_num",list.get(position).getRoom_num());
                getActivity().startActivity(i);
            }
        });
        return root;
    }
    private void list_select(){
        list = myDb.SelectAllKids();
        if(list.size() <= 0){
            recyclerView.setVisibility(View.GONE);
            TextView comment = root.findViewById(R.id.chat_empty_comment);
            comment.setVisibility(View.VISIBLE);

        }else {
            adapter.setLists(list);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        list_select();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}