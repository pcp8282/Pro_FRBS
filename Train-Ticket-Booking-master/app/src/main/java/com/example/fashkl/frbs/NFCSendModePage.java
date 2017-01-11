package com.example.fashkl.frbs;


import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.Locale;

public class NFCSendModePage extends AppCompatActivity
        implements CreateNdefMessageCallback
        , OnNdefPushCompleteCallback {

    NfcAdapter mNfcAdapter = null; // NFC 어댑터
    TextView mTextView;
    TextView scheduleInfoTextView;
    TextView reserveInfoTextView;

    private SharedPreferences setting;
    //예매 필요 정보들
    private String id; //고객 로그인 id
    private int train_no; //기차 호차
    private int train_id;
    private int reserve_no;
    private String seat_no;  //좌석 관련

    private String scheduleInfo;
    private String reserveInfo;
    private String flag; // 출입문, 좌석 구분 변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcsend_mode_page);

        //예약 정보들 받아옴
        setting = getSharedPreferences("setting", 0);
        id = setting.getString("id", ""); //로그인 id
        Log.i("share에서 로그인Id::", id);
        Intent intent = getIntent();
        train_id = intent.getExtras().getInt("train_id"); // 기차 스케줄번호
        train_no = intent.getExtras().getInt("train_no");  //호차
        seat_no = intent.getExtras().getString("seat_no"); //좌석번호
        reserve_no = intent.getExtras().getInt("reserve_no"); //예매번호
        flag = intent.getExtras().getString("flag");
        Log.i("예매번호::", String.valueOf(reserve_no));
        scheduleInfoTextView = (TextView)findViewById(R.id.scheduleInfo);
        reserveInfoTextView = (TextView)findViewById(R.id.reserveInfo);
        mTextView = (TextView)findViewById(R.id.textMessage);

        //텍스트뷰에 예매 정보
        scheduleInfoTextView.setText(intent.getExtras().getString("scheduleInfo"));
        reserveInfoTextView.setText( intent.getExtras().getString("reserveInfo"));

        //Toast.makeText(getApplicationContext(), flag, Toast.LENGTH_SHORT).show();
        // NFC 어댑터를 구한다
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // NFC 어댑터가 null 이라면 칩이 존재하지 않는 것으로 간주
        if(mNfcAdapter.isEnabled())
        {}
        else{
            Toast.makeText(getApplicationContext(), "NFC 읽기/쓰기 모드를 켜주세요", Toast.LENGTH_SHORT).show();
            intent = new Intent( Settings.ACTION_NFCSHARING_SETTINGS );
            startActivity(intent);
        }

        if( mNfcAdapter != null )
            mTextView.setText("NFC 리더기에 태깅후 인식이 되면 화면을 클릭해주세요!");
        else
            mTextView.setText("NFC모드 기능 지원하지 않는 기종입니다.");

        // NDEF 메시지 생성 & 전송을 위한 콜백 함수 설정
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        // NDEF 메시지 전송 완료 이벤트 콜백 함수 설정
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);



    }

    // NDEF 메시지 생성 이벤트 함수
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {


        // 여러개의 NDEF 레코드를 모아서 하나의 NDEF 메시지를 생성
        NdefMessage message = new NdefMessage( new NdefRecord[] {

                createTextRecord(id, Locale.ENGLISH),
                createTextRecord(String.valueOf(train_id), Locale.ENGLISH),
                createTextRecord(String.valueOf(train_no), Locale.ENGLISH),
                createTextRecord(seat_no, Locale.ENGLISH),
                createTextRecord(String.valueOf(reserve_no), Locale.ENGLISH)
        });
        return message;
    }

    // 텍스트 형식의 레코드를 생성
    public NdefRecord createTextRecord(String text, Locale locale) {
        // 텍스트 데이터를 인코딩해서 byte 배열로 변환
        byte[] data = byteEncoding(text, locale);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    // 텍스트 데이터를 인코딩해서 byte 배열로 변환
    public byte[] byteEncoding(String text, Locale locale) {
        // 언어 지정 코드 생성
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        // 인코딩 형식 생성
        Charset utfEncoding = Charset.forName("UTF-8");
        // 텍v스트를 byte 배열로 변환
        byte[] textBytes = text.getBytes(utfEncoding);

        // 전송할 버퍼 생성
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte)langBytes.length;
        // 버퍼에 언어 코드 저장
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        // 버퍼에 데이터 저장
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        return data;
    }

    // URI 형식의 레코드를 생성
    public NdefRecord createUriRecord(String url) {
        // URI 경로를 byte 배열로 변환할 때 US-ACSII 형식으로 지정
        byte[] uriField = url.getBytes(Charset.forName("US-ASCII"));
        // URL 경로를 의미하는 1 을 첫번째 byte 에 추가
        byte[] payload = new byte[uriField.length + 1];
        payload[0] = 0x01;
        System.arraycopy(uriField, 0, payload, 1, uriField.length);
        // NDEF 레코드를 생성해서 반환
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
    }

    // NDEF 메시지 전송 완료 이벤트 함수
    @Override
    public void onNdefPushComplete(NfcEvent event) {
        // 핸들러에 메시지를 전달한다
        mHandler.obtainMessage(1).sendToTarget();
    }

    // NDEF 메시지 전송이 완료되면 TextView 에 결과를 표시한다
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mTextView.setText("NFC태깅이 완료되었습니다");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Intent intent_01 = new Intent(getApplicationContext(), CheckReserveTicketPage.class);
                    startActivity(intent_01);
                    finish();


                    break;
            }
        }
    };
}
