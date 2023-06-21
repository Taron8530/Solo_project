package com.example.solo_project;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class main_adapter extends RecyclerView.Adapter<main_adapter.ViewHolder> {
    String TAG = "main_adapter";
    ArrayList<item_model> lists;
    Context context;
    // 시간
    public int SEC = 60;
    public int MIN = 60;
    public int HOUR = 24;
    public int DAY = 30;
    public  int MONTH = 12;
    public main_adapter(Context context){
        this.context = context;
    }
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

    public String comma_to_int(String number){
        if (number.length() == 0) {
            return "";
        }
        long value = Long.parseLong(number);
        DecimalFormat df = new DecimalFormat("###,###");
        String money = df.format(value);
        return money;
    }

    public void onBindViewHolder(@NonNull main_adapter.ViewHolder holder, int position) {
        item_model item = lists.get(position);
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(item.getDate());
            holder.price.setText(comma_to_int(item.getPrice())+"원");
            holder.used_item.setText(item.getusedname());
            holder.nickname.setText("판매자: "+item.getNickname());
            holder.times.setText(formatTimeString(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Log.e("adapter", String.valueOf(item.getImage_size()));
        String image_name = "";
        if(item.getImage_names() != null && item.getImage_names().size() > 0) {
            image_name = item.getImage_names().get(0);
        }
        Glide.with(holder.itemView)
                .load("http://35.166.40.164/used_image/"+item.getNum()+"/"+image_name)
                .override(100, 100)
                .error(R.drawable.app_icon)
                .into(holder.image);

        if(lists.get(position).getSold_out().equals("1")) {
            holder.sold_out.setVisibility(View.VISIBLE);
            holder.used_item.setPaintFlags(holder.used_item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //취소선
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGray));
        } else {
            // 상태를 기본 상태로 설정
            holder.sold_out.setVisibility(View.GONE);
            holder.used_item.setPaintFlags(holder.used_item.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); //취소선 제거
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        Log.e(TAG, "onBindViewHolder: 호충");
    }
    public  String formatTimeString(Date tempDate) {
        long curTime = System.currentTimeMillis();
        long regTime = tempDate.getTime();
        long diffTime = (curTime - regTime) / 1000;
        String msg = null;
        if (diffTime < SEC) {
            msg = "방금 전";
        } else if ((diffTime /= SEC) < MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= MIN) < HOUR) {
            msg = (diffTime / 12) + "시간 전";
        } else if ((diffTime /= HOUR) < DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= DAY) < MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime / 12 ) + "년 전";
        }
        return msg;
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
        TextView times;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.e(TAG, "ViewHolder: 호출됨" );
            price = itemView.findViewById(R.id.price);
            used_item = itemView.findViewById(R.id.used_name);
            nickname = itemView.findViewById(R.id.r_nickname);
            image = itemView.findViewById(R.id.used_image);
            sold_out = itemView.findViewById(R.id.sold_out);
            layout = itemView.findViewById(R.id.used_item);
            times = itemView.findViewById(R.id.times);

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
//        public void onbind(item_model item) {
//            Log.e(TAG, "onbind: 호출됨");
//            Log.e(TAG,"이미지 이름 확인"+item.getImage_names());
//            Log.e("sold_out",item.getSold_out());
//            price.setText(comma_to_int(item.getPrice())+"원");
//            used_item.setText(item.getusedname());
//            nickname.setText("판매자: "+item.getNickname());
//            Log.e("adapter", String.valueOf(item.getImage_size()));
//            String image_name = "";
//            if(item.getImage_names() != null && item.getImage_names().size() > 0){
//                image_name = item.getImage_names().get(0);
//            }
//            Glide.with(itemView).load("http://35.166.40.164/used_image/"+item.getNum()+"/"+image_name).override(100, 100).error(R.drawable.app_icon).into(image);
////            if(item.getSold_out().equals("1")){
////                sold_out.setVisibility(View.VISIBLE);
////                used_item.setPaintFlags(used_item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //취소선
////                layout.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGray));
////            }
//        }
    }
}
