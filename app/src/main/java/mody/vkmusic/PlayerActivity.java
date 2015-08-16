package mody.vkmusic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import mody.vkmusic.Player.LiveStateUpdater;

public class PlayerActivity extends Activity {

    /**
     * Task for Live updating seeking music ,
     * changing position in list ,
     * song artist and song name
     */
    private LiveStateUpdater updateScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        startUpdater();//Infinity listening for updating time of song
    }

    /**
     * Starts infinite loop for updating state of player
     */
    private void startUpdater(){
        updateScreen = new LiveStateUpdater(PlayerActivity.this);
        updateScreen.execute();
    }

    /**
     * Destroying infinite loop with activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateScreen.cancel(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateScreen.cancel(true);
        updateScreen = null;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(updateScreen==null) {
            updateScreen = new LiveStateUpdater(PlayerActivity.this);
            updateScreen.execute();
        }
    }
}