package com.example.solo_project;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class chat_room_adapter extends RecyclerView.Adapter<chat_room_adapter.ViewHolder> {
    String TAG = "main_adapter";
    ArrayList<chat_room_item> lists;
    private chat_room_adapter.OnItemClickListener mListener = null ;

    public void setOnItemClickListener(chat_room_adapter.OnItemClickListener listener) {
        mListener = listener ;
    }
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    @NonNull
    @Override
    public chat_room_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_roon_item, parent, false);
        Log.e(TAG, "onCreateViewHolder: 호출됨 뷰 생성 반환" );
        return new chat_room_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull chat_room_adapter.ViewHolder holder, int position) {
        holder.onbind(lists.get(position));
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
    public void setLists(ArrayList<chat_room_item> list){
        lists = list;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nickname;
        private ImageView profile;
        private TextView last_msg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.e(TAG, "ViewHolder: 호출됨" );
            nickname = itemView.findViewById(R.id.chat_room_nickname);
            profile = itemView.findViewById(R.id.chat_room_profile);
            last_msg = itemView.findViewById(R.id.chat_room_last_msg);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(view,position);
                    }
                }
            });
        }

        public void onbind(chat_room_item item) {
            Log.e(TAG, "onbind: 호출됨");
            last_msg.setText(item.getLast_msg());
            nickname.setText(item.getNickname());;
            Log.e("이미지",item.getNickname());
            Glide.with(itemView).load("http://35.166.40.164/profile/"+item.getNickname()+".png").override(100, 100).circleCrop().error(R.drawable.app_icon).into(profile);
        }
    }
}
