package com.example.solo_project;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<chat_item> myDataList = null;
    private Context context;

    MyAdapter(ArrayList<chat_item> dataList)
    {
        myDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == 0)//상대가 이미지 보낼때
        {
            view = inflater.inflate(R.layout.left_image_chat, parent, false);
            return new Left_image_chat(view);
        }
        else if(viewType == 1)//상대 메세지 보낼때
        {
            view = inflater.inflate(R.layout.left_chat, parent, false);
            return new LeftViewHolder(view);
        }else if(viewType == 2){ //내가 채팅 보낼때
            view = inflater.inflate(R.layout.right_chat, parent, false);
            return new RightViewHolder(view);
        }
        else //내가 이미지 보낼때
        {
            Log.e("Myadapter","여기 들어옴");
            view = inflater.inflate(R.layout.right_image_chat,parent,false);
            return new Right_image_chat(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        if(viewHolder instanceof Left_image_chat)
        {
//            ((Left_image_chat) viewHolder).content.setText(myDataList.get(position).getContent());
        }
        else if(viewHolder instanceof LeftViewHolder)
        {
            ((LeftViewHolder) viewHolder).name.setText(myDataList.get(position).getName());
            ((LeftViewHolder) viewHolder).content.setText(myDataList.get(position).getContent());
            ((LeftViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
            Glide.with(((LeftViewHolder) viewHolder).itemView).load("http://35.166.40.164/profile/"+myDataList.get(position).getName()+".png").override(100, 100).error(R.drawable.app_icon).circleCrop().into(((LeftViewHolder) viewHolder).image);
        }
        else if(viewHolder instanceof RightViewHolder)
        {
            ((RightViewHolder) viewHolder).content.setText(myDataList.get(position).getContent());
            ((RightViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
        }else if(viewHolder instanceof Right_image_chat){
            Glide.with(context).load(myDataList.get(position).getUri()).into(((Right_image_chat) viewHolder).image);
            ((Right_image_chat) viewHolder).time.setText(myDataList.get(position).getTime());
        }
    }

    @Override
    public int getItemCount()
    {
        return myDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return myDataList.get(position).getViewType();
    }
    public class Right_image_chat extends RecyclerView.ViewHolder{
        ImageView image;
        TextView time;

        Right_image_chat(View itemView)
        {
            super(itemView);

            image = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
        }
    }
    public class Left_image_chat extends RecyclerView.ViewHolder{
        TextView time;
        TextView name;
        ImageView image;
        ImageView profile;


        Left_image_chat(View itemView)
        {
            super(itemView);
            time = itemView.findViewById(R.id.left_chat_time);
            name = itemView.findViewById(R.id.left_chat_name);
            image = itemView.findViewById(R.id.left_chat_image);
            profile = itemView.findViewById(R.id.left_chat_profile);
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView name;
        TextView time;
        ImageView image;

        LeftViewHolder(View itemView)
        {
            super(itemView);
            image = itemView.findViewById(R.id.chating_image);
            content = itemView.findViewById(R.id.content);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView time;

        RightViewHolder(View itemView)
        {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
        }
    }

}