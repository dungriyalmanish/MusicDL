package android.data.app.musicdl.database;

import android.data.app.musicdl.MusicConstants;
import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class MusicData implements Serializable {
    @PrimaryKey
    public
    int id;

    @ColumnInfo
    public String name;
    @ColumnInfo
    public String artist;
    @ColumnInfo
    public String album;
    @ColumnInfo
    public String year;
    @ColumnInfo
    public String length;
    @ColumnInfo
    public String size;
    @ColumnInfo
    public String downloads;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
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