package com.example.solo_project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class chat_promise extends AppCompatActivity {
    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };
    private Button submit;
    private DatePickerDialog datePickerDialog;
    private String TAG = "chat_promise";

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart 호출");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume 호출");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_promise);
        submit = findViewById(R.id.promise_submit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EditText et_Date = (EditText) findViewById(R.id.promise_date);
        EditText et_time = (EditText) findViewById(R.id.promise_time);
        long mNow;
        Date mDate;
        Date tDate;
        SimpleDateFormat mdate_format = new SimpleDateFormat("yyyy년 MM월 dd일 E요일");
        SimpleDateFormat mtime_format = new SimpleDateFormat("a h시 mm분");
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        tDate = new Date(mNow);
        et_Date.setText(mdate_format.format(mDate));
        et_time.setText(mtime_format.format(tDate));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_Date.getText().toString().trim().equals("") && et_time.getText().toString().trim().equals("")) {
                    Toast.makeText(chat_promise.this, "시간과 날짜를 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else if (et_Date.getText().toString().trim().equals("")) {
                    Toast.makeText(chat_promise.this, "날짜를 입력하지 않으셧습니다.", Toast.LENGTH_SHORT).show();
                } else if (et_time.getText().toString().trim().equals("")) {
                    Toast.makeText(chat_promise.this, "시간을 입력하지 않으셧습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(chat_promise.this, chating.class);
                    intent.putExtra("time", et_time.getText().toString());
                    intent.putExtra("date", et_Date.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });
        et_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(chat_promise.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(chat_promise.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String state = "오전";
                        // 선택한 시간이 12를 넘을경우 "PM"으로 변경 및 -12시간하여 출력 (ex : PM 6시 30분)
                        if (selectedHour > 12) {
                            selectedHour -= 12;
                            state = "오후";
                        }
                        // EditText에 출력할 형식 지정
                        et_time.setText(state + " " + selectedHour + "시 " + selectedMinute + "분");
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 TimePicker 출현
                mTimePicker.setTitle("시간");
                mTimePicker.show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "yyyy년 MM월 dd일 E요일";    // 출력형식   2018/11/28
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        EditText et_date = (EditText) findViewById(R.id.promise_date);
        et_date.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
