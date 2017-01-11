package com.example.fashkl.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashkl.project.config.ServerInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ChooseSeatPage extends AppCompatActivity  {

    NfcAdapter mNfcAdapter = null; // NFC 어댑터
    TextView mTextView;
    private SharedPreferences setting;
    private Button reserveBtn;

    //예매 필요 정보들
    private String id; //고객 로그인 id
    private int train_no; //기차 호차
    private int train_id;


    //좌석 관련
    private int seatMaxNum = 16;
    private String seat_no;
    private ArrayList<String> reserveSeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_seat_page);

        Intent intent = getIntent();
        train_id = intent.getExtras().getInt("train_id");
        Log.i("train_id::", String.valueOf(train_id));
        train_no = intent.getExtras().getInt("train_no");
        Log.i("train_no::", String.valueOf(train_no));
        // SharedPreferences에 저장한 로그인 ID 가져옴
        setting = getSharedPreferences("setting", 0);
        id = setting.getString("id", "");
        Log.i("로그인Id::", id);

        //메인에서  통신 위해서
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        TextView trainNum = (TextView)findViewById(R.id.Num);
        trainNum.setText(String.valueOf(train_no)+"호차");// 텍스트 뷰에 호차

        //이미 예매된 좌석 정보
        String sMessage = SendByHttp("getReserveSeat.jsp"); // 예매하기 위한 http 통신
        jsonParserSeatList(sMessage);
        for(int j=0; j<seatMaxNum; j++){

            CheckBox c = (CheckBox) findViewById(R.id.checkBox1+j);
            String seat = c.getText().toString();

            if(reserveSeats.contains(seat)){
                c.setChecked(true);
                c.setClickable(false);
                c.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.color2)));
            }
        }



        reserveBtn =(Button)findViewById(R.id.refreshBtn);
        reserveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seat_no = "";
                int cnt = 0;
                for(int i=0; i<seatMaxNum; i++){
                    // String checkBoxId = "checkBox"+String.valueOf(i); // checkBox1 , checkBox2 .....
                    CheckBox c = (CheckBox) findViewById(R.id.checkBox1+i);

                    if(c.isChecked() && !reserveSeats.contains(c.getText().toString())) {//체크되고 선택한 좌석인경우
                        seat_no += c.getText().toString();
                        cnt++;
                    }
                }//for
                Log.i("예약할 좌석::",seat_no);

                if(cnt == 1){//한자리 선택할 경우만 예약되게
                    AlertDialog dialog = createDialogBox();
                    dialog.show();
                }else
                    Toast.makeText(getApplicationContext(), "좌석 한칸 선택해주세요", Toast.LENGTH_SHORT).show();




            }
        });
    }//onCreate

    private AlertDialog createDialogBox(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("좌석 선택 확인");
        builder.setMessage(seat_no+"좌석으로 정말 예매하시겠습니까?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sMessage = SendByHttp("reserve.jsp"); // 예매하기 위한 http 통신
                String result = jsonParserList(sMessage);
                handlingLogin(result); // 예약 처리
            }
        });
        builder.setNegativeButton("No",null);

        AlertDialog dialog = builder.create();
        return dialog;

    }

    private String SendByHttp(String url ) {// http통신

        DefaultHttpClient client = new DefaultHttpClient();//HttpClient 통신을 합니다.

        try {

            String URL = ServerInfo.SERVER_URL +url; // 서버 주소에 연결할 jsp파일 명 붙임
            Log.i("연결url::",URL);

            HttpPost post =  null;

            if(url.equals("reserve.jsp")) {
                post = new HttpPost(URL + "?id=" + id + "&train_id=" + train_id
                        + "&train_no=" + train_no + "&seat_no=" + seat_no); // 예매 정보들 파라미터로
            }
            else if(url.equals("getReserveSeat.jsp")) {// 예매된 좌석 가져오기 위해
                post = new HttpPost(URL +"?train_id=" + train_id + "&train_no=" + train_no );
            }
            HttpResponse response = client.execute(post); //데이터를 보내고 바로 데이터 응답을 받습니다.
            //받아온 데이터를 buffer에 넣습니다.
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

            String line = null;
            String result = "";

            while ((line = bufreader.readLine()) != null) {//buffer를 읽어와서 result에 넣습니다.
                result += line;
            }

            Log.i("sendBy Http:", result);
            return result;

        } catch (Exception e) {//예외처리
            e.printStackTrace();
            client.getConnectionManager().shutdown();
            return "";
        }
    }
    public String jsonParserList(String pRecvServerPage) {//받아온 데이터를 파싱하는 부분입니다.

        try {

            JSONObject json = new JSONObject(pRecvServerPage);
            String result = json.getString("result");
            Log.i("반환값 ::", result);
            return result;
            //잘 파싱된 데이터를 넘깁니다.
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void jsonParserSeatList(String pRecvServerPage) {//받아온 데이터를 JSON 파싱

        try {
            JSONArray jArr = new JSONArray(pRecvServerPage);
            reserveSeats = new ArrayList<String>();
            for(int i=0; i<jArr.length(); i++){
                reserveSeats.add(jArr.getJSONObject(i).getString("seat_no")) ;
                Log.i("예매좌석::",reserveSeats.get(i));
            }

            //잘 파싱된 데이터를 넘깁니다.
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }


    public void handlingLogin(String result) {

        if (result.equals("reserve_ok")) {

            Toast.makeText(this, "예약 되었습니다", Toast.LENGTH_SHORT).show();

            Intent intent_01 = new Intent(getApplicationContext(), CheckReserveTicketPage.class);
            startActivity(intent_01);
            finish();


        } else if (result.equals("reserve_fail")) {
            Toast toast = Toast.makeText(this, "예약 실패했습니다", Toast.LENGTH_SHORT);
            toast.show();
        } else if (result.equals("connect_fail") || result.equals("")) {
            Toast toast = Toast.makeText(this, "서버 연결 실패했습니다", Toast.LENGTH_SHORT);
            toast.show();
        }
    }




}
