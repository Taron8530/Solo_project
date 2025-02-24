package com.example.solo_project;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<chat_item> myDataList = null;
    private Context context;
    private OnItemClickListener mListener = null ;
    MyAdapter(ArrayList<chat_item> dataList)
    {
        myDataList = dataList;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }
    public interface OnItemClickListener {
        void onImageClick(View v, int position) ;
        void onMapIVew(View v,int position);
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
        else if(viewType == 3)//내가 이미지 보낼때
        {
            Log.e("Myadapter","여기 들어옴");
            view = inflater.inflate(R.layout.right_image_chat,parent,false);
            return new Right_image_chat(view);
        }
        else if(viewType == 4){ //상대가 위치를 공유했을때
            Log.e("Myadapter","여기 들어옴");
            view = inflater.inflate(R.layout.left_location_share,parent,false);
            return new Leftlocation_Share(view);
        }else if(viewType == 5){ // 약속 잡았을때
            Log.e("Myadapter","여기 들어옴");
            view = inflater.inflate(R.layout.chat_promise_item,parent,false);
            return new PromiseViewHolder(view);
        }else{ // 내가 위치를 공유했을때
            Log.e("Myadapter","여기 들어옴");
            view = inflater.inflate(R.layout.right_location_share,parent,false);
            return new Rightlocation_Share(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        if(viewHolder instanceof Left_image_chat) //상대가 이미지 보냈을때
        {
            Glide.with(context).load(myDataList.get(position).getContent()).into(((Left_image_chat) viewHolder).image);
            ((Left_image_chat) viewHolder).time.setText(myDataList.get(position).getTime());
        }
        else if(viewHolder instanceof LeftViewHolder) //상대가 채팅 보냈을때
        {
            ((LeftViewHolder) viewHolder).content.setText(myDataList.get(position).getContent());
            ((LeftViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
        }
        else if(viewHolder instanceof RightViewHolder) // 내가 채팅 보냈을때
        {
            ((RightViewHolder) viewHolder).content.setText(myDataList.get(position).getContent());
            ((RightViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
        }else if(viewHolder instanceof Right_image_chat) { //내가 이미지 보냈을때
            Glide.with(context).load(myDataList.get(position).getContent()).into(((Right_image_chat) viewHolder).image);
            ((Right_image_chat) viewHolder).time.setText(myDataList.get(position).getTime());
        }else if(viewHolder instanceof Rightlocation_Share){
            ((Rightlocation_Share) viewHolder).time.setText(myDataList.get(position).getTime());
        }else if(viewHolder instanceof Leftlocation_Share){
            ((Leftlocation_Share) viewHolder).time.setText(myDataList.get(position).getTime());
        }else if(viewHolder instanceof PromiseViewHolder){
            ((PromiseViewHolder) viewHolder).content.setText(myDataList.get(position).getContent());
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onImageClick(view, pos) ;
                        }
                    }
                }
            });
        }

    }
    public class Left_image_chat extends RecyclerView.ViewHolder{
        TextView time;
//        TextView name;
        ImageView image;


        Left_image_chat(View itemView)
        {
            super(itemView);
            time = itemView.findViewById(R.id.left_chat_time);
            image = itemView.findViewById(R.id.left_chat_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onImageClick(view, pos) ;
                        }
                    }
                }
            });
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView time;

        LeftViewHolder(View itemView)
        {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
        }
    }
    public class PromiseViewHolder extends RecyclerView.ViewHolder{
        TextView content;

        PromiseViewHolder(View itemView)
        {
            super(itemView);
            content = itemView.findViewById(R.id.chat_promise);
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
    public class Rightlocation_Share extends RecyclerView.ViewHolder{
        Button btn;
        TextView time;

        Rightlocation_Share(View itemView)
        {
            super(itemView);

            btn = itemView.findViewById(R.id.location_share_goto_map_view);
            time = itemView.findViewById(R.id.time);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onMapIVew(view, pos); ;
                        }
                    }
                }
            });
        }
    }
    public class Leftlocation_Share extends RecyclerView.ViewHolder{
        Button btn;
        TextView time;

        Leftlocation_Share(View itemView)
        {
            super(itemView);

            btn = itemView.findViewById(R.id.location_share_goto_map_view);
            time = itemView.findViewById(R.id.time);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onMapIVew(view, pos); ;
                        }
                    }
                }
            });
        }
    }

}