package mody.vkmusic.Song;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import mody.vkmusic.Song.Song;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class DownloadMusic extends AsyncTask<Song,Integer,String> {

    private Context context;
    private ProgressDialog mProgressDialog;
    private PowerManager.WakeLock mWakeLock;
    private String fullSize;
    private String currentSize;
    private String fullFileNameWithPath;
    private Properties prop;

    public DownloadMusic(Context context,ProgressDialog mProgressDialog) {
        this.context = context;
        this.mProgressDialog = mProgressDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        mProgressDialog.show();
        //creating property for new song
        prop = new Properties();
    }

    @Override
    protected String doInBackground(Song... songs) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(songs[0].getURL());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            int fileLength = connection.getContentLength();
            fullFileNameWithPath = Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).
                    getPath()+"/"+songs[0].getFileNameToSave();
            input = connection.getInputStream();
            output = new FileOutputStream(fullFileNameWithPath);
            //set propetry param
            prop.setProperty("id",String.valueOf(songs[0].getId()));
            byte data[] = new byte[4096];
            long total = 0;
            int count;

            fullSize = String.format("%.2f", (float) fileLength / Math.pow(10,6))+"Mb";
            currentSize = String.format("%.2f", (float) 0 / Math.pow(10,6))+"Mb";
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                if (fileLength > 0) {
                    currentSize = String.format("%.2f", (float) total / Math.pow(10,6))+"Mb";
                    //mProgressDialog.setMessage(downloadingDialog+currentSizeInMb+"/"+fileSizeInMb+")");
                    publishProgress((int) (total * 100 / fileLength));
                }
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("Downaloading("
                +currentSize+"/"+fullSize+")");
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        if (result != null) {
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            Log.i("PostExecute",result);
        }
        else {
            //Setting property to file to detect hereafter that file had been downloaded
            OutputStream song = null;
            try {
                song = new FileOutputStream(fullFileNameWithPath);
                prop.store(song,"Song from Mody app");
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            }catch (IOException ex) {
                Log.d("onPostExecute",ex.getMessage());
            }finally {
                if(song!=null)
                    try {
                        song.close();
                    }catch(IOException ex){
                        Log.d("onPostExecute",ex.getMessage());
                    }
            }
        }
    }
}