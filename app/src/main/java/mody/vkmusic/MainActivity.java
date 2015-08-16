package mody.vkmusic;

import android.app.TabActivity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

import java.util.List;

public class MainActivity extends TabActivity {

    private TabHost.TabSpec spectacular;
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerTabHost();

        createTabs();

    }

    private void registerTabHost(){
        tabHost = getTabHost();
    }

    private void createTabs() {
        tabHost.addTab(getSongListTab());

        spectacular = tabHost.newTabSpec("SecondTabTag");
        spectacular.setIndicator("2 Tab");
        spectacular.setContent(R.id.tab2);
        tabHost.addTab(spectacular);

        spectacular = tabHost.newTabSpec("ThirdTabTag");
        spectacular.setIndicator("3 Tab");
        spectacular.setContent(R.id.tab3);
        tabHost.addTab(spectacular);
    }

    public TabHost.TabSpec getSongListTab() {
        spectacular = tabHost.newTabSpec("SongList");
        View view = getLayoutInflater().inflate(R.layout.download_tab,null);
        spectacular.setIndicator(view);
        spectacular.setContent(new Intent(this,ListActivity.class));
        return spectacular;
    }
}