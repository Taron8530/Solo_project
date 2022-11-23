package com.example.solo_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class chat_data_db_Helper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "chat_dbs";
    public static final String TABLE_NAME_chat_data = "chat_data";
    private static final String COLUMN_ROOM_NUM = "room_num";
    private static final String COLUMN_sender = "nickname1";
    private static final String COLUMN_receiver = "nickname2";
    private static final String COLUMN_massage = "massage";
    private static final String COLUMN_time = "time";
    private static final String COLUMN_view_type = "view_type";
    private Context context;
    public chat_data_db_Helper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+TABLE_NAME_chat_data+ "(id interger primary key,room_num int ,sender text,receiver text,massage text,time text,view_type int)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+TABLE_NAME_chat_data);
        onCreate(sqLiteDatabase);
    }
    public void insert_data(String room_num,String nickname2,String massage,String time,int view_type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ROOM_NUM, Integer.parseInt(room_num));
        cv.put(COLUMN_receiver, nickname2);
        cv.put(COLUMN_massage, massage);
        cv.put(COLUMN_time, time);
        cv.put(COLUMN_view_type, view_type);

        long result = db.insert(TABLE_NAME_chat_data, null, cv);
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
    public void SelectAllKids(int room_num) {
        ArrayList<chat_room_item> list = new ArrayList<>();
        String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME_chat_data +" WHERE room_num = "+ room_num;
        Log.e("테스트","db_select 진입");
        Cursor cur = getWritableDatabase().rawQuery(SELECT_QUERY, null);
        Log.e("테스트","db_select 진입");
        if (cur != null && cur.moveToFirst()) {

            do {
                Log.e("테스트","db_select 진입1");
                Log.e("테스트",cur.getString(0) );
//                + " , " + cur.getString(1) + " , " + cur.getString(3)+" , " + cur.getString(4)


//                list.add(new chat_item(Integer.parseInt(cur.getString(0)),cur.getString(1),""));
            } while (cur.moveToNext());

        }
//        return list;
    }
}
