package kr.co.company.zebra;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ZebraSignin extends AppCompatActivity {

    boolean idcheckflag = false;
    int sendflag = 0;

    public void changeflag(){
        idcheckflag = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zebrasignin);



        Button checkBtn = (Button)findViewById(R.id.rdctioncheck);
        checkBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){ // --------------------------------------------------------------------------------------------------------- 중복 확인

                EditText idsign = (EditText)findViewById(R.id.idsign);
                String id = idsign.getText().toString();

                if(id.length()==0){
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_LONG).show();
                }
                else{
                    sendflag = 0;
                    try {
                        String result;
                        ZebraSignin.CustomTask task = new ZebraSignin.CustomTask();
                        result = task.execute("duplicate", id).get();
                        result = result.trim();
                        if(result.equals("okay")){
                            Toast.makeText(getApplicationContext(), "사용 가능한 ID 입니다.", Toast.LENGTH_LONG).show();
                            changeflag();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                        }
                        Log.i("리턴 값",result);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        Button signinBtn = (Button)findViewById(R.id.signinconfirm);
        signinBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){ // ------------------------------------------------------------------------------------------------------------ 확인

                EditText idsign = (EditText)findViewById(R.id.idsign);
                String id = idsign.getText().toString();
                EditText pwsign = (EditText)findViewById(R.id.pwsign);
                String pw = pwsign.getText().toString();
                EditText pwsigncheck = (EditText)findViewById(R.id.pwsigncheck);
                String pwcheck = pwsigncheck.getText().toString();

                if(id.length()==0) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_LONG).show();
                }
                else if(pwcheck.length() == 0 || pwsign.length() == 0){
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_LONG).show();
                }
                else if(!pwcheck.equals(pw)){
                    Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요", Toast.LENGTH_LONG).show();
                }
                else if(idcheckflag == false){
                    Toast.makeText(getApplicationContext(), "아이디를 중복확인해주세요", Toast.LENGTH_LONG).show();
                }
                else {
                    sendflag = 1;
                    try {
                        String result;
                        ZebraSignin.CustomTask task = new ZebraSignin.CustomTask();
                        result = task.execute("sign", id, pw).get();
                        result = result.trim();
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                        Log.i("리턴 값",result);
                        Intent intent = new Intent(getApplicationContext(), ZebraStart.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        Button cancelBtn = (Button)findViewById(R.id.signincancel);
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){ //--------------------------------------------------------------------------------------------------------------- 취소
                Intent intent = new Intent(getApplicationContext(), ZebraStart.class);
                startActivity(intent);
            }
        });

    }

    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://10.0.2.2:8080/AndroidProject/servertest.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                switch (sendflag){
                    case 0: sendMsg = "flag="+strings[0]+"&id="+strings[1]; osw.write(sendMsg); break;
                    case 1: sendMsg = "flag="+strings[0]+"&id="+strings[1]+"&pw="+strings[2]; osw.write(sendMsg); break;
                }
                osw.flush();
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "EUC-KR");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }
}