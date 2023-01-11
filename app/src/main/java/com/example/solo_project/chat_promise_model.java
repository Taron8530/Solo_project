package com.example.solo_project;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class chat_promise_model {
    @Expose
    @SerializedName("promise_time")private String promise_time;
    @Expose
    @SerializedName("promise_date")private String promise_date;
    @Expose
    @SerializedName("nickname")private String nickname;
    @Expose
    @SerializedName("time")private String time;
    @Expose
    @SerializedName("room_num")private int room_num;
    @Expose
    @SerializedName("response")private String response;

    public String getPromise_time(){
        return promise_time;
    }
    public String getResponse(){
        return response;
    }
    public String getPromise_date(){
        return promise_date;
    }
    public String getNickname(){
        return nickname;
    }
    public String getTime(){
        return time;
    }
    public int getRoom_num(){
        return room_num;
    }
    public void setResponse(String response){this.response = response;}
    public void setTime(String time){
        this.time = time;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public void setPromise_date(String promise_date){
        this.promise_date = promise_date;
    }
    public void setPromise_time(String promise_time){
        this.promise_time = promise_time;
    }
    public void setRoom_num(int room_num){this.room_num = room_num;}

}
