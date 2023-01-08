package com.example.solo_project;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.*;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "chat_db";
    public final String TABLE_NAME_chat_room = "chat_room";
    private final String COLUMN_ROOM_NUM = "room_num";
    private final String COLUMN_ROOM_NAME = "room_name";
    private final String COLUMN_USER_1 = "user1";
    private final String COLUMN_USER_2 = "user2";
    private final String COLUMN_MSG_COUNT = "msg_count";
    private Context context;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+TABLE_NAME_chat_room+ "(room_num int primary key ,room_name text,last_msg text,user1 text,user2 text,msg_count int,last_msg_time timestamp)");
        Log.e("dbHelper","onCreate 들어옴");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+TABLE_NAME_chat_room);
        onCreate(sqLiteDatabase);
    }
    public void insert_data(int room_num,String room_name,String user1,String user2,int msg_count){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ROOM_NUM, room_num);
        cv.put(COLUMN_ROOM_NAME, room_name);
        cv.put(COLUMN_USER_1, user1);
        cv.put(COLUMN_USER_2, user2);
        cv.put(COLUMN_MSG_COUNT,msg_count);

        long result = db.insert(TABLE_NAME_chat_room, null, cv);
        if (result == -1)
        {
            Log.e("db 저장","성공");
//            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Log.e("db 저장","실패");
//            Toast.makeText(context, "데이터 추가 성공", Toast.LENGTH_SHORT).show();
        }
    }
    public void last_msg_update(int room_num,String last_msg,String time){
//        DBHelper db = new DBHelper(context.getApplicationContext());
//        SQLiteDatabase dbs = db.getWritableDatabase() ;
        String UPDATE_QUERY = "UPDATE "+TABLE_NAME_chat_room+" SET last_msg ='"+last_msg+"', last_msg_time =" + "'"+time+"' WHERE room_num ="+room_num;
        getWritableDatabase().execSQL(UPDATE_QUERY);
    }
    public void msg_count_update(int room_num){
        String UPDATE_QUERY = "UPDATE "+TABLE_NAME_chat_room+" SET msg_count = msg_count + 1 "+" WHERE room_num ="+room_num;
        getWritableDatabase().execSQL(UPDATE_QUERY);
        Log.e("msg_Count_update","진입");
    }
    public void msg_count_reset(int room_num){
        String UPDATE_QUERY = "UPDATE "+TABLE_NAME_chat_room+" SET msg_count = 0 WHERE room_num = "+room_num;
        getWritableDatabase().execSQL(UPDATE_QUERY);
    }
    public boolean check_room(int room_num){
        String SELECT_QUERY = "SELECT * FROM "+TABLE_NAME_chat_room+" WHERE room_num ="+room_num;
        Cursor cur = getReadableDatabase().rawQuery(SELECT_QUERY,null);
        Log.e("check_room",String.valueOf(cur.getCount()));
        if(cur.moveToFirst()){
            Log.e("check_room","false");
            return false;
        }else{
            Log.e("check_room","true");
            return true;
        }
    }

    public ArrayList<chat_room_item> SelectAllKids() {
        ArrayList<chat_room_item> list = new ArrayList<>();
        String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME_chat_room + " ORDER BY last_msg_time DESC";

        Cursor cur = getWritableDatabase().rawQuery(SELECT_QUERY, null);

        if (cur != null && cur.moveToFirst()) {

            do {
                Log.e("테스트",cur.getString(0) + " , " + cur.getString(1) + " , " + cur.getString(3)+" , " + cur.getString(4)+" , " + cur.getString(2)+" , " + cur.getString(5)+" 시간: , " + cur.getString(6));
                list.add(new chat_room_item(cur.getString(0),cur.getString(1),cur.getString(2),Integer.parseInt(cur.getString(5))));
            } while (cur.moveToNext());

        }
        return list;
    }

}
