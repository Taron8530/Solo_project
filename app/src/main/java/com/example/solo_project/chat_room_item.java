package com.example.solo_project;

public class chat_room_item {
    private String room_num;
    private String last_msg;
    private String room_name;
    private int msg_count;
    public chat_room_item(String room_num,String room_name,String last_msg,int msg_count){
        this.room_num = room_num;
        this.room_name = room_name;
        this.last_msg = last_msg;
        this.msg_count = msg_count;
    }

    public String getLast_msg() {
        return last_msg;
    }
    public int getMsg_count(){return msg_count;}
    public String getRoom_name() {
        return room_name;
    }
    public String getRoom_num(){return room_num;}

}
