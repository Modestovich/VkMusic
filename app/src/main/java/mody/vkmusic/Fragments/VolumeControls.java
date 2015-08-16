package mody.vkmusic.Fragments;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.SeekBar;
import mody.vkmusic.R;
import mody.vkmusic.VolumeSetting.VolumeObserver;

public class VolumeControls extends Fragment {
    private SeekBar volumeBar;
    private AudioManager audioManager;
    private VolumeObserver volumeObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeVolume();
    }

    /**
     * Synchronizing changing of volume in player
     * with main volume of phone
     */
    private void initializeVolume() {
        audioManager = (AudioManager) getActivity().
                getSystemService(Context.AUDIO_SERVICE);
        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeObserver = new VolumeObserver(getActivity());
        getActivity().getContentResolver().
                registerContentObserver(android.provider.Settings.System.CONTENT_URI,true,volumeObserver);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) throws NullPointerException {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_volume,container,false);
        volumeBar = (SeekBar) view.findViewById(R.id.volumeBar);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(volumeObserver);
    }
}