package com.example.solo_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "chat_db";
    public static final String TABLE_NAME = "chat_room";
    private static final String COLUMN_ROOM_NUM = "room_num";
    private static final String COLUMN_ROOM_NAME = "room_name";
    private static final String COLUMN_USER_1 = "user1";
    private static final String COLUMN_USER_2 = "user2";
    private Context context;
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+TABLE_NAME+ "(room_num interger primary key ,room_name text,last_msg text,user1 text,user2 text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    public void insert_data(int room_num,String room_name,String user1,String user2){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ROOM_NUM, room_num);
        cv.put(COLUMN_ROOM_NAME, room_name);
        cv.put(COLUMN_USER_1, user1);
        cv.put(COLUMN_USER_2, user2);

        long result = db.insert(TABLE_NAME, null, cv);
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
    public ArrayList<chat_room_item> SelectAllKids() {
        ArrayList<chat_room_item> list = new ArrayList<>();
        String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME;

        Cursor cur = getWritableDatabase().rawQuery(SELECT_QUERY, null);

        if (cur != null && cur.moveToFirst()) {

            do {
                Log.e("테스트",cur.getString(0) + " , " + cur.getString(1) + " , " + cur.getString(3)+" , " + cur.getString(4));
                list.add(new chat_room_item(Integer.parseInt(cur.getString(0)),cur.getString(1),""));
            } while (cur.moveToNext());

        }
        return list;
    }

}
