package mody.vkmusic.Player;

import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import mody.vkmusic.Song.Song;
import java.io.IOException;
import java.security.AccessController;
import java.util.*;

public final class MusicPlayer {

    private static MediaPlayer player = null;
    private static ArrayList<Song> songs;
    private static int currentSeeking;
    private static Song currentSong;
    private static Integer currentPositionInList;
    private static boolean isPlaying;
    private static boolean canSeek;
    private static boolean randomFlag = false;
    private static ArrayList<Integer> sequence = new ArrayList<Integer>();
    private static Integer positionInSequence=0;
    private static boolean isNext;
    private static Random random = new Random();
    private static Integer errorWhat = 0;
    private static Integer errorExtra = 0;

    /**
     * MediaPlayer is waiting for source to load and only then begin working
     */
    private static MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            isPlaying = true;
            canSeek = true;
        }
    };

    /**
     * Current song is finished so automatically switching next
     */
    private static MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            player.seekTo(0);
            if(noErrors()) {
                Next();
            }
            errorWhat = 0;
            errorExtra = 0;
        }
    };

    private static boolean noErrors(){
        return (errorExtra==0 && errorWhat==0);
    }

    private static MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            errorWhat = what;
            errorExtra = extra;
            return false;
        }
    };

    private MusicPlayer(){
    }

    /**
     * Main function of player. It is calling when song is finished
     * or while switching songs.
     * @param song - current song to play
     * @param position - current position of song in list
     */
    public static void Start(Song song, Integer position){
        if (player != null) {
            if(!song.equals(currentSong))
                player.reset();
            else return;//continue playing current song
        }else {
            player = new MediaPlayer();
            player.setOnPreparedListener(preparedListener);
            player.setOnCompletionListener(completionListener);
            player.setOnErrorListener(errorListener);
        }
        try {
            player.setDataSource(song.getURL());
        } catch (IOException ex) {
            Log.i("Invalid url", "Invalid url");
        }
        try {
            player.prepareAsync();
        } catch (IllegalStateException e) {
            Log.i("Incorrect data source", "Error");
        }
        currentSong = song;
        currentPositionInList = position;
    }

    /**
     * Pause player and remember place it stops.
     */
    public static void Pause(){
        player.pause();
        currentSeeking = player.getCurrentPosition();
        isPlaying = false;
    }

    /**
     * Continue playing song from place it ends.
     */
    public static void Continue(){
        player.seekTo(currentSeeking);
        player.start();
        isPlaying = true;
    }

    /**
     * Switching previous song. Checking all options:
     * repeat and shuffle
     */
    public static void Prev() {
        isNext = false;
        startNecessarySong();
    }

    /**
     * Switching next song. Checking all options:
     * repeat and shuffle
     */
    public static void Next(){
        isNext = true;
        startNecessarySong();
    }

    /**
     * Starts Next/Prev song depending on conditions:
     * direction(next/prev),shuffle, repeat
     */
    private static void startNecessarySong(){
        currentSeeking = 0;
        player.seekTo(currentSeeking);
        isPlaying = true;
        if(!isCurrentRepeatOnly()){
            synchronized(AccessController.getContext()){
                setCurrentPosition();
            }
            synchronized(AccessController.getContext()){
                Start(songs.get(currentPositionInList),currentPositionInList);
            }
        }else{
            player.start();//this is needed when song had been fully played
        }
    }

    /**
     * Detect the only one song to play
     * @return true if state is to repeat only song
     */
    private static boolean isCurrentRepeatOnly(){
        return Repeat.getValue().equals(Repeat.REPEAT_SINGLE) ||
                (songs.size()==1 && Repeat.getValue().equals(Repeat.REPEAT_NO_REPEAT));
    }

    /**
     * Finally set current position with particular conditions
     */
    private static void setCurrentPosition(){
        if(isNext){
            if(randomFlag){
                if(positionInSequence==sequence.size()-1){//last element of sequence
                    addNewRandomValueToSequence();
                    currentPositionInList = sequence.get(sequence.size()-1);
                }else{
                    currentPositionInList = sequence.get(++positionInSequence);
                }
            }else{
                if(currentPositionInList!=songs.size()-1){//NOT last element in list of songs
                    currentPositionInList++;
                }else{
                    if(Repeat.getValue().equals(Repeat.REPEAT_ALL))//last element in list of songs -> next is 1st
                        currentPositionInList = 0;
                }
            }
        }else{
            if(randomFlag){
                if(positionInSequence!=0)//NOT THE FIRST element in sequence
                    currentPositionInList = sequence.get(--positionInSequence);
            }else{
                if(currentPositionInList!=0)//NOT THE FIRST element in list of songs
                    currentPositionInList--;
            }
        }
    }

    /**
     * Get random value depending on
     * what repeat is chosen and
     * what elements are inside.
     */
    private static void addNewRandomValueToSequence(){
        if(sequence.size()==songs.size() && Repeat.getValue().equals(Repeat.REPEAT_NO_REPEAT)){
            return;
        }
        Integer randomNumber = random.nextInt(songs.size());
        if(Repeat.getValue().equals(Repeat.REPEAT_NO_REPEAT)){
            if(sequence.size()!=songs.size()){
                while(sequence.contains(randomNumber)){//get unique value excluding existing in sequence
                    randomNumber = random.nextInt(songs.size());
                }
            }
        }
        positionInSequence++;
        sequence.add(randomNumber);
    }

    /**
     * Set new list of songs for player
     * @param songs - list of songs for player
     */
    public static void populateMusicPlayer(ArrayList<Song> songs){
        MusicPlayer.songs = songs;
    }

    /**
     * Get current song of player
     * @return current song of player if exists
     */
    public static Song getCurrentSong(){
        return currentSong==null? null : currentSong;
    }

    /**
     * Get duration for player showing current duration
     * @return duration in necessary format mm:ss
     */
    public static String getCurrentSongDuration(){
        return currentSong.getTransformedDuration();
    }

    /**
     * This is necessary for disabling
     * play/stop button. Don't let
     * user to click until isn't playing.
     * @return state of player.
     */
    public static boolean isPlaying(){
        return isPlaying;
    }

    /**
     * Get position of current song in list
     * for making sign in header which one is playing.
     * @return position of current song in list.
     */
    public static Integer getPositionInList(){
        return currentPositionInList;
    }

    /**
     * Get size of list in player
     * for making sign in header
     * @return size of current list in player.
     */
    public static Integer getListLength(){
        return songs!=null? songs.size() : 0;
    }

    /**
     * Get currently played time of song for updating seekBar
     * @return current millisecond of song playing.
     */
    public static Integer getSeeking(){
        try {
            return player.getCurrentPosition();
        }catch(NullPointerException ex){
            Log.i("Seek is null",ex.getMessage()+"!");
        }
        return 0;
    }

    /**
     * Clearing state of player if choose new song
     * from listView
     * @param song - selected new song to play
     * @param position - selected new song position
     */
    public static void startAfterClearingConditions(Song song, Integer position){
        currentSeeking = 0;
        sequence.clear();
        positionInSequence = 0;
        if(getRandomState()) sequence.add(position);
        Start(song,position);
    }

    /**
     *
     * @return - if random state is on  - true , otherwise - false
     */
    public static boolean getRandomState(){
        return randomFlag;
    }

    /**
     * Make new state of random mode by the way cleaning
     * sequence if it's needed.
     * @return true if random mode is selected
     * and false otherwise
     */
    public static boolean setRandom(){
        if(randomFlag){
            randomFlag = false;
            sequence.clear();
        }else {
            randomFlag = true;
            positionInSequence = 0;
            if(currentSong!=null)
                sequence.add(currentPositionInList);
        }
        return randomFlag;
    }

    /**
     * While updating seek of current song
     * auto updating should be locked
     * because only after mouse up
     * it could be updated finally
     */
    public static void lockAutoUpdatingSeek(){
        canSeek = false;
    }

    /**
     *  When mouse up event fires on seekBar
     *  player's seeking has to be updated
     * @param progress - current progress of seekBar
     */
    public static void updateSeekAfterSliding(Integer progress){
        canSeek = true;
        player.seekTo(progress);
        currentSeeking = progress;
    }

    /**
     * This flag return if user is sliding seekBar or not
     * So can be auto updating of seekBar modified
     * @return - if user is sliding seekBar or not
     */
    public static boolean isCanSeek(){
        return canSeek&&player.getCurrentPosition()<player.getDuration();
    }
}