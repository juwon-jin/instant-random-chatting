package learn2crack.chat;



import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class NonFacebookSignupFragment extends Fragment {
    SharedPreferences prefs;
    EditText ET_name, ET_mobno, ET_password;
    Button login;
    List<NameValuePair> params;
    ProgressDialog progress;

    String name_temp;
    String mobno_temp;



    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_non_facebook_signup, container, false);

        prefs = getActivity().getSharedPreferences("Chat", 0);

        ET_name = (EditText)view.findViewById(R.id.ET_name);
        ET_mobno = (EditText)view.findViewById(R.id.ET_mobno);
        ET_password = (EditText)view.findViewById(R.id.ET_password);
        login = (Button)view.findViewById(R.id.log_btn);
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Registering ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempMobno = ET_mobno.getText().toString();
                String tempName = ET_name.getText().toString();
                String tempPassword = ET_password.getText().toString();
                if(tempMobno.equals("") || tempPassword.equals("") || tempPassword.equals("")){
                    Toast toast = Toast.makeText(getActivity(), "Write your information", Toast.LENGTH_SHORT);
                    int offsetX = 0;
                    int offsetY = 0;
                    toast.setGravity(Gravity.CENTER, offsetX, 300);
                    toast.show();
                    return;
                }

                progress.show();
                Log.d("Asfd1","Asfd1");
                SharedPreferences.Editor edit = prefs.edit();
                Log.d("Asfd2","Asfd1");
                edit.putString("REG_FROM", tempMobno);
                Log.d("Asfd3", "Asfd1");
                edit.putString("FROM_NAME", tempName);
                edit.putString("FROM_PASSWORD", tempPassword);

                edit.commit();
                Log.d("Asfd4", "Asfd1");
                new Signup().execute();
                Log.d("Asfd5", "Asfd1");
            }
        });

        return view;
    }
    private class Signup extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            Log.d("Asfd7","Asfd1");
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            try {
                //params.add(new BasicNameValuePair("name", ET_name.getText().toString()));
                params.add(new BasicNameValuePair("name", URLEncoder.encode(ET_name.getText().toString(), "utf-8")));
            }catch (Exception ex){
                ex.printStackTrace();
            }
            params.add(new BasicNameValuePair("mobno", ET_mobno.getText().toString()));
            params.add(new BasicNameValuePair("password", ET_password.getText().toString()));
            params.add((new BasicNameValuePair("reg_id",prefs.getString("REG_ID",""))));
            Log.d("Asfd6", "Asfd1");
            //앱 설치했던 시각 start
            long first_install_moment = 0;
            try {
                PackageManager pm = getActivity().getPackageManager();
               // ApplicationInfo appInfo = pm.getApplicationInfo("app.package.name", 0);
                ApplicationInfo appInfo = pm.getApplicationInfo("learn2crack.chat", 0);
                String appFile = appInfo.sourceDir;
                first_install_moment = new File(appFile).lastModified(); //Epoch Time
                Log.i("asd", "zxc2" + getData(first_install_moment));
            }catch(Exception ex){
                ex.printStackTrace();
            }
            //앱 설치했던 시각  finish
            Log.d("Asfd8","Asfd1");
            //앱 시작 시각 start
            NonFacebookSigninFragment.connect_start_moment = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
            //앱 시작 시각 finish
            params.add((new BasicNameValuePair("connect_start_moment",NonFacebookSigninFragment.connect_start_moment)));
            params.add((new BasicNameValuePair("first_install_moment",String.valueOf(getData(first_install_moment)))));
            Log.d("Asfd9", "Asfd1");
            Log.i("makejin", "params : " + params);

            JSONObject jObj = json.getJSONFromUrl("http://52.192.83.162:8080/signup",params);
            return jObj;



        }

        @Override
        protected void onPostExecute(JSONObject json) {
            progress.dismiss();
            try {
                String res = json.getString("response");
                if(res.equals("Sucessfully Registered")) {
                    Fragment reg = new UserFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, reg);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
                }else{
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
    private static String getData(long datetime) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datetime);
        String strDate = formatter.format(calendar.getTime());

        return strDate;
    }

}