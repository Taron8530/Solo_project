package com.example.solo_project;


import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class main_adapter extends RecyclerView.Adapter<main_adapter.ViewHolder> {
    String TAG = "main_adapter";
    ArrayList<item_model> lists;


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private OnItemClickListener mListener = null ;
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener ;
    }
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public main_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);
        Log.e(TAG, "onCreateViewHolder: 호출됨" );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull main_adapter.ViewHolder holder, int position) {
        holder.onbind(lists.get(position));
        Log.e(TAG, "onBindViewHolder: 호충");
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
    public void setlist(ArrayList<item_model> list){
        lists = list;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView price;
        TextView used_item;
        TextView nickname;
        ImageView image;
        TextView sold_out;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.e(TAG, "ViewHolder: 호출됨" );
            price = itemView.findViewById(R.id.price);
            used_item = itemView.findViewById(R.id.used_name);
            nickname = itemView.findViewById(R.id.r_nickname);
            image = itemView.findViewById(R.id.used_image);
            sold_out = itemView.findViewById(R.id.sold_out);

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

        public void onbind(item_model item) {
            Log.e(TAG, "onbind: 호출됨");
            Log.e(TAG,"이미지 이름 확인"+item.getImage_names());
            Log.e("sold_out",item.getSold_out());
            price.setText(comma_to_int(item.getPrice())+"원");
            used_item.setText(item.getusedname());
            nickname.setText("판매자: "+item.getNickname());
            Log.e("adapter", String.valueOf(item.getImage_size()));
            String image_name = "";
            if(item.getImage_names() != null && item.getImage_names().size() > 0){
                image_name = item.getImage_names().get(0);
            }
            Glide.with(itemView).load("http://35.166.40.164/used_image/"+item.getNum()+"/"+image_name).override(100, 100).error(R.drawable.app_icon).into(image);
            if(item.getSold_out().equals("1")){
                sold_out.setVisibility(View.VISIBLE);
                used_item.setPaintFlags(used_item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //취소선
            }
        }
        public String comma_to_int(String number){
            if (number.length() == 0) {
                return "";
            }
            long value = Long.parseLong(number);
            DecimalFormat df = new DecimalFormat("###,###");
            String money = df.format(value);
            return money;
        }
    }
}
