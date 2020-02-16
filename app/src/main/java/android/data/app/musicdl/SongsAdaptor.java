package android.data.app.musicdl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SongsAdaptor extends RecyclerView.Adapter<SongsAdaptor.ViewModel> {

    private static final String TAG = "SongsAdaptor";
    Context mContext;
    List<MusicData> musicDataList;
    MusicData md;
    IMusicCardListener mListener;

    public SongsAdaptor(SongsActivity songsActivity) {
        musicDataList = new ArrayList<>();
        mContext = songsActivity;
        mListener = songsActivity;
    }

    @NonNull
    @Override
    public ViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.song_card, parent, false);
        return new ViewModel(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewModel holder, final int position) {
        md = musicDataList.get(position);
        holder.name.setText(md.name);
        String temp = md.artist + " | " + md.album + " | " + md.year;
        holder.artist.setText(temp);
        temp = md.length + " | " + md.downloads + " | " + md.size;
        holder.album.setText(temp);
        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Selected card from Adaptor " + md);
                mListener.selectedCard(musicDataList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicDataList.size();
    }

    public void addData(MusicData musicData) {
        Log.v(TAG, "Insert " + musicData.toString());
        musicDataList.add(musicData);
        notifyItemInserted(musicDataList.size());
    }

    public void updateBitmap(Bitmap bitmap, int position) {
        musicDataList.get(position).setBitmap(bitmap);
        notifyItemChanged(position);

    }

    public class ViewModel extends RecyclerView.ViewHolder {//implements View.OnClickListener {
        TextView name, artist, album;
        ImageView icon;
        ImageButton fab;

        public ViewModel(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id._name);
            artist = itemView.findViewById(R.id._artist);
            album = itemView.findViewById(R.id._album);
            icon = itemView.findViewById(R.id._icon);
            fab = itemView.findViewById(R.id._download_button);
            //fab.setOnClickListener(this);
        }

    }
}