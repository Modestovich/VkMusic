package mody.vkmusic.VKActions;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.TextView;
import mody.vkmusic.Player.MusicPlayer;
import mody.vkmusic.R;
import mody.vkmusic.Song.Song;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import com.vk.sdk.api.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.AbstractHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.concurrent.*;

public class VKActions {

    private static String lyricsText;
    private static Activity playerActivity;
    private static final String iTunesRequestTemplate = "https://itunes.apple.com/search?limit=1&term=";
    private static final Integer REQUEST_TIMEOUT = 2000;

    private static VKRequest.VKRequestListener requestLyrics = new VKRequest.VKRequestListener() {
        @Override
        public void onError(VKError error) {
            lyricsText = "";
            Log.i("Lyrics error", error.toString());
        }

        @Override
        public void onComplete(VKResponse response) {
            try {
                lyricsText = new JSONObject(response.responseString)
                        .getJSONObject("response").getString("text");
                ((TextView)playerActivity.findViewById(R.id.player_lyrics))
                        .setText(lyricsText);
            }catch(JSONException ex){
                Log.i("JSON",response.responseString);
            }
        }
    };

    /**
     * Get lyrics of current playing song
     * @param lyrics_id - id of lyrics of current song
     * @param sourceActivity - activity where textView should be changed
     *            after finishing request processing
     */
    public static void setLyrics(Integer lyrics_id,Activity sourceActivity) {
        VKActions.playerActivity = sourceActivity;
        VKApi.audio().getLyrics(
                VKParameters.from("lyrics_id", lyrics_id)).
                executeWithListener(requestLyrics);
    }

    /**
     * Set background of player depending on current playing song
     * If response is empty show standard background
     * @param sourceActivity - activity to be changed after successful
     *            receiving response
     */
    public static void setBackground(final Activity sourceActivity){
        VKActions.playerActivity = sourceActivity;
        String searchQuery = Song.transformNameForBgSearch(
                MusicPlayer.getCurrentSong());
        String requestUrl = iTunesRequestTemplate + searchQuery;
        HttpResponse response = requestWithTimeout(requestUrl,REQUEST_TIMEOUT);
        String responseString = "";
        try {
            responseString = response != null ? EntityUtils.toString(response.getEntity()) : "";
        }catch(IOException ex){
            Log.i("IO","Can't get response string");
        }
        Drawable image = responseString.length()>0 ? getBackgroundImageDrawable(responseString)
                : null;
        setBackgroundInView(image);
    }

    private static HttpResponse requestWithTimeout(final String requestUrl,Integer timeOut){
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(new URI(requestUrl));
            HttpConnectionParams.setConnectionTimeout(client.getParams(),timeOut);
            HttpConnectionParams.setSoTimeout(client.getParams(), timeOut);
            return client.execute(request);
        } catch (URISyntaxException e) {
            Log.i("URISyntaxException", "ex");
        } catch (ClientProtocolException e) {
            Log.i("ClientProtocolException", "ex");
        } catch (IOException e) {
            Log.i("IOException", "ex");
        } catch(NetworkOnMainThreadException ex ){
            Log.i("NetworkThreadException", "This has to be done in mainThread");
        }
        return null;
    }

    private static Drawable getBackgroundImageDrawable(String responseString){
        try {
            JSONObject responseJSON = new JSONObject(responseString);
            if ((Integer) responseJSON.get("resultCount") != 0) {
                String bgURL = getBackgroundImageURL(responseJSON);
                return getImageFromUrl(bgURL);
            }
        }catch (JSONException ex){
            Log.i("JSONEx","Error while making JSON object from response");
        }
        return null;
    }

    private static String getBackgroundImageURL(JSONObject responseJSON){
        try {
            String bgURL =
            (String)((JSONObject)responseJSON.getJSONArray("results").get(0))
                    .get("artworkUrl100");
            if(bgURL.length()>0)
                bgURL = bgURL.replace("100x100","600x600");
            return bgURL;
        }catch(JSONException ex){
            Log.i("json","Error while getting needed data from response");
        }
        return "";
    }

    /**
     * Get object(DrawableImage) for background if response isn't empty
     * otherwise return null
     * @param bgURL - URL of image to be background
     * @return - object(DrawableImage) for background
     */
    private static Drawable getImageFromUrl(String bgURL){
        InputStream is;
        try {
            is = (InputStream) new URL(bgURL).getContent();
            return new BitmapDrawable(playerActivity.getResources(),is);
        }catch(IOException ex){
            Log.i("IOException","Failed to open stream");
        }
        return null;
    }

    /**
     * Setting background for player
     * @param image - object (DrawableImage) to be background for player
     */
    private static void setBackgroundInView(final Drawable image){
        VKActions.playerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                (VKActions.playerActivity.findViewById(R.id.player_backgroundKeeper))
                        .setBackground(image);
            }
        });
    }
}