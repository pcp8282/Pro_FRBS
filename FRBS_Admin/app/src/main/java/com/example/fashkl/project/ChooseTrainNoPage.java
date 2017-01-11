package com.example.fashkl.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ChooseTrainNoPage extends AppCompatActivity {

    private int train_no; //기차 호차
    private int train_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_train_no_page);

        Intent intent = getIntent();
        train_id = intent.getExtras().getInt("train_id");

        Button button = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);

        Button button5 = (Button) findViewById(R.id.button5);
        Button button6 = (Button) findViewById(R.id.button6);

        button.setOnClickListener(new View.OnClickListener() {//1호차 클릭
            @Override
            public void onClick(View v) {
                train_no = 1;
                moveChooseSeatPage();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {//2호차 클릭
            @Override
            public void onClick(View v) {
                train_no = 2;
                moveChooseSeatPage();
            }
        });


        button5.setOnClickListener(new View.OnClickListener() {//1호차 클릭
            @Override
            public void onClick(View v) {
                train_no = 1;
                moveCheckSeatPage();
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {//2호차 클릭
            @Override
            public void onClick(View v) {
                train_no = 2;
                moveCheckSeatPage();
            }
        });
    }//onCreate


    private void moveChooseSeatPage(){
        Intent intent_01 = new Intent(getApplicationContext(), ChooseSeatPage.class);
        //ChooseSeatPage액티비티 이동할때 넘길값들
        intent_01.putExtra("train_id",train_id);
        intent_01.putExtra("train_no",train_no);
        startActivity(intent_01);
    }
    private void moveCheckSeatPage(){
        Intent intent_02 = new Intent(getApplicationContext(), CheckSeatPage.class);
        //CheckSeatPage액티비티 이동할때 넘길값들
        intent_02.putExtra("train_id",train_id);
        intent_02.putExtra("train_no",train_no);
        startActivity(intent_02);
    }

}
