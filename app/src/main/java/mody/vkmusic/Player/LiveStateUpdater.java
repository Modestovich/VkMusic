package mody.vkmusic.Player;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import mody.vkmusic.R;
import mody.vkmusic.Song.Song;
import mody.vkmusic.VKActions.VKActions;

public class LiveStateUpdater extends AsyncTask<Void, Integer, Void>{

    private Integer songId;
    private SeekBar barSeeking;
    private TextView textSeeking;
    private TextView lyricsView;
    private Button lyricsButton;
    private TextView artistTextView;
    private TextView songTitleTextView;
    private TextView songIndexNumberTextView;
    private TextView songDurationTextView;
    private Activity playerActivity;

    /**
     * Initializing all views to be changed while current song
     * is playing or/and changed
     * @param playerActivity - Activity which state is checking
     *                       for updates in infinite loop
     */
    public LiveStateUpdater(Activity playerActivity){
        this.playerActivity = playerActivity;
        barSeeking = (SeekBar) playerActivity.findViewById(R.id.player_song_progressBar);
        textSeeking = (TextView) playerActivity.findViewById(R.id.player_progress);
        lyricsView = (TextView) playerActivity.findViewById(R.id.player_lyrics);
        lyricsButton = (Button) playerActivity.findViewById(R.id.player_lyricsControl);
        artistTextView =  (TextView) playerActivity.findViewById(R.id.player_artist);
        songTitleTextView = (TextView) playerActivity.findViewById(R.id.player_title);
        songIndexNumberTextView = (TextView) playerActivity.findViewById(R.id.player_song_number);
        songDurationTextView = (TextView) playerActivity.findViewById(R.id.player_to_finish);
    }

    /**
     * Full updating state when song's been changed
     */
    private void updateState(){
        Song song = MusicPlayer.getCurrentSong();
        artistTextView.setText(song.getArtist());
        songTitleTextView.setText(song.getTitle());
        songIndexNumberTextView
                .setText((MusicPlayer.getPositionInList()+1)+" of "
                        + MusicPlayer.getListLength());
        songDurationTextView
                .setText(Song.transformDuration(song.getDuration()));
        barSeeking.setProgress(0);
        barSeeking.setMax(song.getDuration());
        lyricsView.setText("");
        playerActivity.findViewById(R.id.player_backgroundKeeper).setBackground(null);
    }
    private void updateLyrics(){
        Song song = MusicPlayer.getCurrentSong();
        if(song.hasLyrics()){
            VKActions.setLyrics(song.getLyricsId(), playerActivity);
            lyricsView.setAlpha(1.0f);
            lyricsButton.setEnabled(true);
        }else {
            lyricsView.setAlpha(0.0f);
            lyricsButton.setEnabled(false);
        }
    }
    private void updateProgress(Integer... values) {
        if (values.length>0) {
            if (values[0] < 0)//while processing new song duration is null so this is the resolve of problem
                values[0] = 0;
            barSeeking.setProgress(values[0] / 1000);
            textSeeking.setText(Song.transformDuration(values[0] / 1000));
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     *  Infinite loop checking updates of state of player:
     * 1. SeekBar.
     * 2. Changing the song changes lyrics/lyrics status,
     *    changes name of artist and song, position in list
     * @param params - no parameters required
     * @return - nothing is returned
     */
    @Override
    protected Void doInBackground(Void... params) {
        barSeeking.setProgress(0);
        barSeeking.setMax(MusicPlayer.
                getCurrentSong().getDuration());
        songId = -1;//MusicPlayer.getCurrentSong().getId();
        while(true){
            Sleep(500);
            if(MusicPlayer.getCurrentSong()!=null) {
                makeUpdates();
            }else{
                songId = -1;
            }
            if(isCancelled())
                break;
        }
        return null;
    }

    /**
     * Changing state Live (seeking),
     * updating BG picture in particular time
     * and refreshing song's lyrics
     * @param values - current seeking of playing song
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(isNewSong()){
            updateState();
            updateLyrics();
            songId = MusicPlayer.getCurrentSong().getId();
        }else {
            if (MusicPlayer.isCanSeek()) {
                updateProgress(values);
            }
        }
    }

    private void makeUpdates(){
        if (isNewSong()) {
            publishProgress();
            VKActions.setBackground(playerActivity);
            //do this here 'cause
            //can't send request not in main Thread
        } else {
            publishProgress(MusicPlayer.getSeeking());
        }
    }
    private void Sleep(Integer milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    private boolean isNewSong(){
        return !songId.equals(MusicPlayer.getCurrentSong().getId());
    }
}