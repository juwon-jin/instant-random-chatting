
package learn2crack.chat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class AdminFragment extends Fragment {
    ListView list;
    ArrayList<HashMap<String, String>> users = new ArrayList<HashMap<String, String>>();
    Button refresh;
    List<NameValuePair> params;
    SharedPreferences prefs;

    static public int total_connect_time;
    static public String connect_finish_moment;
    FragmentManager fm;
    Button rank;

    String tempMobno;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.activity_admin_fragment, container, false);
        prefs = getActivity().getSharedPreferences("Chat", 0);

        list = (ListView)view.findViewById(R.id.listView);
        refresh = (Button)view.findViewById(R.id.refresh);
        //TV_score.setText(total_connect_time);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.content_frame)).commit();
                Fragment reg = new AdminFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, reg);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();

            }
        });
        new Load().execute();

        return view;
    }


    private class Load extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM", "")));
            JSONArray jAry = json.getJSONArray("http://52.192.83.162:8080/getuser", params);

            return jAry;
        }

        @Override
        protected void onPostExecute(JSONArray json) {

            for (int i = 0; i < json.length(); i++) {
                JSONObject c = null;
                try {
                    c = json.getJSONObject(i);
                    String name = "";
                    try {
                        name = URLDecoder.decode(c.getString("name"), "utf-8");
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    String mobno = c.getString("mobno");
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", name);
                    map.put("mobno", mobno);
                    users.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.i("makejin2", getActivity() + " " + users);
            ListAdapter adapter = new SimpleAdapter(getActivity(), users,
                    R.layout.user_list_single,
                    new String[]{"name", "mobno"}, new int[]{
                    R.id.name, R.id.mobno});
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Log.d("Asad", "" + tempMobno);
                    tempMobno = users.get(position).get("mobno");
                    Log.d("Asad", "" + tempMobno);
                    new RemoveMember().execute();
                }
            });
        }
    }

    private class RemoveMember extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", tempMobno));
            Log.d("Asad", "" + tempMobno);
            Log.i("makejin", "params : " + params);

            JSONObject jObj = json.getJSONFromUrl("http://52.192.83.162:8080/withdrawal", params);
            return jObj;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                String res = json.getString("response");
                if (res.equals("Removed Sucessfully")) {
                    refresh.callOnClick();
                } else {
                    Toast toast = Toast.makeText(getActivity(), res, Toast.LENGTH_SHORT);
                    int offsetX = 0;
                    int offsetY = 0;
                    toast.setGravity(Gravity.CENTER, offsetX, 300);
                    toast.show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}