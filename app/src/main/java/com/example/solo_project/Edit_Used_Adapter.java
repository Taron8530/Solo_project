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

import java.util.ArrayList;

public class Edit_Used_Adapter extends RecyclerView.Adapter<Edit_Used_Adapter.ViewHolder>{
    private ArrayList<String> mData = null ;
    private Context mContext = null ;
    private main_adapter.OnItemClickListener mListener = null ;
    private String num;
    public void setOnItemClickListener(main_adapter.OnItemClickListener listener) {
        mListener = listener ;
    }
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    Edit_Used_Adapter(ArrayList<String> list, Context context,String num) {
        mData = list ;
        mContext = context;
        this.num = num;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView cancel;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조.
            image = itemView.findViewById(R.id.edit_used_multi_imageview);
            cancel = itemView.findViewById(R.id.edit_used_image_cancel);
        }
    }


    @Override
    public Edit_Used_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;    // context에서 LayoutInflater 객체를 얻는다.
        View view = inflater.inflate(R.layout.edit_used_item_recyclerview, parent, false) ;	// 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
        Edit_Used_Adapter.ViewHolder vh = new Edit_Used_Adapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(Edit_Used_Adapter.ViewHolder holder, int position) {
        Log.e("onbindViewHolder",mData.get(position).toString()+"// "+position);
        String image_uri = mData.get(position) ;
        int pos = position;
        if(image_uri.contains(".jpeg")){
            Glide.with(mContext)
                    .load("http://taron.duckdns.org/used_image/"+num+"/"+image_uri)
                    .override(500, 500)
                    .into(holder.image);
        }else{
            Glide.with(mContext)
                    .load(image_uri)
                    .override(500, 500)
                    .into(holder.image);
        }
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    if (mListener != null) {
                        mListener.onItemClick(view, pos); ;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
