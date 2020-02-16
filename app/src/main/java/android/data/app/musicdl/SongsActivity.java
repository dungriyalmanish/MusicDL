package android.data.app.musicdl;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.data.app.musicdl.database.MusicData;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
    SharedPreferences sp;
    static int max_list = 5;

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
        sp = getPreferences(MODE_PRIVATE);
        max_list = sp.getInt(MusicConstants.MAX_LIST, 5);
        intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(MusicConstants.ACTION_FILE_EXIST);
        intentFilter.addAction(MusicConstants.ACTION_DOWNLOAD_MUSIC);
        //musicLoader.loadMusic();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (songsAdapter.getItemCount() == 0) {
            musicLoader.loadMusic(max_list);
        }
        if (br != null)
            registerReceiver(br, intentFilter);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String search = editable.toString();
                Log.v(TAG, "searching " + search);
                songsAdapter.clearAll();
                if (search.matches("#[0-9]+#")) {
                    Log.v(TAG, "search text matches super code");
                    SharedPreferences.Editor edit = sp.edit();
                    max_list = Integer.parseInt(search.substring(1, search.length() - 1));
                    musicLoader.loadMusic(max_list);
                    edit.putInt(MusicConstants.MAX_LIST, max_list);
                    edit.apply();
                    searchText.setText("");

                } else {
                    musicLoader.searchMusic(search);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        String uri, name;
        if (!skip) {
            uri = MusicConstants.DOWNLOAD_URL + musicData.id;
            name = musicData.name + "-" + musicData.artist + ".mp3";
        } else {
            uri = intent_data[0];
            name = intent_data[1];
        }
        data.putExtra(MusicConstants.EXTRA_MUSIC_URI, uri);
        data.putExtra(MusicConstants.EXTRA_MUSIC_NAME, name);
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
            } else if (MusicConstants.ACTION_DOWNLOAD_MUSIC.equals(intent.getAction())) {
                Toast.makeText(SongsActivity.this, intent.getStringExtra(MusicConstants.EXTRA_MUSIC_NAME), Toast.LENGTH_SHORT).show();
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
                Log.v(TAG, "status received: " + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
                if (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL)
                    Snackbar.make(findViewById(R.id.container), file + " downloaded", Snackbar.LENGTH_LONG).show();
                else {
                    Snackbar.make(findViewById(R.id.container), file + " not downloaded", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        songsAdapter.clearAll();
        switch (item.getItemId()) {
            case R.id._by_name:
                musicLoader.sortMusicData(MusicConstants.SORT_BY_NAME);
                break;
            case R.id._by_album:
                musicLoader.sortMusicData(MusicConstants.SORT_BY_ALBUM);
                break;
            case R.id._by_artist:
                musicLoader.sortMusicData(MusicConstants.SORT_BY_ARTIST);
                break;
            case R.id._by_downloads:
                musicLoader.sortMusicData(MusicConstants.SORT_BY_DOWNLOADS);
                break;
            case R.id._by_size:
                musicLoader.sortMusicData(MusicConstants.SORT_BY_SIZE);
                break;
            case R.id._by_time:
                musicLoader.sortMusicData(MusicConstants.SORT_BY_DURATION);
                break;
            case R.id._by_year:
                musicLoader.sortMusicData(MusicConstants.SORT_BY_YEAR);
                break;
            default:
                break;

        }
        return true;
    }
}
