package com.example.solo_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class chat_data_db_Helper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "chat_dbs";
    public static final String TABLE_NAME_chat_data = "chat_data";
    private static final String COLUMN_ROOM_NUM = "room_num";
    private static final String COLUMN_receiver = "receiver";
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
    public void insert_data(String room_num,String nickname,String massage,String time,int view_type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ROOM_NUM, Integer.parseInt(room_num));
        cv.put(COLUMN_receiver, nickname);
        cv.put(COLUMN_massage, massage);
        cv.put(COLUMN_time, time);
        cv.put(COLUMN_view_type, view_type);

        long result = db.insert(TABLE_NAME_chat_data, null, cv);
        if (result == -1)
        {
            Log.e("db 저장","실패");
//            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Log.e("db 저장","성공");
//            Toast.makeText(context, "데이터 추가 성공", Toast.LENGTH_SHORT).show();
        }
    }
    public String String_extract_time(String time) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        String new_time = new SimpleDateFormat("HH:mm").format(date);
        return new_time;
    }
    public ArrayList<chat_item> SelectAllKids(int room_num) throws ParseException {
        ArrayList<chat_item> list = new ArrayList<>();
        String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME_chat_data + " WHERE room_num = " + room_num;
//        WHERE room_num = " + room_num
        Log.e("테스트","db_select 진입");
        Cursor cur = getWritableDatabase().rawQuery(SELECT_QUERY, null);
        Log.e("테스트","db_select 진입");
        if (cur != null && cur.moveToFirst()) {

            do {
                Log.e("테스트",cur.getString(1)+ " , " + cur.getString(3)+" , " + cur.getString(4)+" , " + cur.getString(5)+" , " + cur.getString(6));

                list.add(new chat_item(cur.getString(4),cur.getString(3),String_extract_time(cur.getString(5)),Integer.parseInt(cur.getString(6))));
//                list.add(new chat_item(Integer.parseInt(cur.getString(0)),cur.getString(1),""));
            } while (cur.moveToNext());

        }
        return list;
    }
}
