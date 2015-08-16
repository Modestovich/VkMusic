package mody.vkmusic.Fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import mody.vkmusic.ListActivity;
import mody.vkmusic.R;

public class StartUpProgress extends Fragment {

    private ProgressBar startUpProgress;
    private TextView progressText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_up_progress,container,false);
        startUpProgress =(ProgressBar) view.findViewById(R.id.startUpBar);
        progressText =(TextView) view.findViewById(R.id.percentProgress);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new ProgressingBar().execute();
    }

    public class ProgressingBar extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(getApplicationContext() ,"Start loading...",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            for(int i=startUpProgress.getProgress();i<=startUpProgress.getMax();i++){
                try{
                    Thread.sleep(10);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                publishProgress(i);
            }

            return "Finish loading";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            startUpProgress.setProgress(values[0]);
            progressText.setText(values[0]+"%");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent slideactivity = new Intent( getActivity(), ListActivity.class);
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getActivity(),
                            R.anim.current,R.anim.next)
                            .toBundle();
            startActivity(slideactivity, bndlanimation);
        }
    }
}