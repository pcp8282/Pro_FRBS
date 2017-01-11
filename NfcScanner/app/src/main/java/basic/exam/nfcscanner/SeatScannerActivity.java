package basic.exam.nfcscanner;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import basic.exam.nfcscanner.config.ServerInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

import basic.exam.nfcscanner.R;

public class SeatScannerActivity extends Activity {
    TextView mTextView;
    TextView scannerInfo2View;
    NfcAdapter mNfcAdapter; // NFC 어댑터
    PendingIntent mPendingIntent; // 수신받은 데이터가 저장된 인텐트
    IntentFilter[] mIntentFilters; // 인텐트 필터
    String[][] mNFCTechLists;
    String[] nfcInfo;

    //////////////////////

    EditText input01;

    TextView output;

    private int trainId;
    private int trainNo;
    private String seatNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_scanner);

        Intent intent = getIntent();
        trainId = Integer.parseInt(intent.getExtras().getString("trainId")) ;
        trainNo = Integer.parseInt(intent.getExtras().getString("trainNo")) ;
        seatNo = intent.getExtras().getString("seatNo");
        Log.i("scanner정보::", String.valueOf(trainId)+"/"+String.valueOf(trainNo)+"/"+seatNo);

        scannerInfo2View = (TextView)findViewById(R.id.scannerInfo2);
        scannerInfo2View.setText(trainNo+"호차  좌석:"+seatNo);
        mTextView = (TextView)findViewById(R.id.textMessage);
        // NFC 어댑터를 구한다
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // NFC 어댑터가 null 이라면 칩이 존재하지 않는 것으로 간주
        if( mNfcAdapter == null ) {
            mTextView.setText("This phone is not NFC enable.");
            return;
        }

        mTextView.setText("예매한 스마트폰을 태깅해주세요!");

        // NFC 데이터 활성화에 필요한 인텐트를 생성
        //Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // NFC 데이터 활성화에 필요한 인텐트 필터를 생성
        IntentFilter iFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            iFilter.addDataType("*/*");
            mIntentFilters = new IntentFilter[] { iFilter };
        } catch (Exception e) {
            mTextView.setText("Make IntentFilter error");
        }
        mNFCTechLists = new String[][] { new String[] { NfcF.class.getName() } };

    }//onCreate

    private String SendByHttp( String[] nfcInfo, String flag) {// http통신


        DefaultHttpClient client = new DefaultHttpClient();//HttpClient 통신을 합니다.

        try {
            Log.i("http요청~~!~!","ㅇㄹ");
            String nfcURL = ServerInfo.SERVER_URL + "checkSeatStaus.jsp"; // 서버 주소에 연결할 jsp파일 명 붙임
            Log.i("연결url::",nfcURL);

            // nfcInfo   회원id, train_id, train_no, seat_no, reserve_no 순
            HttpPost post = new HttpPost(nfcURL + "?flag="+flag+"&train_id=" + trainId+"&train_no="+trainNo
                    +"&seat_no="+seatNo+ "&reserve_no=" + nfcInfo[4]); //웹서버로 데이터를 전송합니다.

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



    public void onResume() {
        super.onResume();
        // 앱이 실행될때 NFC 어댑터를 활성화 한다
        if( mNfcAdapter != null )
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);

        // NFC 태그 스캔으로 앱이 자동 실행되었을때
        if( NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()) )
            // 인텐트에 포함된 정보를 분석해서 화면에 표시
            onNewIntent(getIntent());
    }

    public void onPause() {
        super.onPause();
        // 앱이 종료될때 NFC 어댑터를 비활성화 한다
        if( mNfcAdapter != null )
            mNfcAdapter.disableForegroundDispatch(this);
    }

    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.buttonSetup : {
                if (mNfcAdapter == null) return;
                // NFC 환경설정 화면 호출
                Intent intent = new Intent( Settings.ACTION_NFCSHARING_SETTINGS );
                startActivity(intent);
                break;
            }
        }
    }

    // NFC 태그 정보 수신 함수. 인텐트에 포함된 정보를 분석해서 화면에 표시
    @Override
    public void onNewIntent(Intent intent) {
        // 인텐트에서 액션을 추출
        String action = intent.getAction();
        // 인텐트에서 태그 정보 추출
        String tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG).toString();
        String strMsg = action + "\n\n" + tag;
        // 액션 정보와 태그 정보를 화면에 출력
        mTextView.setText(strMsg);

        // 인텐트에서 NDEF 메시지 배열을 구한다
        Parcelable[] messages = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(messages == null) return;

        for(int i=0; i < messages.length; i++)
            // NDEF 메시지를 화면에 출력
            showMsg((NdefMessage)messages[i]);



    }

    /*                                 메세지 읽어오는부분!!!!!!!!!!!!!11*/
    // NDEF 메시지를 화면에 출력
    public void showMsg(NdefMessage mMessage) {
        String strMsg = "", strRec="";

        // NDEF 메시지에서 NDEF 레코드 배열을 구한다
        NdefRecord[] recs = mMessage.getRecords();

        //저장할 부분
        nfcInfo = new String[recs.length];
        for (int i = 0; i < recs.length; i++) {
            // 개별 레코드 데이터를 구한다
            NdefRecord record = recs[i];
            byte[] payload = record.getPayload();
            // 레코드 데이터 종류가 텍스트 일때
            if( Arrays.equals(record.getType(), NdefRecord.RTD_TEXT) ) {
                // 버퍼 데이터를 인코딩 변환
                strRec = byteDecoding(payload);
                nfcInfo[i] = strRec;
                strRec = "Text: " + strRec; // 읽어오는 부분

                Log.i("읽어온 Nfc정보::", i+"--"+nfcInfo[i]);
            }
            // 레코드 데이터 종류가 URI 일때
            else if( Arrays.equals(record.getType(), NdefRecord.RTD_URI) ) {
                strRec = new String(payload, 0, payload.length);
                strRec = "URI: " + strRec;
            }
            strMsg += ("\n\nNdefRecord[" + i + "]:\n" + strRec);
        }

        mTextView.append(strMsg);

        //////////////////////////////////////////////
        String flag = "";
        if(trainId == Integer.parseInt(nfcInfo[1]) && trainNo == Integer.parseInt(nfcInfo[2])//기차번호,호차, 좌석모두 일치하는경우
                && seatNo.equals(nfcInfo[3])){
            flag = "Y";

        }else{
            mTextView.setText("유효하지 않는 예약 정보입니다. 인증 실패했습니다!");
            flag = "F";

        }
        ////////////////////////////////////// 태깅 성공!!!!!!!!!!!!!1111

        //메인에서  통신 위해서
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // http 통신으로 처리한다!!!!!!!!!!
        String sMessage = SendByHttp(nfcInfo , flag); // http통신결과 sMessage 저장 ex){"result":"login_ok"}
        String result = jsonParserList(sMessage);
        if("checkSeat_ok".equals(result) && flag.equals("Y")){

            if(nfcInfo[0].equals("admin")){//관리자가 예매해줬고 NFC스티커로 인증한 경우
                mTextView.setText("guest님, "+seatNo + "좌석 인증되었습니다!");
            }else{
                mTextView.setText(nfcInfo[0]+"님, "+seatNo + "좌석 인증되었습니다!");
            }


        }else if("checkSeat_fail".equals(result)){
            mTextView.setText("유효하지 않는 예약 정보입니다. 인증 실패했습니다!");
        }else if("connect_fail".equals(result)){
            mTextView.setText("태깅 다시 시도해주세요!");
        }





    }

    // 버퍼 데이터를 디코딩해서 String 으로 변환
    public String byteDecoding(byte[] buf) {
        String strText="";
        String textEncoding = ((buf[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
        int langCodeLen = buf[0] & 0077;

        try {
            strText = new String(buf, langCodeLen + 1,
                    buf.length - langCodeLen - 1, textEncoding);
        } catch(Exception e) {
            Log.d("tag1", e.toString());
        }
        return strText;
    }




}
