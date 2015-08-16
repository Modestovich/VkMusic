package mody.vkmusic.Song.SongEvent;

import mody.vkmusic.Player.MusicPlayer;
import mody.vkmusic.Song.Song;

public class SongChangeEvent{
    private OnSongChangeListener listener;
    private volatile Song currentSong;
    private Thread startDetection;
    private Runnable detectingNewSong = new Runnable() {
        @Override
        public void run() {
            while(true) {
                Sleep(500);
                if (isNewSong()) {
                    listener.songChanged(currentSong,MusicPlayer.getCurrentSong());
                    currentSong = MusicPlayer.getCurrentSong();
                }
                if(Thread.interrupted()){
                    break;
                }
            }
        }
    };

    public SongChangeEvent(Song currentSong){
        startDetection = new Thread(detectingNewSong);
        listener = null;
        this.currentSong = currentSong;
    }

    public void setOnSongChangeListener(OnSongChangeListener listener) {
        if(this.listener!=null) {
            startDetection.interrupt();
        }
        this.listener = listener;
        startDetection.start();
    }
    private boolean isNewSong(){
        return MusicPlayer.getCurrentSong()!=null &&
                (currentSong==null ||
                        !currentSong.getId().equals(MusicPlayer.getCurrentSong().getId()));

    }
    private void Sleep(Integer milliSeconds){
        try{
            Thread.sleep(milliSeconds);
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }
    }
}
