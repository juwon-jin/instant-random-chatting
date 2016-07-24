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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class UserFragment extends Fragment {
    ListView list;
    TextView TV_name;
    TextView TV_score;
    ArrayList<HashMap<String, String>> users = new ArrayList<HashMap<String, String>>();
    Button refresh,logout,withdrawal;
    List<NameValuePair> params;
    SharedPreferences prefs;

    static public int total_connect_time;
    static public String connect_finish_moment;
    FragmentManager fm;

    Button rank;

    @Override
    public void onStart() {
        super.onStart();

        new LoadTotalTime().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        new SaveTotalTime().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.user_fragment, container, false);
        prefs = getActivity().getSharedPreferences("Chat", 0);

        list = (ListView)view.findViewById(R.id.listView);
        refresh = (Button)view.findViewById(R.id.refresh);
        logout = (Button)view.findViewById(R.id.logout);
        withdrawal = (Button) view.findViewById(R.id.withdrawal);

        TV_name = (TextView) view.findViewById(R.id.TV_name);
        TV_name.setText(prefs.getString("FROM_NAME", ""));
        Log.d("qrewe", ""+prefs.getAll());
        TV_score = (TextView) view.findViewById(R.id.TV_score);
        //TV_score.setText(total_connect_time);
        rank = (Button) view.findViewById(R.id.rank);


        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RankActivity.class);
                intent.putExtra("mobno", prefs.getString("REG_FROM",""));
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.content_frame)).commit();
                Fragment reg = new UserFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, reg);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment reg = new NonFacebookSigninFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, reg);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("REG_FROM", "");
                edit.commit();
            }
        });
        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new  withdrawal().execute();

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
            params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM","")));
            JSONArray jAry = json.getJSONArray("http://52.192.83.162:8080/getuser",params);

            return jAry;
        }
        @Override
        protected void onPostExecute(JSONArray json) {

              for (int i = 0; i < json.length(); i++) {
                      JSONObject c = null;
                    try {
                      c = json.getJSONObject(i);
                      String name ="";
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
                  }
                  catch (JSONException e) {
                      e.printStackTrace();
                  }
              }

            Log.i("makejin2", getActivity() + " " + users);
            ListAdapter adapter = new SimpleAdapter(getActivity(), users,
                    R.layout.user_list_single,
                    new String[] { "name","mobno"}, new int[] {
                    R.id.name, R.id.mobno});
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Bundle args = new Bundle();
                    args.putString("mobno", users.get(position).get("mobno"));
                    Log.i("makejin3201", "args : " + args);
                    Intent chat = new Intent(getActivity(), ChatActivity.class);
                    chat.putExtra("INFO", args);
                    startActivity(chat);
                }
            });
        }
    }
    private class withdrawal extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM", "")));
            JSONObject jObj = json.getJSONFromUrl("http://52.192.83.162:8080/withdrawal",params);

            return jObj;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            String res = null;
            try {
                res = json.getString("response");
                Toast toast = Toast.makeText(getActivity(), res, Toast.LENGTH_LONG);
                int offsetX = 0;
                int offsetY = 0;
                toast.setGravity(Gravity.CENTER, offsetX, offsetY);
                toast.show();
                if(res.equals("Removed Sucessfully")) {
                    Fragment reg = new NonFacebookSigninFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, reg);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("REG_FROM", "");
                    edit.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }

    private class LoadTotalTime extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM","")));
            JSONArray jAry = json.getJSONArray("http://52.192.83.162:8080/load_total_time",params);

            return jAry;
        }
        @Override
        protected void onPostExecute(JSONArray json) {

            for (int i = 0; i < json.length(); i++) {
                JSONObject c = null;
                try {
                    c = json.getJSONObject(i);
                    NonFacebookSigninFragment.name = c.getString("name");
                    total_connect_time = c.getInt("total_connect_time");
                    //TV_score.setText(c.getInt("total_connect_time"));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private class SaveTotalTime extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM","")));
            //접속종료시간 업데이트 -> backpressed 이거나 onDestroy에
            connect_finish_moment = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
            params.add((new BasicNameValuePair("connect_finish_moment", connect_finish_moment)));
            params.add((new BasicNameValuePair("total_connect_time",String.valueOf(total_connect_time+getByTime()))));

            JSONObject jObj = json.getJSONFromUrl("http://52.192.83.162:8080/save_total_time", params);
            return jObj;

        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                String res = json.getString("response");
                if(res.equals("Save Total Time Success")) {

                    /*
                    Fragment reg = new UserFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, reg);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
                    */
                }else{
                    Toast toast = Toast.makeText(getActivity(), res, Toast.LENGTH_SHORT);
                    int offsetX = 0;
                    int offsetY = 0;
                    toast.setGravity(Gravity.CENTER, offsetX, offsetY);
                    toast.show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private int getByTime(){
        String tempFinishTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        Log.i("asd", "connect_start_moment" + NonFacebookSigninFragment.connect_start_moment);
        Log.i("asd", "connect_finish_moment" + tempFinishTime);
        Log.i("qwe", "qwe" + new String("12:34:56").substring(2,4));

        String tempStartTime = NonFacebookSigninFragment.connect_start_moment;
        //Log.i("asd", "zxc2" + tempStartTime.indexOf(' ') + " " + tempStartTime.indexOf('-') + " " + tempStartTime.indexOf(':') + " " + tempStartTime.length());
        //10, 4, 13, 19
        int tempHourStart;
        int tempHourFinish;

        tempHourStart = Integer.parseInt(tempStartTime.substring(tempStartTime.indexOf(' ')+1, tempStartTime.indexOf(':')));
        tempHourFinish = Integer.parseInt(tempFinishTime.substring(tempFinishTime.indexOf(' ')+1, tempFinishTime.indexOf(':')));


        int tempMinuteStart_index = tempStartTime.indexOf(':', tempStartTime.indexOf(':')+1);
        int tempMinuteFinish_index = tempFinishTime.indexOf(':', tempFinishTime.indexOf(':')+1);
        int tempMinuteStart = Integer.parseInt(tempStartTime.substring(tempStartTime.indexOf(':')+1, tempMinuteStart_index));
        int tempMinuteFinish = Integer.parseInt(tempFinishTime.substring(tempFinishTime.indexOf(':')+1, tempMinuteFinish_index));

        int tempSecondStart = Integer.parseInt(tempStartTime.substring(tempMinuteStart_index+1, tempStartTime.length()));
        int tempSecondFinish = Integer.parseInt(tempFinishTime.substring(tempMinuteFinish_index+1, tempFinishTime.length()));



        int tempYearStart = Integer.parseInt(tempStartTime.substring(0, tempStartTime.indexOf('-')));
        int tempYearFinish = Integer.parseInt(tempFinishTime.substring(0, tempFinishTime.indexOf('-')));

        int tempMonthStart_index = tempStartTime.indexOf('-', tempStartTime.indexOf('-')+1);
        int tempMonthFinish_index = tempFinishTime.indexOf('-', tempFinishTime.indexOf('-')+1);
        int tempMonthStart = Integer.parseInt(tempStartTime.substring(tempStartTime.indexOf('-')+1, tempMonthStart_index));
        int tempMonthFinish = Integer.parseInt(tempFinishTime.substring(tempFinishTime.indexOf('-')+1, tempMonthFinish_index));

        int tempDayStart_index = tempStartTime.indexOf(' ');
        int tempDayFinish_index = tempFinishTime.indexOf(' ');
        int tempDayStart = Integer.parseInt(tempFinishTime.substring(tempMonthStart_index+1, tempDayStart_index));
        int tempDayFinish = Integer.parseInt(tempFinishTime.substring(tempMonthFinish_index+1, tempDayFinish_index));

        int Y = tempYearFinish - tempYearStart;
        int M = tempMonthFinish - tempMonthStart;
        int D = tempDayFinish - tempDayStart;

        int h = tempHourFinish - tempHourStart;
        int m = tempMinuteFinish - tempMinuteStart;
        int s = tempSecondFinish - tempSecondStart;

        Log.i("asd", "Y" + Y);
        Log.i("asd", "M" + M);
        Log.i("asd", "D" + D);

        Log.i("asd", "h" + h);
        Log.i("asd", "m" + m);
        Log.i("asd", "s" + s);

        return (Y*365*31*24*3600 + M*31*24*3600 + D*24*3600 + h*3600 + m*60 + s);
    }

}