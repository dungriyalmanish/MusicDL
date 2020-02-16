package android.data.app.musicdl;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class MusicLoader extends Handler {
    private final String TAG = "MusicLoader";
    ISongView iSV;

    MusicLoader(ISongView sv) {
        Log.v(TAG, "Temp updating Music Data");
        iSV = sv;
    }

    //https://mp3.gisher.org/songs/
    public void loadMusic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc;
                Elements element;
                Element e;
                MusicData md;
                String[] temp = new String[8];
                for (int i = 1; i < 10; i++) {
                    try {
                        doc = Jsoup.connect("https://mp3.gisher.org/songs/" + i).get();
                        /*element = doc.getElementsByTag("h2");
                        Log.d(TAG, element.get(0).text() + "download = " + "https://mp3.gisher.org/download/" + i);
                        temp = element.get(0).text().split(" - ");*/
                        e = doc.select("dl.details").eq(1).get(0);
                        /*Log.v(TAG,"Artist: "+e.childNode(3).childNode(0).childNode(0).toString());
                        Log.v(TAG,"Album: "+e.childNode(7).childNode(0).toString().trim());
                        Log.v(TAG,"Year: "+e.childNode(11).childNode(0).toString().trim());
                        Log.v(TAG,"Genre: "+e.childNode(15).childNode(0).toString().trim());
                        Log.v(TAG,"Length: "+e.childNode(23).childNode(0).toString().trim());
                        Log.v(TAG,"Size: "+e.childNode(35).childNode(0).toString().trim());
                        Log.v(TAG,"Downloads: "+e.childNode(39).childNode(0).toString().trim());
                        */
                        temp[MusicConstants.ARTIST] = e.childNode(3).childNode(0).childNode(0).toString().trim();
                        temp[MusicConstants.ALBUM] = e.childNode(7).childNode(0).toString().trim();
                        temp[MusicConstants.YEAR] = e.childNode(11).childNode(0).toString().trim();
                        temp[MusicConstants.GENRE] = e.childNode(15).childNode(0).toString().trim();
                        temp[MusicConstants.LENGTH] = e.childNode(23).childNode(0).toString().trim();
                        temp[MusicConstants.SIZE] = e.childNode(35).childNode(0).toString().trim();
                        temp[MusicConstants.DOWNLOADS] = e.childNode(39).childNode(0).toString().trim();
                        temp[MusicConstants.SONG_NAME] = e.childNode(49).childNode(0).nextSibling().childNode(0).toString().trim();
                        md = new MusicData(i,temp);
                        Log.v(TAG,"Loaded: "+md);
                        Message.obtain(MusicLoader.this, 1001, md).sendToTarget();
                    } catch (IOException ex) {
                        Log.e(TAG, "error:" + ex);
                    }

                }
            }
        }).start();
    }

    @Override
    public void handleMessage(Message msg) {
        Log.v(TAG, "handle Message msg=" + msg.what);
        iSV.addNewCard((MusicData) msg.obj);
    }
}
