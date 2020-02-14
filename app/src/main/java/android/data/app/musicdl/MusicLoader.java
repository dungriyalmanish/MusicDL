package android.data.app.musicdl;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class MusicLoader extends Handler {
    private final String TAG = "MusicLoader";
    ISongView iSV;
    //temp
    MusicData[] md = new MusicData[10];

    MusicLoader(ISongView sv) {
        Log.v(TAG, "Temp updating Music Data");
        iSV = sv;
        for (int i = 0; i < 10; i++) {
            md[i] = new MusicData("name" + i, "artist" + i, "album" + i);
        }
    }

    //https://mp3.gisher.org/songs/
    public void loadMusic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc;
                Elements element;
                for (int i = 1; i < 10; i++) {
                    try {
                        doc = Jsoup.connect("https://mp3.gisher.org/songs/" + i).get();
                        element = doc.getElementsByTag("h2");
                        Log.d(TAG,element.get(0).text()+"download = "+"https://mp3.gisher.org/download/"+i);

                    } catch (IOException e) {
                        Log.e(TAG,"error:"+e);
                    }

                }
            }
        }).start();


        /*new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Log.v(TAG,"Thread Running -> "+i);
                        Message.obtain(MusicLoader.this, 1001, md[i]).sendToTarget();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }

                }
            }
        }).start();*/
    }

    @Override
    public void handleMessage(Message msg) {
        Log.v(TAG, "handle Message msg=" + msg.what);
        iSV.addNewCard((MusicData) msg.obj);
    }
}
