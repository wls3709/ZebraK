package kr.co.company.zebra;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ZebraMain extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zebramain);

        TextView test = (TextView) findViewById(R.id.test);
        Button sendBtn = (Button)findViewById(R.id.sendpage);
        sendBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Intent intent = new Intent(getApplicationContext(), ZebraSend.class);
                //startActivity(intent);
                search(test);
            }
        });

        ImageButton camerabtn = (ImageButton)findViewById(R.id.camera);
        camerabtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Toast.makeText(getApplicationContext(), "카메라", Toast.LENGTH_LONG).show();
                startBarcodeReader(v);
            }
        });
    }

    public void startBarcodeReader(View view){
        new IntentIntegrator(this).initiateScan();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.kyobobook.co.kr/product/detailViewKor.laf?mallGb=KOR&ejkGb=KOR&barcode="+result.getContents()));
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void search(TextView test){
        new Thread(){
            public void run() {
                Document doc = null;
                final StringBuilder builder = new StringBuilder();
                try {
                    doc = Jsoup.connect("http://www.kyobobook.co.kr/product/detailViewKor.laf?mallGb=KOR&ejkGb=KOR&barcode=9788967994815").get();
                    //String title = doc.title();
                    String title = doc.select("h1.title strong").text();
                    String authorcompany = doc.select("span.name a").text();
                    String[] tmp = authorcompany.split("\\s");
                    String author = "";
                    String company = "";

                    for(int i = 0; i<tmp.length-1; i++){
                        author = author.concat(tmp[i]+" ");
                    }
                    company = tmp[tmp.length-1];



                    builder.append(title).append("\n");
                    builder.append(author).append("\n");
                    builder.append(company).append("\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(()->{


                    test.setText(builder.toString());
                });
            }
        }.start();
    }
}