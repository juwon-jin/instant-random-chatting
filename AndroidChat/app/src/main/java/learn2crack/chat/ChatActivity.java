package learn2crack.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends Activity {
    SharedPreferences prefs;
    List<NameValuePair> params;
    EditText chat_msg;
    Button send_btn;
    Bundle bundle;
    TableLayout tab;

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tab = (TableLayout)findViewById(R.id.tab);
        prefs = getSharedPreferences("Chat", 0);
        bundle = getIntent().getBundleExtra("INFO");
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("CURRENT_ACTIVE", bundle.getString("mobno"));
        edit.commit();
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

        Log.i("makejin3201", "bundle.getString(\"name\") : " + bundle.getString("name"));
        if(bundle.getString("name") != null){
            Log.i("makejin3201", "bundle : " + bundle);
            TableRow tr1 = new TableRow(getApplicationContext());
            tr1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView textview = new TextView(getApplicationContext());
            textview.setTextSize(20);
            textview.setTextColor(Color.parseColor("#0B0719"));
            textview.setText(Html.fromHtml("<b>"+bundle.getString("name")+" : </b>"+bundle.getString("msg")));
            //textview.setText(Html.fromHtml("<b>"+bundle.getString("name")+" : </b>"+bundle.getString("msg")));
            tr1.addView(textview);
            tab.addView(tr1);

        }

        chat_msg = (EditText)findViewById(R.id.chat_msg);
        send_btn = (Button)findViewById(R.id.sendbtn);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow tr2 = new TableRow(getApplicationContext());
                tr2.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView textview = new TextView(getApplicationContext());
                textview.setTextSize(20);
                textview.setTextColor(Color.parseColor("#A901DB"));
                textview.setText(Html.fromHtml("<b>Me : </b>" + chat_msg.getText().toString()));
                tr2.addView(textview);
                tab.addView(tr2);
                new Send().execute();
            }
        });


    }

     private BroadcastReceiver onNotice= new BroadcastReceiver() {
   //private MSGReceiver onNotice= new MSGReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str="";
            String str1="";
            try {
                str = URLDecoder.decode(intent.getStringExtra("msg"), "UTF-8");
                str1 = URLDecoder.decode(intent.getStringExtra("fromname"), "UTF-8");
            }catch (Exception ex){
                ex.printStackTrace();
            }
            String str2 = intent.getStringExtra("fromu");
            Log.i("makejin0222", "msg"+ str);
            Log.i("makejin0222", "fromname"+ str1);
            Log.i("makejin0222", "fromu"+ str1);


            if(str2.equals(bundle.getString("mobno"))){

                TableRow tr1 = new TableRow(getApplicationContext());
                tr1.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView textview = new TextView(getApplicationContext());
                textview.setTextSize(20);
                textview.setTextColor(Color.parseColor("#0B0719"));
                textview.setText(Html.fromHtml("<b>"+str1+" : </b>"+str));
                tr1.addView(textview);
                tab.addView(tr1);
            }



        }
    };
    private class Send extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();

            try {
                params.add(new BasicNameValuePair("from",prefs.getString("REG_FROM", "")));
                params.add(new BasicNameValuePair("fromn", URLEncoder.encode(prefs.getString("FROM_NAME", ""), "utf-8")));
                params.add(new BasicNameValuePair("to", bundle.getString("mobno")));
                params.add(new BasicNameValuePair("msg",  URLEncoder.encode(chat_msg.getText().toString(), "utf-8")));
            }catch (Exception ex){
                ex.printStackTrace();
            }
            Log.i("makejin0222", "asd" + chat_msg.getText().toString());

            JSONObject jObj = json.getJSONFromUrl("http://52.192.83.162:8080/send", params);
            return jObj;



        }
        @Override
        protected void onPostExecute(JSONObject json) {
            chat_msg.setText("");

            String res = null;
            try {
                res = json.getString("response");
                if(res.equals("Failure")){
                    Toast.makeText(getApplicationContext(),"The user has logged out. You cant send message anymore !",Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}