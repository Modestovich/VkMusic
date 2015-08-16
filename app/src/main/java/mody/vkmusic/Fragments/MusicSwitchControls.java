package mody.vkmusic.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import mody.vkmusic.Player.MusicPlayer;
import mody.vkmusic.R;

public class MusicSwitchControls extends Fragment {

    private Button startPauseBut;
    private boolean isPlaying;
    private Integer prevId;

    private View.OnClickListener prevTrack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            prevId = MusicPlayer.getCurrentSong().getId();
            MusicPlayer.Prev();
            if (!isPlaying) {
                setTextAndPlay(getActivity().getString(R.string.player_pause), true);
            }
            if (prevId.equals(MusicPlayer.getCurrentSong().getId()))
                ((TextView) getActivity().findViewById(R.id.player_lyrics)).setText("");
        }
    };
    private View.OnClickListener nextTrack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            prevId = MusicPlayer.getCurrentSong().getId();
            MusicPlayer.Next();
            if (!isPlaying) {
                setTextAndPlay(getActivity().getString(R.string.player_pause), true);
            }
            if (prevId.equals(MusicPlayer.getCurrentSong().getId()))
                ((TextView) getActivity().findViewById(R.id.player_lyrics)).setText("");
        }
    };
    private View.OnClickListener startPauseTrack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isPlaying){
                MusicPlayer.Pause();
                setTextAndPlay(getActivity().getString(R.string.player_play),false);
            }else {
                MusicPlayer.Continue();
                setTextAndPlay(getActivity().getString(R.string.player_pause),true);
            }
        }
    };

    /**
     * Setting text/image for play/pause button
     * @param text - text/image of play/pause button
     * @param play - flag detecting if Player is playing or not
     *               This was made in that case that while song
     *               isn't ready to play yet, user can't pause song
     */
    private void setTextAndPlay(String text,boolean play){
        startPauseBut.setText(text);
        isPlaying = play;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_switch_controls,container);
        startPauseBut = (Button) view.findViewById(R.id.player_pause_play);
        startPauseBut.setOnClickListener(startPauseTrack);
        (view.findViewById(R.id.player_next_track))
                .setOnClickListener(nextTrack);
        (view.findViewById(R.id.player_prev_track))
                .setOnClickListener(prevTrack);
        isPlaying = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTextAndPlay(getActivity().getString(R.string.player_pause),true);
    }
}