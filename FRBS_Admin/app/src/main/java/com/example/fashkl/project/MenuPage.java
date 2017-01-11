package com.example.fashkl.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MenuPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        Button searchRouteBtn = (Button)findViewById(R.id.searchRouteBtn);
        Button checkTicketBtn = (Button)findViewById(R.id.checkTicketBtn);

        searchRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_01 = new Intent(getApplicationContext(), SerachRoutePage.class);
                startActivity(intent_01);
            }
        });

        checkTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_01 = new Intent(MenuPage.this, ChooseReserveTypePage.class);
                startActivity(intent_01);
            }
        });
    }//onCreate

    public void onBackPressed() {
        AlertDialog dialog = createDialogBox();
        dialog.show();

    }

    private AlertDialog createDialogBox(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MenuPage.this);

        builder.setTitle("");
        builder.setMessage("프로그램을 종료하시겠습니까?");


        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent_01 = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(intent_01);
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){

            }
        });

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
