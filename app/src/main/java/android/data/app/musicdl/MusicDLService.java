package android.data.app.musicdl;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;


public class MusicDLService extends IntentService {

    private static final String TAG = "MusicDLService";
    public static final String FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MusicDL";
    static DownloadManager downloadManager;
    DownloadManager.Request request;
    File file;

    public MusicDLService() {
        super(null);
        Log.v(TAG, "Constructor Called");
        //downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(MusicConstants.ACTION_DOWNLOAD_MUSIC)) {
            String uri_string = intent.getStringExtra(MusicConstants.EXTRA_MUSIC_URI);
            String filename = intent.getStringExtra(MusicConstants.EXTRA_MUSIC_NAME);
            boolean skip = intent.getBooleanExtra(MusicConstants.EXTRA_SKIP, false);
            File loc = new File(FILE_DIR);
            if (!loc.exists()) {
                loc.mkdirs();
            }
            file = new File(loc, filename);
            if (!skip && file.exists()) {
                Intent i = new Intent(MusicConstants.ACTION_FILE_EXIST);
                i.putExtra(MusicConstants.EXTRA_MUSIC_URI, uri_string);
                i.putExtra(MusicConstants.EXTRA_MUSIC_NAME, filename);
                sendBroadcast(i);
                return;
            }
            Log.v(TAG, "Location =" + file.getAbsolutePath());
            request = new DownloadManager.Request(Uri.parse(uri_string))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDescription("Downloading...")
                    .setTitle(filename)
                    .setDestinationUri(Uri.fromFile(file));
            if (downloadManager == null) {
                Log.v(TAG, "DownloadManager was null");
                downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            }
            downloadManager.enqueue(request);
            sendBroadcast(new Intent(MusicConstants.ACTION_DOWNLOAD_MUSIC).putExtra(MusicConstants.EXTRA_MUSIC_NAME, filename + " enqueued to download."));

        }
    }
}
