package com.example.fashkl.frbs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fashkl.frbs.config.ServerInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//import com.example.fashkl.project.ProjectDataBase.*;

///////////////////////////////////////////////////////////////////

public class RegisterPage extends AppCompatActivity {

    private EditText RuserIdFld, RpasswordFld, RmobileNoFld, RemailFld;
    private Button RsubmitBtn;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    public boolean isValid(String s) {
        return (!s.trim().isEmpty());
    }

    public boolean userIdIsValid(String s) {
        return (s.length() >= 4 && isValid(s));
    }

    public boolean passIsValid(String s) {
        return (s.length() >= 4 && isValid(s));
    }


    public int C2I(String st) {
        return Integer.parseInt(st);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        RuserIdFld = (EditText) findViewById(R.id.RuserIdFld);
        RpasswordFld = (EditText) findViewById(R.id.RpasswordFld);
        RmobileNoFld = (EditText) findViewById(R.id.RmobileNoFld);
        RemailFld = (EditText) findViewById(R.id.RemailFld);

        RsubmitBtn = (Button) findViewById(R.id.RsubmitBtn);

        RsubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean idFlag = true; boolean pwFlag = true;
                boolean mobileFlag = true; boolean emailFlag = true;

                if(!userIdIsValid(RuserIdFld.getText().toString())){
                    idFlag = false;
                    Toast.makeText(RegisterPage.this, "ID 4자이상 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                if(!passIsValid(RpasswordFld.getText().toString())) {
                    pwFlag = false;
                    Toast.makeText(RegisterPage.this, "PW 4자이상 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                if(!isValid(RmobileNoFld.getText().toString()) || RmobileNoFld.getText().toString().length() != 11
                        || RmobileNoFld.getText().charAt(0) !='0' || RmobileNoFld.getText().charAt(1) !='1'){// 11자리
                    mobileFlag = false;
                    Toast.makeText(RegisterPage.this, "01X22223333 형식으로 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                if(!isValid(RemailFld.getText().toString()) || !RemailFld.getText().toString().contains("@") ){
                    emailFlag = false;
                    Toast.makeText(RegisterPage.this, "@포함한 이메일 형식으로 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                if (idFlag && pwFlag && mobileFlag && emailFlag) {

                    //메인에서  통신 위해서
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    String sMessage = SendByHttp(RuserIdFld.getText().toString(),RpasswordFld.getText().toString()
                            , RmobileNoFld.getText().toString(), RemailFld.getText().toString());
                    String result = jsonParserList(sMessage);

                    if("register_ok".equals(result)){//가입 성공

                        //SharedPreferences
                        setting = getSharedPreferences("setting", 0); //setting , mode
                        editor= setting.edit();
                        editor.putString("id", RuserIdFld.getText().toString());
                        editor.putString("pw", RpasswordFld.getText().toString());
                        editor.commit();


                        Intent intent = new Intent(v.getContext(), MenuPage.class);
                        Toast.makeText(RegisterPage.this, "회원가입 되었습니다!", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }else if("register_fail".equals(result) || "connect_fail".equals(result)){
                        Toast.makeText(RegisterPage.this, "회원가입 실패했습니다! 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });


    }//onCreate

    private String SendByHttp( String id, String pw,String phoneNumber, String email) {// http통신


        DefaultHttpClient client = new DefaultHttpClient();//HttpClient 통신을 합니다.

        try {

            String LoginURL = ServerInfo.SERVER_URL + "register.jsp"; // 서버 주소에 연결할 jsp파일 명 붙임
            Log.i("연결url::",LoginURL);
            HttpPost post = new HttpPost(LoginURL + "?id=" + id + "&pw=" + pw
                    +"&phoneNumber="+phoneNumber+"&email="+email); //웹서버로 데이터를 전송합니다.
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

}

