package learn2crack.chat;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

public class FacebookLoginActivity extends Activity {
    CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    TextView info;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    Intent fbLoginIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook_login);

        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        facebookLoginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        info = (TextView) findViewById(R.id.info);

        Log.i("asd", "zxc " + facebookLoginButton.getText().toString());

        /*
        if(facebookLoginButton.getText().toString().equals("Log out")){
            Intent nonFacebookSigninIntent = new Intent(getApplication(),NonFacebookSigninActivity.class);
            nonFacebookSigninIntent.putExtra("isFacebookFlag","1");
            startActivity(nonFacebookSigninIntent);
        }
*/
        Log.i("asd", "zxc1-11");
        try {
            Log.i("asd", "zxc2 " + facebookLoginButton.getText().toString());
            facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.i("asd", "zxc1-13");
                    // 로그인되어있는지 임시적으로 확인하려고 사용
                    info.setText(
                            "User ID: "
                                    + loginResult.getAccessToken().getUserId()
                                    + "\n" +
                                    "Auth Token: "
                                    + loginResult.getAccessToken().getToken()
                    );

                    Log.i("asd", "zxc2");

                    // 정보 받아오는 graph api
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    // Application code
                                    Log.v("LoginActivity", response.toString());
                                    info.setText(object.optString("email"));
                                    Log.e("aaa", object.optString("birthday"));
                                    Log.e("aaa", AccessToken.getCurrentAccessToken().getUserId().toString());

                                    fbLoginIntent = new Intent(getApplicationContext(), MainActivity.class);

                                    Log.e("aaa", "1");
                                    fbLoginIntent.putExtra("isFacebookFlag", "1");
                                    fbLoginIntent.putExtra("email", object.optString("email"));
                                    fbLoginIntent.putExtra("name",object.optString("name"));

                                    Log.e("aaa", "2");
                                    startActivity(fbLoginIntent);
                                    Log.e("aaa", "3");
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,birthday");
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
            });

        } catch (Exception ex) {
            Log.i("asd", "zxc1-14");
        }
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {


                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
            }
        };
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

}
