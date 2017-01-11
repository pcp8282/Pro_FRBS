package com.example.fashkl.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ChooseReserveTypePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_reserve_type_page);

        Button searchRouteBtn = (Button)findViewById(R.id.searchRouteBtn);
        Button checkTicketBtn = (Button)findViewById(R.id.checkTicketBtn);

        searchRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_01 = new Intent(getApplicationContext(), CheckReserveUserTicketPage.class);
                startActivity(intent_01);
            }
        });

        checkTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_01 = new Intent(ChooseReserveTypePage.this, CheckReserveTicketPage.class);
                startActivity(intent_01);
            }
        });
    }
}
