package basic.exam.nfcscanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import basic.exam.nfcscanner.config.ServerInfo;

public class MainActivity extends AppCompatActivity {

    private Button setServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button doorScannerBtn = (Button)findViewById(R.id.doorScanner);
        Button seatScannerBtn = (Button)findViewById(R.id.seatScanner);
        setServer = (Button) findViewById(R.id.setServer);
        setServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = createDialogBox();
                dialog.show();
            }
        });
        doorScannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_01 = new Intent(getApplicationContext(), SetDoorScannerActivity.class);
                startActivity(intent_01);
            }
        });

        seatScannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_01 = new Intent(getApplicationContext(), SetSeatScannerActivity.class);
                startActivity(intent_01);
            }
        });
    }

    private AlertDialog createDialogBox(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("서버 IP설정");
        builder.setMessage("192.168.43.157:8888 형식으로 입력해주세요");

        final EditText ip = new EditText(MainActivity.this);
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
