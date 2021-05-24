package kr.co.company.zebra;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ZebraStart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zebrastart);

        Button signBtn = (Button)findViewById(R.id.signin);
        signBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //   Intent intent = new Intent(getApplicationContext(), RestoreTalkBrowse.class);
                //  startActivity(intent);
                Intent intent = new Intent(getApplicationContext(), ZebraSignin.class);
                startActivity(intent);
            }
        });

        Button loginBtn = (Button)findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                EditText idtext = (EditText)findViewById(R.id.id);
                String id = idtext.getText().toString();
                EditText passwordtext = (EditText)findViewById(R.id.password);
                String password = passwordtext.getText().toString();

                if(id.length() == 0){
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_LONG).show();
                }
                else if(password.length() == 0){
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_LONG).show();
                }
                else{
                    try {
                        String result;
                        ZebraStart.CustomTask task = new ZebraStart.CustomTask();
                        result = task.execute("login", id, password).get();
                        result = result.trim();
                        if(result.equals("login")){
                            Intent intent = new Intent(getApplicationContext(), ZebraMain.class);
                            intent.putExtra("user", id);
                            startActivity(intent);
                        }
                        else if(result.equals("id")){
                            Toast.makeText(getApplicationContext(), "ID를 확인해주세요", Toast.LENGTH_LONG).show();
                        }
                        else if(result.equals("pw")){
                            Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "에러", Toast.LENGTH_LONG).show();
                        }
                        Log.i("리턴 값",result);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_LONG).show();
                    }

                    Intent intent = new Intent(getApplicationContext(), ZebraMain.class);
                    intent.putExtra("user", id);
                    startActivity(intent);
                }
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

                sendMsg = "flag="+strings[0]+"&id="+strings[1]+"&pw="+strings[2]; osw.write(sendMsg);

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