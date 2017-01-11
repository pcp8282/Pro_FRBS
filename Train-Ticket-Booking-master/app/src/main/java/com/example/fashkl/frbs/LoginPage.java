package com.example.fashkl.frbs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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


public class LoginPage extends AppCompatActivity {

    private Button registerBtn, btnLinkToRegisterScreen;
    private EditText passwordFld;
    private AutoCompleteTextView userNameFld;
    private String[] loop;
    private ArrayAdapter<String> adapter;

    public Intent selectPageIntent, registerPageIntent;


    SharedPreferences setting;
    SharedPreferences.Editor editor;
    String id;
    String pw;


    private boolean isUserIdValid(String userId) {
        return (userId.length() >= 4 && !userId.trim().isEmpty());
    }


    private boolean isPasswordValid(String password) {
        return (password.length() >= 4 && !password.trim().isEmpty());
    }

        public void reset(EditText ed) {
        ed.setText(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login2);

        userNameFld = (AutoCompleteTextView) findViewById(R.id.userIdFldd);
        passwordFld = (EditText) findViewById(R.id.passwordFldd);
        registerBtn = (Button) findViewById(R.id.loginRegistBtnd);
        btnLinkToRegisterScreen = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        //selectPageIntent = new Intent(this, SelectPage.class);
        registerPageIntent = new Intent(this, RegisterPage.class);



        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isUserIdValid(userNameFld.getText().toString()) &&
                        isPasswordValid(passwordFld.getText().toString())) {

                    id = userNameFld.getText().toString().trim();
                    pw = passwordFld.getText().toString().trim();



                    //SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
                   // UserDetails userdetails = RegisteratoinLab.get(LoginPage.this).findUserDetails(Id);
                   /* if (RegisteratoinLab.get(LoginPage.this).getflage()) {
                        Toast.makeText(LoginPage.this, "User not found  ", Toast.LENGTH_SHORT).show();
                    } else {

                    }*/

                    //메인에서  통신 위해서
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);


                    String sMessage = SendByHttp(id, pw); // http통신결과 sMessage 저장 ex){"result":"login_ok"}
                    String result = jsonParserList(sMessage);
                    handlingLogin(result); // 로그인 성공, 실패 처리

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Id,pw 4자이상 입력해주세요 ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnLinkToRegisterScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(registerPageIntent);
                finish();
            }
        });

    }//onCreate

    private String SendByHttp( String id, String pw) {// http통신


        DefaultHttpClient client = new DefaultHttpClient();//HttpClient 통신을 합니다.

        try {

            String LoginURL = ServerInfo.SERVER_URL + "login.jsp"; // 서버 주소에 연결할 jsp파일 명 붙임
            Log.i("연결url::",LoginURL);
            HttpPost post = new HttpPost(LoginURL + "?id=" + id + "&pw=" + pw); //웹서버로 데이터를 전송합니다.
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

    public void handlingLogin(String result) {

        if (result.equals("login_ok")) {
            Toast toast = Toast.makeText(this, "로그인 되었습니다", Toast.LENGTH_SHORT);
            toast.show();

            //SharedPreferences
            setting = getSharedPreferences("setting", 0); //setting , mode
            editor= setting.edit();
            editor.putString("id", id);
            editor.putString("pw", pw);
            editor.commit();
            Intent intent_01 = new Intent(getApplicationContext(), MenuPage.class);
            startActivity(intent_01);
            finish();
        } else if (result.equals("login_fail")) {
            Toast toast = Toast.makeText(this, "Id, Pw 확인해주세요.", Toast.LENGTH_SHORT);
            toast.show();
        } else if (result.equals("connect_fail") || result.equals("")) {
            Toast toast = Toast.makeText(this, "로그인 버튼을 다시 눌러주세요", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
