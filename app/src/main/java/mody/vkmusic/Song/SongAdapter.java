package mody.vkmusic.Song;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import mody.vkmusic.Player.MusicPlayer;
import mody.vkmusic.R;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter implements Filterable{

    private ArrayList<Song> fullListOfSongs;
    private ArrayList<Song> temporaryData;
    private Context context;

    public SongAdapter(Context context, ArrayList<Song> songs){
        fullListOfSongs = songs;
        temporaryData = songs;
        this.context = context;
    }

    /**
     * Rendering each visible item from listView consequentially
     * When one of them is currently playing - obligatorily making a mark on it
     * @param position - position of item to be rendered
     * @param convertView - view of single list item
     * @param parent - parent of listView
     * @return - item that has been rendered (inflated)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.list_view_music,
                    parent,false);
        }
        Song currentSong = temporaryData.get(position);
        ((TextView) view.findViewById(R.id.song_name))
                .setText(currentSong.getTitle());
        ((TextView) view.findViewById(R.id.artist_name))
                .setText(currentSong.getArtist());
        ((TextView) view.findViewById(R.id.song_duration))
                .setText(currentSong.getTransformedDuration());
        if(isCurrentlyPlaying(currentSong)) {
            view.findViewById(R.id.player_playing)
                    .setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.player_playing)
                    .setVisibility(View.INVISIBLE);
        }
        return view;
    }

    private boolean isCurrentlyPlaying(Song currentSong){
        return MusicPlayer.getCurrentSong()!=null &&
                currentSong.getId().equals(MusicPlayer.getCurrentSong().getId());
    }

    @Override
    public int getCount() {
        return temporaryData!=null?temporaryData.size():0;
    }

    @Override
    public Object getItem(int position) {
        return  temporaryData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  temporaryData.get(position).getId();
    }

    /**
     * Filtering full list of songs with values
     * which where written into searchView
     * @return - filtered list by incoming query
     */
    @Override
    public Filter getFilter() {
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.length()>0? constraint.toString().toLowerCase():"";
                FilterResults result = new FilterResults();
                if (constraint.toString().length() > 0) {
                    ArrayList<Song> filterList = new ArrayList<Song>();
                    for(Song song: fullListOfSongs){
                        String textForFilter = song.getNameToFilter();
                        if(textForFilter.toLowerCase().contains(constraint)){
                            filterList.add(song);
                        }
                    }
                    result.values = filterList;
                    result.count = filterList.size();
                }else {
                    result.values = fullListOfSongs;
                    result.count = fullListOfSongs.size();
                }
                return result;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                temporaryData = (ArrayList<Song>) results.values;
                MusicPlayer.populateMusicPlayer(temporaryData);
                notifyDataSetChanged();
            }
        };
    }
}