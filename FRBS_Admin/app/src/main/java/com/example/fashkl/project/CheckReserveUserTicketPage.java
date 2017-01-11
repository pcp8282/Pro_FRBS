package com.example.fashkl.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashkl.project.VO.ReservationVO;
import com.example.fashkl.project.config.ServerInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CheckReserveUserTicketPage extends AppCompatActivity {

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

    private EditText userIdText;
    private Button selectReserve;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_reserve_user_ticket_page);

        // SharedPreferences에 저장한 로그인 ID 가져옴
        setting = getSharedPreferences("setting", 0);
        id = setting.getString("id", "");
        Log.i("로그인Id::", id);

        userIdText = (EditText)findViewById(R.id.userIdText);
        selectReserve = (Button) findViewById(R.id.selectReserve);

        selectReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!userIdText.getText().toString().trim().isEmpty()){//id 입력한 경우
                    userId = userIdText.getText().toString().trim();
                    // Adapter 생성
                    adapter = new ListViewAdapter() ;

                    String result =  SendByHttp();
                    if(result.equals("[]")){
                        Toast.makeText(getApplicationContext(),"해당 ID에 예매현황이 없습니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        jsonParserList(result);

                        // 리스트뷰 참조 및 Adapter달기
                        listview = (ListView) findViewById(R.id.list);
                        listview.setAdapter(adapter);


                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView parent, View v, int position, long id) {//리스트뷰 클릭

                                ReservationVO item = (ReservationVO) parent.getItemAtPosition(position) ;
                                train_id = item.getTrainId();
                                train_no = item.getTrainNo();
                                seat_no = item.getSeatNo();
                                reserve_no = item.getreserveNo();
                                scheduleInfo = item.getDepartureInfo()+"   "+item.getArrivalInfo();
                                reserveInfo = train_no +"호차     좌석:"+seat_no;
                                Log.i("seat_no", seat_no);

                                //NFCSender 액티비티 이동
                                Intent intent_01 = new Intent(getApplicationContext(), NFCSendModePage.class);
                                intent_01.putExtra("train_id",train_id);
                                intent_01.putExtra("train_no",train_no);
                                intent_01.putExtra("seat_no",seat_no);
                                intent_01.putExtra("reserve_no",reserve_no);
                                intent_01.putExtra("scheduleInfo",scheduleInfo);
                                intent_01.putExtra("reserveInfo",reserveInfo);
                                intent_01.putExtra("userId",userId);

                                startActivity(intent_01);
                            }
                        }) ;
                    }


                }else{
                    Toast.makeText(getApplicationContext(),"조회할 ID를 입력해주세요 ", Toast.LENGTH_SHORT).show();
                }
            }
        });



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

            // TextView trainIdTextView = (TextView) convertView.findViewById(R.id.trainId) ;
            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득


            ReservationVO listVO = listViewItemList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            scheduleInfo = listVO.getDepartureInfo()+"   "+listVO.getArrivalInfo();
            reserveInfo = listVO.getTrainNo() +"호차     좌석:"+listVO.getSeatNo();
            scheduleInfoTextView.setText(scheduleInfo);
            reserveInfoTextView.setText(reserveInfo);
            //arrivalInfoTextView.setText();

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
        public void addItem(int icon, String title, String desc, int trainNo, String seatNo , int reserveNo) {
            ReservationVO vo =  new ReservationVO(icon, title,desc,trainNo,seatNo,reserveNo );

            listViewItemList.add(vo);
        }
    }


    private String SendByHttp( ) {// http통신

        DefaultHttpClient client = new DefaultHttpClient();//HttpClient 통신을 합니다.

        try {

            String URL = ServerInfo.SERVER_URL + "checkReserveTicket.jsp"; // 서버 주소에 연결할 jsp파일 명 붙임
            Log.i("연결url::",URL);
            HttpPost post = new HttpPost(URL + "?id=" + userId ); //웹서버로 데이터를 전송합니다.
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
                int Reserve_No =   Integer.parseInt(jArr.getJSONObject(i).getString("Reserve_No"));
                String seatNo = jArr.getJSONObject(i).getString("Seat_No").toString();

                //리스트 뷰에 띄울 정보 넣음
                adapter.addItem(jArr.getJSONObject(i).getInt("Train_id"), departInfo, arrivalInfo
                            ,trainNo ,seatNo, Reserve_No);

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
}//

