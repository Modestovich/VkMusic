package mody.vkmusic.Fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import mody.vkmusic.ListActivity;
import mody.vkmusic.MainActivity;
import mody.vkmusic.Player.MusicPlayer;
import mody.vkmusic.Player.Repeat;
import mody.vkmusic.R;
import mody.vkmusic.Song.Song;

public class PlayerHeader extends Fragment {

    private Button repeatButton;
    private Button randomButton;
    private Button lyricsButton;
    private LinearLayout subWrapper;
    private LinearLayout backgroundKeeper;
    private TextView lyricsView;
    private View.OnClickListener backClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent tabsActivity = new Intent(
            getActivity().getApplicationContext(), MainActivity.class);
            Bundle bndlanimation =
                ActivityOptions.makeCustomAnimation(getActivity().getApplicationContext(),
                        R.anim.current,R.anim.next)
                    .toBundle();
            tabsActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(tabsActivity, bndlanimation);
        }
    };
   private View.OnClickListener hideControls = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            subWrapper.setVisibility(View.INVISIBLE);
        }
    };

    private View.OnClickListener showControls = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            subWrapper.setVisibility(View.VISIBLE);
        }
    };
    private View.OnClickListener repeatClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setNextRepeat();
        }
    };
    private View.OnClickListener randomClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setRandom(MusicPlayer.setRandom());
        }
    };
    private View.OnClickListener lyricsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer lyrId = MusicPlayer.getCurrentSong().getLyricsId();
            if(lyricsView.getVisibility()==View.INVISIBLE) {
                if (lyrId > 0) {
                    lyricsView.setVisibility(View.VISIBLE);
                    lyricsButton.setText(R.string.player_lyrics_hide);
                }
            }else {
                lyricsButton.setText(R.string.player_lyrics_show);
                lyricsView.setVisibility(View.INVISIBLE);
            }
        }
    };
    private SeekBar.OnSeekBarChangeListener seekChange = new SeekBar.OnSeekBarChangeListener() {
        private Integer progress = 0;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            ((TextView) getActivity().findViewById(R.id.player_progress))
                    .setText(Song.transformDuration(progress));
            this.progress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            MusicPlayer.lockAutoUpdatingSeek();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            MusicPlayer.updateSeekAfterSliding(progress*1000);
            seekBar.setProgress(progress);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_header,container);
        (view.findViewById(R.id.player_backButton)).
                setOnClickListener(backClick);
        repeatButton = (Button) view.findViewById(R.id.player_repeat);
        repeatButton.setOnClickListener(repeatClick);
        randomButton = (Button) view.findViewById(R.id.player_random);
        randomButton.setOnClickListener(randomClick);
        lyricsButton = (Button) view.findViewById(R.id.player_lyricsControl);
        lyricsButton.setOnClickListener(lyricsClick);
        subWrapper = (LinearLayout) view.findViewById(R.id.player_subWrapper);
        subWrapper.setOnClickListener(hideControls);
        lyricsView = (TextView) view.findViewById(R.id.player_lyrics);
        lyricsView.setMovementMethod(new ScrollingMovementMethod());
        lyricsView.setOnClickListener(hideControls);

        backgroundKeeper = (LinearLayout) view.findViewById(R.id.player_backgroundKeeper);
        backgroundKeeper.setOnClickListener(showControls);
        ((TextView) view.findViewById(R.id.player_artist))
            .setText(MusicPlayer.getCurrentSong().getArtist());
        ((TextView) view.findViewById(R.id.player_title))
            .setText(MusicPlayer.getCurrentSong().getTitle());
        ((TextView) view.findViewById(R.id.player_song_number))
                .setText((MusicPlayer.getPositionInList()+1)+" of "
                        + MusicPlayer.getListLength());
        ((TextView) view.findViewById(R.id.player_progress))
                .setText("0:00");
        ((TextView) view.findViewById(R.id.player_to_finish))
                .setText(MusicPlayer.getCurrentSongDuration());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((SeekBar) view.
            findViewById(R.id.player_song_progressBar)).
            setOnSeekBarChangeListener(seekChange);
    }

    /**
     * Switching text/image to UI to understand
     * what mode is selected Repeat single/No repeat/Repeat all
     */
    private void setNextRepeat(){
        Repeat.setNextRepeat();
        if(Repeat.getValue().equals(Repeat.REPEAT_ALL)) {
            repeatButton.setText(R.string.player_repeat_all);
        }else if(Repeat.getValue().equals(Repeat.REPEAT_NO_REPEAT)) {
            repeatButton.setText(R.string.player_repeat_no_repeat);
        }else{
            repeatButton.setText(R.string.player_repeat_single);
        }
    }

    /**
     * Switching text/image to UI to understand
     * what mode is selected Random/Simple
     * @param random - parameter of shuffling
     */
    private void setRandom(boolean random){
        if(random)
            randomButton.setText(R.string.player_next_random);
        else randomButton.setText(R.string.player_next_simple);
    }
}