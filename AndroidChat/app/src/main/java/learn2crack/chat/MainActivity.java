package learn2crack.chat;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String SENDER_ID = "665375685332";


    static final String TAG = "L2C";

    GoogleCloudMessaging gcm;
    SharedPreferences prefs;

    List<NameValuePair> params;



    Context context;
    String regid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        prefs = getSharedPreferences("Chat", 0);
        context = getApplicationContext();
        if(!prefs.getString("REG_FROM","").isEmpty()){ // mobno
            Fragment user = new NonFacebookSigninFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, user);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        }
        else  if(!prefs.getString("REG_ID", "").isEmpty()){ // reg_id
            Fragment reg = new NonFacebookSigninFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, reg);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();

        }else if(checkPlayServices()){

            new Register().execute();

        }else{
            Toast.makeText(getApplicationContext(),"This device is not supported",Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private class Register extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                    regid = gcm.register(SENDER_ID);
                    Log.e("RegId",regid);

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("REG_ID", regid);
                    edit.commit();

                }

                return  regid;

            } catch (IOException ex) {
                Log.e("Error", ex.getMessage());
                return "Fails";

            }
        }
        @Override
        protected void onPostExecute(String json) {
            Fragment reg = new NonFacebookSigninFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, reg);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        }
    }



}
