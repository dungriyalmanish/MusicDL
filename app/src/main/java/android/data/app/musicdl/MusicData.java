package android.data.app.musicdl;

import android.graphics.Bitmap;

public class MusicData {
	String name;
	String artist;
	String album;
	Bitmap icon=null;
	
	public MusicData(String name, String artist, String album){
		this.name = name;
		this.artist = artist;
		this.album = album;
	}
	
	public void setBitmap(Bitmap bmp){
		this.icon = bmp;
	}
	
	@Override
	public String toString(){
		return "name="+name+", artist="+artist+", album="+album+", icon="+icon;
	}
}