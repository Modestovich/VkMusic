package mody.vkmusic.Song.SongEvent;

import mody.vkmusic.Song.Song;

public interface OnSongChangeListener {
    void songChanged(Song oldSong, Song newSong);
}
