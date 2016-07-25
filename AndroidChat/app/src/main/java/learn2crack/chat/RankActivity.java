package learn2crack.chat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class RankActivity extends Activity {
    List<NameValuePair> params;
    Intent flagIntent;
    ArrayList<HashMap<String, String>> users = new ArrayList<HashMap<String, String>>();
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        list = (ListView)findViewById(R.id.listView);
        flagIntent = getIntent();
        new LoadRank().execute();
    }

    private class LoadRank extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", flagIntent.getStringExtra("mobno")));
            JSONArray jAry = json.getJSONArray("http://52.192.83.162:8080/load_rank",params);

            return jAry;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            int [] arr = new int[json.length()];
            for (int i = 0; i < json.length(); i++) {
                JSONObject c = null;
                try {
                    c = json.getJSONObject(i);
                    String name = "";
                    try {
                        name = URLDecoder.decode(c.getString("name"), "UTF-8");
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    String mobno = c.getString("mobno");
                    String total_connect_time = c.getString("total_connect_time");
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", name);
                    map.put("mobno", mobno);
                    map.put("total_connect_time", total_connect_time);
                    arr[i] = Integer.parseInt(total_connect_time);
                    users.add(map);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

/*
            for(int i=0;i<arr.length;i++){
                Log.d("mbn", ""+arr[i]);
            }

            quickSort(arr, 0, json.length() - 1);

            for(int i=0;i<arr.length;i++){
                Log.d("mbn", ""+arr[i]);
            }
            */

            Collections.sort(users, new SortByTime());

            Log.i("makejin2"," " + users);
            ListAdapter adapter = new SimpleAdapter(getApplicationContext(), users,
                    R.layout.user_list_single2,
                    new String[] { "name","mobno", "total_connect_time"}, new int[] {
                    R.id.name, R.id.mobno, R.id.total_connect_time});

            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Bundle args = new Bundle();
                    args.putString("mobno", users.get(position).get("mobno"));
                    Log.i("makejin3201", "args : " + args);
                    Intent chat = new Intent(getApplicationContext(), ChatActivity.class);
                    chat.putExtra("INFO", args);
                    startActivity(chat);
                }
            });
        }
    }
    public class SortByTime implements Comparator {

        public int compare(Object o1, Object o2) {
            HashMap<String, String> p1 = (HashMap<String, String>) o1;
            HashMap<String, String> p2 = (HashMap<String, String>) o2;
            // return -1, 0, 1 to determine less than, equal to or greater than
            return (Integer.parseInt(p1.get("total_connect_time")) < Integer.parseInt(p2.get("total_connect_time")) ? 1 : (p1.get("total_connect_time") == p2.get("total_connect_time") ? 0 : -1));
            // **or** the previous return statement can be simplified to:
            //return p1.getPrice() - p2.getPrice();
        }
    }
    int partition(int arr[], int left, int right)
    {
        int i = left, j = right;
        int tmp;
        int pivot = arr[(left + right) / 2];

        while (i <= j) {
            while (arr[i] > pivot)
                i++;
            while (arr[j] < pivot)
                j--;
            if (i <= j) {
                tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
                i++;
                j--;
            }
        };

        return i;
    }

    void quickSort(int arr[], int left, int right) {
        int index = partition(arr, left, right);
        if (left < index - 1)
            quickSort(arr, left, index - 1);
        if (index < right)
            quickSort(arr, index, right);
    }
}
