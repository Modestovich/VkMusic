package mody.vkmusic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

public class StartUpActivity extends AppCompatActivity {

    private String[] scope = {
            VKScope.NOHTTPS,
            VKScope.AUDIO,
            VKScope.FRIENDS,
            VKScope.PHOTOS
    };

    private VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Log.i("Access token", "Can't get appropriate access token");
            } else {
                Log.i("Access token", "Got appropriate access token");
            }
        }
    };

    VKCallback<VKSdk.LoginState> loginStateCallBack = new VKCallback<VKSdk.LoginState>() {
        @Override
        public void onResult(VKSdk.LoginState res) {
            if (res != VKSdk.LoginState.LoggedIn)
                VKSdk.login(StartUpActivity.this, scope);
            Log.i("Result", res.toString());
        }

        @Override
        public void onError(VKError error) {
            Log.i("Result", error.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        setMainTextFont();

        startVkSession();

        setAppropriateText();
    }

    private void startVkSession() {
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }

    private void setAppropriateText() {
        if(VKSdk.isLoggedIn()){
            ((Button)findViewById(R.id.login)).setText(
                    getResources().getText(R.string.ClickToContinue));
        }
    }

    private void setMainTextFont() {
        TextView mainText = (TextView) findViewById(R.id.startUpText);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/28.ttf");
        mainText.setTypeface(face);
    }

    private boolean hasInternetConnection(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Log.i("Auth", "Successful entering");
            }

            @Override
            public void onError(VKError error) {
                Log.i("Auth", "Error entering");
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void logIn(View view){
        if(!VKSdk.isLoggedIn()) {
            VKSdk.wakeUpSession(this, loginStateCallBack);
        }else {
            StartMainActivity();
        }
        vkAccessTokenTracker.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!vkAccessTokenTracker.isTracking())
            vkAccessTokenTracker.startTracking();
        setAppropriateText();
    }

    private void StartMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}