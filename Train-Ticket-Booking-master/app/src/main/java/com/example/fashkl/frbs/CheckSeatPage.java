package com.example.fashkl.frbs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fashkl.frbs.config.ServerInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CheckSeatPage extends AppCompatActivity  {

    NfcAdapter mNfcAdapter = null; // NFC 어댑터
    TextView mTextView;
    private SharedPreferences setting;
    private Button refreshBtn;

    //예매 필요 정보들
    private String id; //고객 로그인 id
    private int train_no; //기차 호차
    private int train_id;


    //좌석 관련
    private int seatMaxNum = 16;
    private String seat_no;
    private ArrayList<String> reserveSeats;
    private ArrayList<String> checkedSeats; //착석 인증한 좌석
    private ArrayList<String> failedSeats; // 인증실패 좌석
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_seat_page);
        Intent intent = getIntent();

        train_id = intent.getExtras().getInt("train_id");
        Log.i("train_id::", String.valueOf(train_id));
        train_no = intent.getExtras().getInt("train_no");
        Log.i("train_no::", String.valueOf(train_no));
        // SharedPreferences에 저장한 로그인 ID 가져옴
        setting = getSharedPreferences("setting", 0);
        id = setting.getString("id", "");
        Log.i("로그인Id::", id);

        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        //imageView.setImageDrawable(drawable);
        TextView trainNum = (TextView)findViewById(R.id.Num);
        trainNum.setText(String.valueOf(train_no)+"호차");// 텍스트 뷰에 호차
        reserveSeats = new ArrayList<String>();
        checkedSeats = new ArrayList<String>();
        failedSeats =  new ArrayList<String>();
        //메인에서  통신 위해서
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //이미 예매된 좌석 정보
        String sMessage = SendByHttp("getReserveSeat.jsp"); // 예약된 좌석들 가져오기위해
        jsonParserSeatList(sMessage);


        String sMessage2 = SendByHttp("getCheckSeat.jsp"); // 인증된 좌석들 가져오기위해
        jsonParserSeatList(sMessage2);

        for(int j=0; j<seatMaxNum; j++){

            CheckBox c = (CheckBox) findViewById(R.id.checkBox1+j);
            String seat = c.getText().toString();

            if(reserveSeats.contains(seat)){//예매된 좌석 체크하고 클릭 안되게
                c.setChecked(true);
                c.setClickable(false);
                c.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.color2)));
                if(checkedSeats.contains(seat)){// 착석 인증도 된 좌석인 경우
                    //c.setTextColor(getResources().getColor(R.color.color6));
                    c.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.color6)));
                }else if(failedSeats.contains(seat)){// 착석 인증도 된 좌석인 경우
                    //c.setTextColor(getResources().getColor(R.color.color6));
                    c.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                }
            }
        }//for

        refreshBtn =(Button)findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {//새로고침 버튼 클릭
            @Override
            public void onClick(View v) {
                reserveSeats = new ArrayList<String>();
                checkedSeats = new ArrayList<String>();
                failedSeats = new ArrayList<String>();
                ///////////////////이미 예매된 좌석 정보///////////////////
                String sMessage = SendByHttp("getReserveSeat.jsp"); // 예약된 좌석들 가져오기위해
                jsonParserSeatList(sMessage);
                String sMessage2 = SendByHttp("getCheckSeat.jsp"); // 인증된 좌석들 가져오기위해
                jsonParserSeatList(sMessage2);
                for(int j=0; j<seatMaxNum; j++){

                    CheckBox c = (CheckBox) findViewById(R.id.checkBox1+j);
                    String seat = c.getText().toString();

                    if(reserveSeats.contains(seat)){//예매된 좌석 체크하고 클릭 안되게
                        c.setChecked(true);
                        c.setClickable(false);
                        c.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.color2)));
                        if(checkedSeats.contains(seat)){// 착석 인증도 된 좌석인 경우
                            //c.setTextColor(getResources().getColor(R.color.color6));
                            c.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.color6)));
                        }else if(failedSeats.contains(seat)){// 착석 인증도 된 좌석인 경우
                            //c.setTextColor(getResources().getColor(R.color.color6));
                            c.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                        }
                    }
                }//for
                /////// /////// /////// /////// /////// ///////

                onResume();

            }
        });
    }//onCreate

    private String SendByHttp(String url ) {// http통신


        DefaultHttpClient client = new DefaultHttpClient();//HttpClient 통신을 합니다.

        try {

            String URL = ServerInfo.SERVER_URL +url; // 서버 주소에 연결할 jsp파일 명 붙임
            Log.i("연결url::",URL);

            HttpPost post =  null;


            post = new HttpPost(URL +"?train_id=" + train_id + "&train_no=" + train_no );

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


    public void jsonParserSeatList(String pRecvServerPage) {//받아온 데이터를 JSON 파싱

        try {
            JSONArray jArr = new JSONArray(pRecvServerPage);


            if(pRecvServerPage.contains("seat_no")){

                for(int i=0; i<jArr.length(); i++){
                    reserveSeats.add(jArr.getJSONObject(i).getString("seat_no")) ;
                    Log.i("예매좌석::",reserveSeats.get(i));
                }
            }else {

                for(int i=0; i<jArr.length(); i++){
                    String seatInfo = jArr.getJSONObject(i).getString("check_seat"); // A1`F   , A2'Y 이런식
                    String[] s = seatInfo.split("`");

                    if(s[1].equals("Y"))
                        checkedSeats.add(s[0]);
                    else if(s[1].equals("F"))
                        failedSeats.add(s[0]);

                    // checkedSeats.add(jArr.getJSONObject(i).getString()) ;
                    // failedSeats.add(jArr.getJSONObject(i).getString("F")) ;
                    //Log.i("인증된 좌석::",checkedSeats.get(i));
                    //Log.i("인증실패 좌석::",failedSeats.get(i));
                }
            }
            //잘 파싱된 데이터를 넘깁니다.
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
}
