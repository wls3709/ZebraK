package kr.co.company.zebra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
                    Intent intent = new Intent(getApplicationContext(), ZebraMain.class);
                    startActivity(intent);
                }
            }
        });
    }
}