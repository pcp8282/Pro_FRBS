package com.example.fashkl.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashkl.project.VO.TrainScheduleVO;
import com.example.fashkl.project.config.ServerInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SerachRouteResultPage extends AppCompatActivity {

    private String departure_date;
    private String departure_station;
    private String arrival_station;
    ListView listview ;
    ListViewAdapter adapter;
    //ArrayList<TrainScheduleVO> list = new ArrayList<TrainScheduleVO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach_route_result_page);

        Intent intent = getIntent();
        departure_date = intent.getExtras().getString("departure_date");
        departure_station = intent.getExtras().getString("departure_station");
        arrival_station = intent.getExtras().getString("arrival_station");
        TextView dateText = (TextView)findViewById(R.id.date);
        dateText.setText(departure_date);



        // Adapter 생성
        adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.list);
        listview.setAdapter(adapter);

        //메인에서  통신 위해서
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String result =  SendByHttp();
        jsonParserList(result);

        if(result.equals("[]"))
            Toast.makeText(getApplicationContext(), "노선이 없습니다!", Toast.LENGTH_SHORT).show();


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                TrainScheduleVO item = (TrainScheduleVO) parent.getItemAtPosition(position) ;
                int train_id = item.getTrainId();
                Log.i("좌석선택 넘길 train_id::", String.valueOf(train_id));

                Intent intent_01 = new Intent(getApplicationContext(), ChooseTrainNoPage.class);
                //ChooseSeatPage액티비티 이동할때 넘길값들
                intent_01.putExtra("train_id",train_id);
                startActivity(intent_01);
                finish();
            }
        }) ;

    }//onCreate

    public class ListViewAdapter extends BaseAdapter {
        // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
        private ArrayList<TrainScheduleVO> listViewItemList = new ArrayList<TrainScheduleVO>() ;

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
                convertView = inflater.inflate(R.layout.listview_btn_item, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            // ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;


            TextView departureInfoTextView = (TextView) convertView.findViewById(R.id.departureInfo) ;
            TextView arrivalInfoTextView = (TextView) convertView.findViewById(R.id.arrivalInfo) ;
            TextView trainIdTextView = (TextView) convertView.findViewById(R.id.trainId) ;
            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            TrainScheduleVO listVO = listViewItemList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            //iconImageView.setImageDrawable(listViewItem.getIcon());
            departureInfoTextView.setText(listVO.getDepartureInfo());
            arrivalInfoTextView.setText(listVO.getArrivalInfo());

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
        public void addItem(int icon, String title, String desc) {
            TrainScheduleVO vo =  new TrainScheduleVO(icon, title,desc);

            listViewItemList.add(vo);
        }
    }


    private String SendByHttp( ) {// http통신

        DefaultHttpClient client = new DefaultHttpClient();
        try {

            String URL = ServerInfo.SERVER_URL + "searchRoute.jsp"; // 서버 주소에 연결할 jsp파일 명 붙임
            Log.i("연결url::",URL);
            Log.i("dddddfir::",departure_station);
            Log.i("dddddsec::",arrival_station);
            HttpPost post = new HttpPost(URL + "?departure_date=" + departure_date + "&departure_station=" + departure_station
                    +"&arrival_station="+arrival_station+"&flag="+"admin"); //웹서버로 데이터를 전송합니다.
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
    public String jsonParserList(String pRecvServerPage) {//받아온 데이터를 JSON 파싱

        try {
            JSONArray jArr = new JSONArray(pRecvServerPage);

            for(int i=0; i<jArr.length(); i++){

                String departDate = departure_date;

                String stationInfo = jArr.getJSONObject(i).getString("Departure_station").toString()+"("
                        +jArr.getJSONObject(i).getString("Departure_time").toString()+")  -->  "
                        + jArr.getJSONObject(i).getString("Arrival_station").toString()+"("
                        +jArr.getJSONObject(i).getString("Arrival_time").toString()+")";

                //리스트 뷰에 띄울 정보 넣음
                adapter.addItem(jArr.getJSONObject(i).getInt("Train_id"),departDate,stationInfo);

            }
            return "";
            //잘 파싱된 데이터를 넘깁니다.
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }



}
