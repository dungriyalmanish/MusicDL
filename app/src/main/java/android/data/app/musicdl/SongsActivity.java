package android.data.app.musicdl;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

public class SongsActivity extends AppCompatActivity implements IMusicCardListener,ISongView {

    private static final String TAG = "SongsActivity";
    RecyclerView songsRecycler;
    SongsAdaptor songsAdapter;
    TextInputEditText searchText;
    MusicLoader musicLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        songsRecycler = findViewById(R.id._songs_recycler);
        searchText = findViewById(R.id._search_text);
        songsAdapter = new SongsAdaptor(this);
        musicLoader = new MusicLoader(this);
        songsRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        songsRecycler.setHasFixedSize(true);
        songsRecycler.setAdapter(songsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        musicLoader.loadMusic();
    }

    //Callback when card clicked
    @Override
    public void selectedCard(MusicData musicData) {
        Log.v(TAG, "Card Clicked: " + musicData.toString());
    }


    @Override
    public void addNewCard(MusicData md) {
        songsAdapter.addData(md);
    }
}
