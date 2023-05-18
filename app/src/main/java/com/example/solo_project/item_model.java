package com.example.solo_project;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class item_model{
    @Expose
    @SerializedName("nickname")private String nickname;
    @Expose
    @SerializedName("price")private String price;
    @Expose
    @SerializedName("usedname")private String usedname;
    @Expose
    @SerializedName("response") private  String response;
    @Expose
    @SerializedName("detail") private  String detail;
    @Expose
    @SerializedName("num") private  String num;
    @Expose
    @SerializedName("image_size") private  int image_size;
    @Expose
    @SerializedName("sold_out") private String sold_out;
    @Expose
    @SerializedName("image_names") private ArrayList<String> image_names;
    @Expose
    @SerializedName("date") private String date;

    item_model(String nickname,String price,String usedname,String detail,String num,int image_size,String sold_out,ArrayList<String> image_names){
        this.nickname = nickname;
        this.price = price;
        this.usedname = usedname;
        this.detail = detail;
        this.num = num;
        this.image_size = image_size;
        this.sold_out = sold_out;
        this.image_names = image_names;
    }

    public ArrayList<String> getImage_names() {
        return image_names;
    }
    public String getDate() {return date;}
    public String getNickname(){
        return nickname;
    }
    public String getusedname(){
        return usedname;
    }
    public String getPrice(){
        return price;
    }
    public String getResponse(){
        return response;
    }
    public String getDetail(){return detail;}
    public String getNum(){return num;}
    public int getImage_size(){return image_size;}
    public String getSold_out(){return sold_out;}
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public void setUsed_name(String used_name){
        this.usedname = used_name;
    }
    public void setPrice(String price){
        this.price = price;
    }
    public void setResponse(String response){
        this.response = response;
    }
    public void setDetail(String detail){this.detail = detail;}
    public void setNum(String num){
        this.num = num;
    }
    public void setImage_size(int size){
        this.image_size = size;
    }

}
