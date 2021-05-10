package kr.co.company.zebra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ZebraSignin extends AppCompatActivity {

    boolean idcheckflag = false;

    public void changeflag(){
        idcheckflag = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zebrasignin);



        Button checkBtn = (Button)findViewById(R.id.rdctioncheck);
        checkBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                EditText idsign = (EditText)findViewById(R.id.idsign);
                String id = idsign.getText().toString();

                if(id.length()==0){
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "중복확인", Toast.LENGTH_LONG).show();
                    changeflag();
                }
            }
        });

        Button signinBtn = (Button)findViewById(R.id.signinconfirm);
        signinBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

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
                    Toast.makeText(getApplicationContext(), "확인", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button cancelBtn = (Button)findViewById(R.id.signincancel);
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), ZebraStart.class);
                startActivity(intent);
            }
        });

    }
}