package com.example.fashkl.project;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


public class SerachRoutePage extends AppCompatActivity {

    ////////////date picker////////////////
    private TextView mDateDisplay;
    private Button mPickDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    static final int DATE_DIALOG_ID = 0;
    ///////////////////////////////////////

    ////////////넘길 변수들 선언////////////
    private String departure_date;
    private String departure_station;
    private String arrival_station;
    ///////////////////////////////////////


    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDisplay();
        }

    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,mDateSetListener, mYear, mMonth,
                        mDay);
                Calendar calendar = Calendar.getInstance();

                calendar.add(Calendar.DATE, 0); // Add 0 days to Calendar
                Date newDate = calendar.getTime();
                datePickerDialog.getDatePicker().setMinDate(newDate.getTime()-(newDate.getTime()%(24*60*60*1000)));
                return datePickerDialog;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_route_page);

        /////////////////// 날짜 처리 부분 ///////////////////////////////
        // 레이아웃에서 뷰 요소를 찾는다.
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        mPickDate = (Button) findViewById(R.id.pickDate);

        // 버튼에 리스너를 추가한다.
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        // 현재 날짜를 얻는다.

        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,mDateSetListener, mYear, mMonth,
                mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());


        if(mYear < c.get(Calendar.YEAR)||mMonth <c.get(Calendar.MONTH)||mDay < c.get(Calendar.DAY_OF_MONTH))
        {
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }

        // 현재 날자를 표시한다.
        updateDisplay();
        Log.i("출발날짜::",departure_date);
        ///////////////////////////////////////////////////////////////

        // spinner 출발 도착역
        Spinner departSpinner = (Spinner)findViewById(R.id.choose_departure);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Stations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        departSpinner.setAdapter(adapter);
        departSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                departure_station = parent.getItemAtPosition(position).toString();
                Log.i("출발역::",departure_station);
                /*Toast.makeText(parent.getContext(),"출발::"+parent.getItemAtPosition(position).toString()
                        , Toast.LENGTH_SHORT).show();*/
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "선택해주세요!"
                        , Toast.LENGTH_SHORT).show();
            }
        });

        Spinner arrivalSpinner = (Spinner)findViewById(R.id.choose_arrival);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.Stations, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        arrivalSpinner.setAdapter(adapter2);

        arrivalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                arrival_station = parent.getItemAtPosition(position).toString();
                Log.i("도착역::",arrival_station);
                /*Toast.makeText(parent.getContext(),parent.getItemAtPosition(position).toString()
                        , Toast.LENGTH_SHORT).show();*/
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "선택해주세요!"
                        , Toast.LENGTH_SHORT).show();
            }
        });


        /*/ 노선조회 버튼 클릭/*/
        findViewById(R.id.SearchRouteBtn).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {//SerachRouteResultPage 액티비티로 이동
                        Intent intent_01 = new Intent(getApplicationContext(), SerachRouteResultPage.class);
                        //액티비티 이동할때 넘길값들
                        intent_01.putExtra("departure_date",departure_date);
                        intent_01.putExtra("departure_station",departure_station);
                        intent_01.putExtra("arrival_station",arrival_station);
                        startActivity(intent_01);
                    }
                }
        );
    }//onCreate


    // 텍스트 뷰의 날짜를 변경한다.
    private void updateDisplay() {

        StringBuilder sb = new StringBuilder();
        sb.append(mYear).append("-").append(mMonth + 1).append("-").append(mDay); //sb에 날짜담음
        mDateDisplay.setText(sb);// 텍스트뷰 변경
        departure_date = sb.toString().trim(); // 넘길 변수에 담음음
    }

}
