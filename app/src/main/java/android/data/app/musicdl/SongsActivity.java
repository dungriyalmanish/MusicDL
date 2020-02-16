package android.data.app.musicdl;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class SongsActivity extends AppCompatActivity implements IMusicCardListener, ISongView {

    private static final String TAG = "SongsActivity";
    RecyclerView songsRecycler;
    SongsAdaptor songsAdapter;
    TextInputEditText searchText;
    MusicLoader musicLoader;
    boolean readPermission;
    boolean writePermission;
    DownloadBroadcastReceiver br;
    Intent data;
    IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        Log.v(TAG, "onCreate Called");
        songsRecycler = findViewById(R.id._songs_recycler);
        searchText = findViewById(R.id._search_text);
        songsAdapter = new SongsAdaptor(this);
        musicLoader = new MusicLoader(this);
        songsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        songsRecycler.setHasFixedSize(true);
        songsRecycler.setAdapter(songsAdapter);
        br = new DownloadBroadcastReceiver();
        intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(MusicConstants.ACTION_FILE_EXIST);
        //musicLoader.loadMusic();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (songsAdapter.getItemCount() == 0) {
            musicLoader.loadMusic();
        }
        if (br != null)
            registerReceiver(br, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (br != null)
            unregisterReceiver(br);
    }

    //Callback when card clicked
    @Override
    public void selectedCard(MusicData musicData) {
        Log.v(TAG, "Card Clicked: " + musicData.toString());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            readPermission = true;
            writePermission = true;
            downloadMusic(musicData, false);
        }
    }

    private void downloadMusic(MusicData musicData, boolean skip, @Nullable String... intent_data) {
        data = new Intent(this, MusicDLService.class);
        data.setAction(MusicConstants.ACTION_DOWNLOAD_MUSIC);
        if (!skip) {
            data.putExtra(MusicConstants.EXTRA_MUSIC_URI, MusicConstants.DOWNLOAD_URL + musicData.id);
            data.putExtra(MusicConstants.EXTRA_MUSIC_NAME, musicData.name + "-" + musicData.artist + ".mp3");
        } else {
            data.putExtra(MusicConstants.EXTRA_MUSIC_URI, intent_data[0]);
            data.putExtra(MusicConstants.EXTRA_MUSIC_NAME, intent_data[1]);
        }
        data.putExtra(MusicConstants.EXTRA_SKIP, skip);
        startService(data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        readPermission = true;
                    }
                } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        writePermission = true;
                    }
                }
            }
        }
    }

    @Override
    public void addNewCard(MusicData md) {
        songsAdapter.addData(md);
    }

    private class DownloadBroadcastReceiver extends BroadcastReceiver {
        DownloadBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction()))
                notifyUI(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1));
            else if (MusicConstants.ACTION_FILE_EXIST.equals(intent.getAction())) {
                showFileDialog(intent);
            }
        }
    }

    private void showFileDialog(final Intent intent) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this)
                .setTitle(intent.getStringExtra(MusicConstants.EXTRA_MUSIC_NAME) + " already exist !")
                .setMessage("Do you still want to download ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] data = new String[]{intent.getStringExtra(MusicConstants.EXTRA_MUSIC_URI), intent.getStringExtra(MusicConstants.EXTRA_MUSIC_NAME)};
                        downloadMusic(null, true, data);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        ab.show();
    }

    private void notifyUI(long id) {
        if (MusicDLService.downloadManager != null) {
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(id);
            Cursor c = MusicDLService.downloadManager.query(q);
            if (c.moveToNext()) {
                String file = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
                Log.v(TAG, "Total Columns: " + c.getColumnCount());
                for (int i = 0; i < c.getColumnCount(); i++) {
                    try {
                        Log.v(TAG, "At i=" + i + c.getColumnName(i) + ": " + c.getString(i));
                    } catch (Exception e) {
                        Log.v(TAG, "At i=" + i + " error:" + e.getMessage());
                    }
                }
                if (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)) == 200)
                    Snackbar.make(findViewById(R.id.container), file + " downloaded", Snackbar.LENGTH_LONG).show();
                else {
                    Snackbar.make(findViewById(R.id.container), file + " not downloaded", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }
}
