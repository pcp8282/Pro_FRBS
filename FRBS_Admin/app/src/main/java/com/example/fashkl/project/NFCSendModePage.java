package com.example.fashkl.project;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

public class NFCSendModePage extends AppCompatActivity
        {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;

    //TextView mTextView;
    TextView scheduleInfoTextView;
    TextView reserveInfoTextView;

    private SharedPreferences setting;
    //예매 필요 정보들
    private String id; //고객 로그인 id
    private int train_no; //기차 호차
    private int train_id;
    private String seat_no;  //좌석 관련
    private int reserve_no;
    private String scheduleInfo;
    private String reserveInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        //예약 정보들 받아옴
        setting = getSharedPreferences("setting", 0);
        id = setting.getString("id", ""); //로그인 id
        Log.i("share에서 로그인Id::", id);
        Intent intent = getIntent();
        train_id = intent.getExtras().getInt("train_id"); // 기차 스케줄번호
        train_no = intent.getExtras().getInt("train_no");  //호차
        seat_no = intent.getExtras().getString("seat_no"); //좌석번호
        reserve_no = intent.getExtras().getInt("reserve_no"); //예매번호

        // 관리자앱에서 회원 id조회해서 예약정보 가져온 경우
        if(intent.getExtras().getString("userId") != null)
            id = intent.getExtras().getString("userId");
        Log.i("예매번호::", String.valueOf(reserve_no));

        scheduleInfoTextView = (TextView)findViewById(R.id.scheduleInfo);
        reserveInfoTextView = (TextView)findViewById(R.id.reserveInfo);


        //텍스트뷰에 예매 정보
        scheduleInfoTextView.setText(intent.getExtras().getString("scheduleInfo"));
        reserveInfoTextView.setText( intent.getExtras().getString("reserveInfo"));


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        // NFC ���� ��ü ����
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        intent = new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Log.e("nfc error 어디니","nfcAdapter");
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_write,
                    container, false);
            return rootView;
        }
    }

    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter
                    .enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    // NFC �±� ��ĵ�� ȣ��Ǵ� �޼ҵ�
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {
            processTag(intent); // processTag �޼ҵ� ȣ��
        }
    }

    // onNewIntent �޼ҵ� ���� �� ȣ��Ǵ� �޼ҵ�
    private void processTag(Intent intent) {

        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefMessage message = createTagMessage("frbs 예매정보보", TYPE_TEXT);
        writeTag(message, detectedTag);
    }

    // ������ �±׿� NdefMessage�� ���� �޼ҵ�
    public boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return false;
                }

                if (ndef.getMaxSize() < size) {
                    return false;
                }

                //////////////////////////////////////////////////

                message = new NdefMessage(new NdefRecord[]{
                        createTextRecord(id, Locale.ENGLISH),
                        createTextRecord(String.valueOf(train_id), Locale.ENGLISH),
                        createTextRecord(String.valueOf(train_no), Locale.ENGLISH),
                        createTextRecord(seat_no, Locale.ENGLISH),
                        createTextRecord(String.valueOf(reserve_no), Locale.ENGLISH)});

                ////////////////////////////////////////////////
                ndef.writeNdefMessage(message);
                Toast.makeText(getApplicationContext(), "정보가 저장되었습니다!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "스티커에 저장할 태그 내용이 없습니다",
                        Toast.LENGTH_SHORT).show();

                NdefFormatable formatable = NdefFormatable.get(tag);
                if (formatable != null) {
                    try {
                        formatable.connect();
                        formatable.format(message);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            return false;
        }

        return true;
    }

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
        data[0] = (byte) langBytes.length;
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


    /**
     * Create a new tag message
     *
     * @param msg
     * @param type
     * @return
     */
    private NdefMessage createTagMessage(String msg, int type) {
        NdefRecord[] records = new NdefRecord[1];

        if (type == TYPE_TEXT) {
            records[0] = createTextRecord(msg, Locale.KOREAN, true);
        } else if (type == TYPE_URI) {
            records[0] = createUriRecord(msg.getBytes());
        }

        NdefMessage mMessage = new NdefMessage(records);

        return mMessage;
    }

    private NdefRecord createTextRecord(String text, Locale locale,
                                        boolean encodeInUtf8) {
        final byte[] langBytes = locale.getLanguage().getBytes(
                Charsets.US_ASCII);
        final Charset utfEncoding = encodeInUtf8 ? Charsets.UTF_8 : Charset
                .forName("UTF-16");
        final byte[] textBytes = text.getBytes(utfEncoding);
        final int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        final char status = (char) (utfBit + langBytes.length);
        final byte[] data = Bytes.concat(new byte[] { (byte) status },
                langBytes, textBytes);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
                new byte[0], data);
    }

    private NdefRecord createUriRecord(byte[] data) {
        return new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI, NdefRecord.RTD_URI,
                new byte[0], data);
    }
}
