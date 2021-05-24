package kr.co.company.zebra;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ZebraSend extends AppCompatActivity {
    String checkedstring = "false";
    String spinner = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zebrasend);

        Intent intent = getIntent();
        String user = intent.getStringExtra("user");
        String ISBN = intent.getStringExtra("ISBN");
        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String company = intent.getStringExtra("company");

        String titlestring = "제목 : " + title;
        String authorstring = "저자 : " + author;
        String companystring = "출판사 : " + company;

        TextView test2 = (TextView) findViewById(R.id.test2);
        final StringBuilder builder = new StringBuilder();
        builder.append(titlestring).append("\n\n");
        builder.append(authorstring).append("\n\n");
        builder.append(companystring).append("\n");

        test2.setText(builder.toString());

        Button send = (Button)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { // -------------------------------------------------------------------------------------------------------------- 전송
                Log.i(this.getClass().getName(), "시작");
                CheckBox finishedChck = (CheckBox)findViewById(R.id.check_finished);
                if(finishedChck.isChecked()){
                    Spinner starSpinner = (Spinner)findViewById(R.id.spinner_star);
                    spinner = starSpinner.getSelectedItem().toString().substring(0, 1);
                }
                try {
                    String result;
                    CustomTask task = new CustomTask();
                    result = task.execute("book", user, ISBN, title, author, company, checkedstring, spinner).get();
                    //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    Log.i("리턴 값",result);
                    Intent intent = new Intent(getApplicationContext(), ZebraMain.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button sendcancelBtn = (Button)findViewById(R.id.sendcancel);
        sendcancelBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), ZebraMain.class);
                //intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        CheckBox finishedChck = (CheckBox)findViewById(R.id.check_finished);
        finishedChck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // --------------------------------------------------------------------------------------------------------------- 체크박스
                if(finishedChck.isChecked()){
                    LinearLayout thislayout = (LinearLayout)findViewById(R.id.layoutcheck);
                    thislayout.setVisibility(View.VISIBLE);
                    checkedstring = "true";
                }
                else{
                    LinearLayout thislayout = (LinearLayout)findViewById(R.id.layoutcheck);
                    thislayout.setVisibility(View.INVISIBLE);
                    checkedstring = "false";
                }
            }
        });

        Spinner starSpinner = (Spinner)findViewById(R.id.spinner_star);
        ArrayAdapter starAdapter = ArrayAdapter.createFromResource(this,
                R.array.star, android.R.layout.simple_spinner_item);
        starAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        starSpinner.setAdapter(starAdapter);
        starSpinner.setSelection(4);

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
                sendMsg = "flag="+strings[0]+"&user="+strings[1]+"&ISBN="+strings[2]+"&title="+strings[3]+"&author="+strings[4]+"&company="+strings[5]+"&finished="+strings[6]+"&spinner="+strings[7];
                osw.write(sendMsg);
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










