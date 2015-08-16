package mody.vkmusic.Song;

import com.vk.sdk.api.model.VKApiAudio;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Song{

    /**
     * params of song
     */
    private String title;
    private Integer id;
    private String artist;
    private String url;
    private Integer lyrics_id;
    private Integer duration;
    private String lyricsText;
    private boolean hasImage;

    public Song(VKApiAudio song){
        title = song.title;
        id = song.id;
        artist = song.artist;
        url = song.url;
        lyrics_id = song.lyrics_id;
        duration = song.duration;
    }
    public Song(JSONObject json){
        try {
            title = json.getString("title");
            id = json.getInt("id");
            artist = json.getString("artist");
            url = json.getString("url");
            lyrics_id = json.getInt("lyrics_id");
            duration = json.getInt("duration");
            if (json.has("lyricsText")) {
                lyricsText = json.getString("lyricsText");
                hasImage = json.getBoolean("hasImage");
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }
    }
    public String getName(){
        return this.title;
    }
    public String getTransformedDuration(){
        return transformDuration(this.duration);
    }
    private String getExtension(){
        String url = this.url;
        return url.substring(url.lastIndexOf("."),
                url.lastIndexOf("?")>-1?url.lastIndexOf("?"):url.length());
    }
    public String getFileNameToSave(){
        return this.artist+"-"+this.title+this.getExtension();
    }
    public String getNameToFilter(){
        return this.artist+" "+this.title;
    }

    /**
     * Transforming duration to the UI displaying
     * 64 sec = 1:04
     * @param duration - current time to be transformed into UI view
     * @return - transformed duration
     */
    public static String transformDuration(Integer duration){
        String convertedDuration = duration/60 + ":";//calculate number of minutes
        convertedDuration += (duration%60>=10) ? duration%60 : "0"+duration%60;
        return convertedDuration;
    }

    /**
     * Make value for search background in player
     * @param song - song to be searched
     * @return - search query for get request to find background image
     */
    public static String transformNameForBgSearch(Song song){
        String searchValue = song.getArtist()+" "+song.getTitle();
        searchValue = searchValue.replaceAll("\\(.*?\\)", " ").trim()
                .replaceAll("\\s+","+");
        return searchValue;
    }

    public Integer getId(){
        return this.id;
    }
    public String getArtist(){
        return this.artist;
    }
    public String getTitle(){
        return this.title;
    }
    public String getURL(){
        return this.url;
    }
    public Integer getLyricsId(){
        return this.lyrics_id;
    }
    public Integer getDuration(){
        return this.duration;
    }
    public boolean hasLyrics(){
        return getLyricsId()>0;
    }
    /**
     * Compare two songs by id.
     * @param song - comparable object
     * @return :
     *      - true - if songs have same ids
     *      - false - if songs have different ids
     */
    @Override
    public boolean equals(Object song) {
        return (song!=null && this.getId().equals(((Song)song).getId()));
    }

    /*public static JSONObject collectionToJSON(Collection<Song> songs){
        JSONObject finalJSON = new JSONObject();
        try {
            JSONArray json = new JSONArray();
            for(Song song:songs){
                json.put(song.toJSON());
            }
            finalJSON.put(LocalStorage.VK_SONG_LIST,(Object)json);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return finalJSON;
    }

    private JSONObject toJSON(String... extraData){
        JSONObject json = new JSONObject();
        try {
            json.put("id", this.getId());
            json.put("artist", this.getArtist());
            json.put("title", this.getTitle());
            json.put("url", this.getURL());
            json.put("duration", this.getDuration());
            json.put("lyrics_id", this.getLyricsId());
            if(extraData.length>0){
                json.put("lyricsText",extraData[0]);
                json.put("hasImage",extraData[1].length()>0);
            }
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return json;
    }

    public static ArrayList<Song> jsonToCollection(JSONObject json){
        ArrayList<Song> songsList = new ArrayList<Song>();
        try{
            JSONArray jArray = json.getJSONArray(LocalStorage.VK_SONG_LIST);
            for(int i=0;i<jArray.length();i++){
                songsList.add(new Song((JSONObject) jArray.get(i)));
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return songsList;
    }*/

    /**
     * This is tet function
     * @param json - appearance of song in json
     * @return simply string with key : value parameters of song
     */
    private static String jsonSongToString(JSONObject json){

        String songToString = "";
        try {
            Iterator params = json.keys();
            while (params.hasNext()) {
                String key = (String) params.next();
                songToString += key + ":" + json.get(key).toString() + "\n";
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return songToString;
    }
}