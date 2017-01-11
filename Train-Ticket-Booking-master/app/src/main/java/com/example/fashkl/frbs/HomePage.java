package com.example.fashkl.frbs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fashkl.frbs.config.ServerInfo;

public class HomePage extends AppCompatActivity {

    private Button loginBtn, creditBtn;
    public Intent loginPageIntent, registerIntent;
    private Button setServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        loginBtn = (Button) findViewById(R.id.login_Button);
        creditBtn = (Button) findViewById(R.id.credits_Button);
        setServer = (Button) findViewById(R.id.setServer);
        setServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = createDialogBox();
                dialog.show();
            }
        });

        loginPageIntent = new Intent(this, LoginPage.class);
        registerIntent = new Intent(this, RegisterPage.class);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(loginPageIntent);
                finish();
            }
        });
        creditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(registerIntent);
            }
        });


    }//onCreate

    private AlertDialog createDialogBox(){

        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);

        builder.setTitle("서버 IP설정");
        builder.setMessage("192.168.43.157:8888 형식으로 입력해주세요");

        final EditText ip = new EditText(HomePage.this);
        builder.setView(ip);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                ServerInfo.SERVER_URL = "http://" +ip.getText().toString() +"/FRBS_Project/" ;
                Toast.makeText(getApplicationContext(),  ServerInfo.SERVER_URL, Toast.LENGTH_SHORT).show();
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
