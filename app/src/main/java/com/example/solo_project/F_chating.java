package com.example.solo_project;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class F_chating extends Fragment {
    private String nickname; //닉네임
    private ArrayList<chat_room_item> list;
    private RecyclerView recyclerView;
    private chat_room_adapter adapter;
    public F_chating(String nickname){
        this.nickname = nickname;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Log.e(TAG,nickname);
        View root = inflater.inflate(R.layout.fragment_f_chating, container, false); // 뷰 변수
        recyclerView = root.findViewById(R.id.chat_room_recyclerview);
        adapter = new chat_room_adapter();
        adapter.setLists(list);
        return root;
    }
}