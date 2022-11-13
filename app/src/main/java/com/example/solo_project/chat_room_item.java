package com.example.solo_project;

public class chat_room_item {
    private String last_msg;
    private String nickname;
    public chat_room_item(String nickname,String last_msg){
        this.nickname = nickname;
        this.last_msg = last_msg;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public String getNickname() {
        return nickname;
    }

}
