package learn2crack.chat;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class NonFacebookSigninFragment extends Fragment {
    SharedPreferences prefs;
    EditText ET_name, ET_mobno, ET_password;
    Button login;
    Button signup;
    List<NameValuePair> params;
    ProgressDialog progress;
    static public String name;
    static String connect_start_moment;
    private com.facebook.login.widget.LoginButton facebook_login = null;
    CallbackManager callbackManager;
    TextView info;
    Intent fbLoginIntent;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;

    String tempEmail;
    String tempName;
    String tempGender;
    String tempAge;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.i("asd", "zxc1-13");
            Log.i("asd", "zxc2");
            // 정보 받아오는 graph api
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // Application code
                            Log.v("LoginActivity", response.toString());
                            Log.e("aaa", AccessToken.getCurrentAccessToken().getUserId().toString());

                            //info.setText("id : " + tempLoginResult.getAccessToken().getUserId());
                            info.setText("email : " + object.optString("email"));
                            info.append("\nname : " + object.optString("name"));
                            info.append("\ngender : " + object.optString("gender"));

                            Log.e("aaa",""+ response.getJSONObject());

                            /*
                            try {
                                info.append("\nage range : " + object.getString("age range"));
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                            info.append("\nage range : " + object.optString("age range"));
                            */
                            info.append("\n\n위와 같이, 페이스북 정보를 받을 수 있으나 사용하지않습니다. \n본 서비스를 이용하시려면 \"Not a member\"를 클릭해주세요.");

                            tempEmail = object.optString("email");
                            tempName = object.optString("name");
                            tempGender = object.optString("gender");
                           //tempAge = object.optString("age_range");
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender");
            request.setParameters(parameters);
            request.executeAsync();


        }


        @Override
        public void onCancel() {
            info.setText("Login attempt canceled.");
            Log.i("asd", "zxc1-15");
        }

        @Override
        public void onError(FacebookException e) {
            Log.i("asd", "zxc1-16");
            info.setText("Login attempt failed.");

        }
    };

        @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity());
         View view = inflater.inflate(R.layout.fragment_non_facebook_signin, container, false);
            callbackManager = CallbackManager.Factory.create();
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    // App code
                    Log.i("eNuri", "Current Token : " + currentAccessToken);
                    if(currentAccessToken == (null)){ //로그아웃된 상태
                        info.setText("");
                        //info.setTextColor(Color.red(1));
                    }
                }
            };


            accessTokenTracker.startTracking();

        info = (TextView) view.findViewById(R.id.info);
        prefs = getActivity().getSharedPreferences("Chat", 0);
        ET_name = (EditText)view.findViewById(R.id.ET_name);
        ET_mobno = (EditText)view.findViewById(R.id.ET_mobno);
        ET_password = (EditText)view.findViewById(R.id.ET_password);
        login = (Button)view.findViewById(R.id.log_btn);
        signup = (Button) view.findViewById(R.id.signup);

        progress = new ProgressDialog(getActivity());
        progress.setMessage("Registering ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment reg = new NonFacebookSignupFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, reg);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempMobno = ET_mobno.getText().toString();
                String tempPassword = ET_password.getText().toString();

                if(tempMobno.equals("") || tempPassword.equals("")){
                    Toast toast = Toast.makeText(getActivity(), "Write your phone number and password", Toast.LENGTH_SHORT);
                    int offsetX = 0;
                    int offsetY = 0;
                    toast.setGravity(Gravity.CENTER, offsetX, offsetY);
                    toast.show();
                    return;
                }

                progress.show();
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("REG_FROM",tempMobno);
                edit.putString("FROM_PASSWORD", tempPassword);

                edit.commit();
                new Signin().execute();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton)view.findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("public_profile", "user_friends","email");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, callback);
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class Signin extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", ET_mobno.getText().toString()));
            params.add(new BasicNameValuePair("password", ET_password.getText().toString()));
            params.add((new BasicNameValuePair("reg_id",prefs.getString("REG_ID",""))));

            //앱 시작 시각 start
            connect_start_moment = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
            //앱 시작 시각 finish

            params.add((new BasicNameValuePair("connect_start_moment",connect_start_moment)));
            Log.i("asd", "connect_start_moment" + connect_start_moment);

            JSONObject jObj = json.getJSONFromUrl("http://52.192.83.162:8080/signin",params);
            return jObj;



        }
        @Override
        protected void onPostExecute(JSONObject json) {
            progress.dismiss();
            try {
                String res = json.getString("response");
                if(res.equals("Sign In Success")) {
                    Fragment reg;
                    if (ET_mobno.getText().toString().equals("0")) {
                        reg =  new AdminFragment();
                    } else {
                        reg = new UserFragment();
                    }
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, reg);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
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

}
