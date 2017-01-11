package com.example.fashkl.frbs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fashkl.frbs.VO.ReservationVO;
import com.example.fashkl.frbs.config.ServerInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CheckReserveTicketPage extends AppCompatActivity {

    private SharedPreferences setting;
    ListView listview ;
    ListViewAdapter adapter;

    private String id; //로그인 id
    //예매 필요 정보들
    private int train_no; //기차 호차
    private int train_id;
    private String seat_no;
    private int reserve_no;
    //액티 비티 넘길 스케줄, 예매 정보
    private String scheduleInfo;
    private String reserveInfo;

    private ArrayList<String> reserveSeats;
    private ArrayList<String> checkedSeats; //착석 인증한 좌석
    private ArrayList<String> failedSeats; // 인증실패 좌석


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_reserve_ticket_page);

        // SharedPreferences에 저장한 로그인 ID 가져옴
        setting = getSharedPreferences("setting", 0);
        id = setting.getString("id", "");
        Log.i("로그인Id::", id);

        // Adapter 생성
        adapter = new ListViewAdapter() ;

        String result =  SendByHttp();
        jsonParserList(result);

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.list);
        listview.setAdapter(adapter);

    }//onCreate

    public class ListViewAdapter extends BaseAdapter {
        // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
        private ArrayList<ReservationVO> listViewItemList = new ArrayList<ReservationVO>() ;

        // ListViewAdapter의 생성자
        public ListViewAdapter() {

        }

        // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
        @Override
        public int getCount() {
            return listViewItemList.size() ;
        }

        // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.reserve__list, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            // ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;


            TextView scheduleInfoTextView = (TextView) convertView.findViewById(R.id.scheduleInfo) ;
            TextView reserveInfoTextView = (TextView) convertView.findViewById(R.id.reserveInfo) ;
            Button button1 = (Button) convertView.findViewById(R.id.button1); // 출입문 인증
            Button button2 = (Button) convertView.findViewById(R.id.button2); // 좌석 인증
            final ReservationVO listVO = listViewItemList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            scheduleInfo = listVO.getDepartureInfo()+"   "+listVO.getArrivalInfo();
            reserveInfo = listVO.getTrainNo() +"호차     좌석:"+listVO.getSeatNo();
            scheduleInfoTextView.setText(scheduleInfo);
            reserveInfoTextView.setText(reserveInfo);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            String sMessage1 = getDoorStatus("getTagResult.jsp", listVO.getreserveNo()); // 인증된 좌석들 가져오기위해
            String result = jsonParserSeatList(sMessage1);
            String[] sb = result.split("`");
            String door = sb[0];
            String seat = sb[1];
            Log.i("태깅 인증상황", result);
            if("Y".equals(door)){
                button1.setText("출입문인증 O");
                button1.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.color1)));
                button1.setEnabled(false);
            }else if("N".equals(door)){
                button1.setText("출입문인증 X");

            }else if("F".equals(door)){
                button1.setText("출입문인증 Fail");
                button1.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            }

            if("Y".equals(seat)){
                button2.setText("좌석인증 O");
                button2.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.color1)));
            }else if("N".equals(seat)){
                button2.setText("좌석인증X");

            }else if("F".equals(seat)){
                button2.setText("좌석인증 Fail");
                button2.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            }

            button1.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    ReservationVO item = listVO;
                    train_id = item.getTrainId();
                    train_no = item.getTrainNo();
                    seat_no = item.getSeatNo();
                    reserve_no = item.getreserveNo();
                    scheduleInfo = item.getDepartureInfo()+"   "+item.getArrivalInfo();
                    reserveInfo = train_no +"호차     좌석:"+seat_no;
                    GoActivity("door");
                }
            });

            button2.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    ReservationVO item = listVO;
                    train_id = item.getTrainId();
                    train_no = item.getTrainNo();
                    seat_no = item.getSeatNo();
                    reserve_no = item.getreserveNo();
                    scheduleInfo = item.getDepartureInfo()+"   "+item.getArrivalInfo();
                    reserveInfo = train_no +"호차     좌석:"+seat_no;
                    GoActivity("seat");
                }
            });
            Button button3 = (Button) convertView.findViewById(R.id.button3); // 착석확인
            button3.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    ReservationVO item = listVO;
                    train_id = item.getTrainId();
                    train_no = item.getTrainNo();
                    seat_no = item.getSeatNo();
                    reserve_no = item.getreserveNo();
                    scheduleInfo = item.getDepartureInfo()+"   "+item.getArrivalInfo();
                    reserveInfo = train_no +"호차     좌석:"+seat_no;

                    Intent intent_01 = new Intent(getApplicationContext(), CheckSeatPage.class);
                    intent_01.putExtra("train_id",train_id);
                    intent_01.putExtra("train_no",train_no);
                    intent_01.putExtra("seat_no",seat_no);
                    intent_01.putExtra("reserve_no",reserve_no);

                    intent_01.putExtra("scheduleInfo",scheduleInfo);
                    intent_01.putExtra("reserveInfo",reserveInfo);
                    startActivity(intent_01);
                    finish();
                }
            });

            return convertView;
        }

        // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
        @Override
        public long getItemId(int position) {
            return position ;
        }

        // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
        @Override
        public Object getItem(int position) {
            return listViewItemList.get(position) ;
        }

        // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
        public void addItem(int icon, String title, String desc, int trainNo, String seatNo, int reserveNo) {
            ReservationVO vo =  new ReservationVO(icon, title,desc,trainNo,seatNo, reserveNo );

            listViewItemList.add(vo);
        }
    }
    public void GoActivity(String flag){
        //NFCSender 액티비티 이동

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent_01 = new Intent(getApplicationContext(), NFCSendModePage.class);
        intent_01.putExtra("train_id",train_id);
        intent_01.putExtra("train_no",train_no);
        intent_01.putExtra("seat_no",seat_no);
        intent_01.putExtra("reserve_no",reserve_no);

        intent_01.putExtra("scheduleInfo",scheduleInfo);
        intent_01.putExtra("reserveInfo",reserveInfo);
        intent_01.putExtra("flag",flag);
        startActivity(intent_01);
        finish();
    }

    private String SendByHttp( ) {// http통신

        DefaultHttpClient client = new DefaultHttpClient();//HttpClient 통신을 합니다.

        try {

            String URL = ServerInfo.SERVER_URL + "checkReserveTicket.jsp"; // 서버 주소에 연결할 jsp파일 명 붙임
            Log.i("연결url::",URL);
            HttpPost post = new HttpPost(URL + "?id=" + id ); //웹서버로 데이터를 전송합니다.
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

            //JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = new JSONArray(pRecvServerPage);

            for(int i=0; i<jArr.length(); i++){
                Log.i("train_id::", jArr.getJSONObject(i).getString("Train_id"));
                String departInfo = jArr.getJSONObject(i).getString("Departure_date").toString();
                String arrivalInfo = jArr.getJSONObject(i).getString("Departure_station").toString()+"("
                        +jArr.getJSONObject(i).getString("Departure_time").toString()+") --> "
                        +jArr.getJSONObject(i).getString("Arrival_station").toString()+"("
                        +jArr.getJSONObject(i).getString("Arrival_time").toString()+")";
                int trainNo =   Integer.parseInt(jArr.getJSONObject(i).getString("Train_No"));
                String seatNo = jArr.getJSONObject(i).getString("Seat_No").toString();
                int Reserve_No =   Integer.parseInt(jArr.getJSONObject(i).getString("Reserve_No"));
                //리스트 뷰에 띄울 정보 넣음
                adapter.addItem(jArr.getJSONObject(i).getInt("Train_id"), departInfo, arrivalInfo
                            ,trainNo ,seatNo,Reserve_No);

            }

          /*  String result = json.getString("result");
            Log.i("반환값 ::", result);*/
            return "";
            //잘 파싱된 데이터를 넘깁니다.
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String SendByNFC(String url, int train_id, int train_no, String seat_no ) {// http통신


        DefaultHttpClient client = new DefaultHttpClient();//HttpClient 통신을 합니다.

        try {

            String URL = ServerInfo.SERVER_URL +url; // 서버 주소에 연결할 jsp파일 명 붙임
            Log.i("연결url::",URL);

            HttpPost post =  null;


            post = new HttpPost(URL +"?train_id=" + train_id + "&train_no=" + train_no + "&seat_no="+seat_no);

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

    private String getDoorStatus(String url, int reserve_no ) {// http통신


        DefaultHttpClient client = new DefaultHttpClient();//HttpClient 통신을 합니다.

        try {

            String URL = ServerInfo.SERVER_URL +url; // 서버 주소에 연결할 jsp파일 명 붙임
            Log.i("연결url::",URL);

            HttpPost post =  null;


            post = new HttpPost(URL +"?reserve_no=" + reserve_no);

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

    public String jsonParserSeatList(String pRecvServerPage) {//받아온 데이터를 파싱하는 부분입니다.

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


}//

