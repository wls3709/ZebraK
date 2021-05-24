package kr.co.company.zebra;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ZebraMain extends AppCompatActivity {
    ArrayList<MyItem> arItem;
    ArrayAdapter<String> Adapter;

    int sendflag = 0;
    String userstr = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zebramain);

        Intent intent = getIntent();
        userstr = intent.getStringExtra("user");
        TextView usertxt = (TextView) findViewById(R.id.user);
        usertxt.setText(userstr+"의 서재");

        String result="";

        arItem = new ArrayList<MyItem>();
        MyItem mi;
        //mi = new MyItem("삼성 노트북");arItem.add(mi);
        //mi = new MyItem("LG 세탁기");arItem.add(mi);
        //mi = new MyItem("대우 마티즈");arItem.add(mi);

        MyListAdapter MyAdapter = new MyListAdapter(this, R.layout.zebra_context, arItem);

        ListView list;
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(MyAdapter);
        list.setDivider(new ColorDrawable(0xffffffff));
        list.setDividerHeight(3);

        try {
            sendflag = 0;



            ZebraMain.CustomTask task = new ZebraMain.CustomTask();
            result = task.execute("browse", userstr).get();
            result = result.trim();
            String[] splitText = result.split("==");

            for(int i = 0; i<splitText.length; i++){
                String[] splitText2 = splitText[i].split("--");
                String tmptitle = splitText2[0];
                String tmpauthor = splitText2[1];
                String tmpcompany = splitText2[2];
                String tstar = "";
                int tstarint = Integer.parseInt(splitText2[3]);
                if(tstarint == 0){
                    tstar = "읽지 않음";
                }
                else{
                    for(int j = 0; j<tstarint; j++){
                        tstar += "★ ";
                    }
                }

                arItem.add(new MyItem(tmptitle, tmpauthor, tmpcompany, tstar));
            }

            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            Log.i("리턴 값",result);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "도서를 등록해주세요", Toast.LENGTH_LONG).show();
        }

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder bld = new AlertDialog.Builder(ZebraMain.this);
                bld.setTitle("항목 삭제");
                bld.setMessage("항목이 삭제됩니다");
                bld.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //String tmpitem = Integer.toString(position);
                        //Toast.makeText(getApplicationContext(), tmpitem, Toast.LENGTH_LONG).show();
                        //String tmpname = arItem.get(3).atitle;

                        //String tmpname = arItem.;
                        //Toast.makeText(getApplicationContext(), tmpname, Toast.LENGTH_LONG).show();
                        deletedbcontent(position);
                    }
                });

                bld.setNegativeButton("취소", null);
                bld.create();
                bld.show();

                return true;
            }
        });

        ImageButton camerabtn = (ImageButton)findViewById(R.id.camera);
        camerabtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startBarcodeReader(v);
            }
        });
    }

    public void deletedbcontent(int item){
        try {
            sendflag = 1;
            String tmpname = arItem.get(item).atitle;

            ZebraMain.CustomTask task = new ZebraMain.CustomTask();
            String result = task.execute("delete", userstr, tmpname).get();
            //Toast.makeText(getApplicationContext(), tmpname, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), ZebraMain.class);
            intent.putExtra("user", userstr);
            startActivity(intent);
            Log.i("리턴 값",result);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_LONG).show();
        }
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
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                String content = result.getContents();
                search(content);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void search(String content){
        new Thread(){
            public void run() {
                Document doc = null;
                final StringBuilder builder = new StringBuilder();
                try {
                    doc = Jsoup.connect("http://www.kyobobook.co.kr/product/detailViewKor.laf?mallGb=KOR&ejkGb=KOR&barcode="+content).get();
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

                    Intent intent = new Intent(getApplicationContext(), ZebraSend.class);
                    intent.putExtra("user", userstr);
                    intent.putExtra("ISBN", content);
                    intent.putExtra("title", title);
                    intent.putExtra("author", author);
                    intent.putExtra("company", company);
                    startActivity(intent);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(()->{

                });
            }
        }.start();
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
                switch (sendflag) {
                    case 0:
                        sendMsg = "flag=" + strings[0] + "&user=" + strings[1];
                        osw.write(sendMsg);
                        break;
                    case 1:
                        sendMsg = "flag=" + strings[0] + "&user=" + strings[1] + "&title=" + strings[2];
                        osw.write(sendMsg);
                        break;
                }
                osw.flush();
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "EUC-KR");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
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

//리스트 뷰에 출력할 항목
class MyItem {
    MyItem(String title, String author, String company, String star) {

        atitle = title;
        aauthor = author;
        acompany = company;
        astar = star;
    }
    String atitle;
    String aauthor;
    String acompany;
    String astar;
}

//어댑터 클래스
class MyListAdapter extends BaseAdapter {
    LayoutInflater Inflater;
    ArrayList<MyItem> arSrc;
    int layout;

    public MyListAdapter(Context context, int alayout, ArrayList<MyItem> aarSrc) {
        Inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        arSrc = aarSrc;
        layout = alayout;
    }

    public int getCount() {
        return arSrc.size();
    }

    public String getItem(int position) {
        return arSrc.get(position).atitle;
    }

    public long getItemId(int position) {
        return position;
    }

    // 각 항목의 뷰 생성
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        if (convertView == null) {
            convertView = Inflater.inflate(layout, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.booktitle);
        title.setText(arSrc.get(position).atitle);

        TextView author = (TextView) convertView.findViewById(R.id.bookauthor);
        author.setText(arSrc.get(position).aauthor);

        TextView company = (TextView) convertView.findViewById(R.id.bookcompany);
        company.setText(arSrc.get(position).acompany);

        TextView star = (TextView) convertView.findViewById(R.id.bookstar);
        star.setText(arSrc.get(position).astar);

        return convertView;
    }
}
