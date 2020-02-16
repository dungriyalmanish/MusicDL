package android.data.app.musicdl;

import android.graphics.Bitmap;

import java.io.Serializable;

public class MusicData implements Serializable {
    int id;
    public String name, artist, album, year, length, size, downloads;
    Bitmap icon = null;

    public MusicData(int id, String name, String artist, String album, String year, String length, String size, String downloads) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.length = length;
        this.year = year;
        this.size = size;
        this.downloads = downloads;
    }

    public MusicData(int i, String[] data) {
        this.id = i;
        this.name = data[MusicConstants.SONG_NAME];
        this.artist = data[MusicConstants.ARTIST];
        this.album = data[MusicConstants.ALBUM];
        this.length = data[MusicConstants.LENGTH];
        this.year = data[MusicConstants.YEAR];
        this.size = data[MusicConstants.SIZE];
        this.downloads = data[MusicConstants.DOWNLOADS];
    }

    public void setBitmap(Bitmap bmp) {
        this.icon = bmp;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", name=" + name +
                ", artist=" + artist +
                ", album=" + album +
                ", length=" + length +
                ", year=" + year +
                ", size=" + size +
                ", downloads=" + downloads;
    }
}