package android.data.app.musicdl;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class MusicLoader extends Handler {
    private final String TAG ="MusicLoader";
    ISongView iSV;
    //temp
    MusicData[] md = new MusicData[10];

    MusicLoader(ISongView sv) {
        Log.v(TAG,"Temp updating Music Data");
        iSV = sv;
        for (int i = 0; i < 10; i++) {
            md[i] = new MusicData("name" + i, "artist" + i, "album" + i);
        }
    }

    public void loadMusic() {
        new Thread(new Runnable() {
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
        }).start();
    }

    @Override
    public void handleMessage(Message msg) {
        Log.v(TAG,"handle Message msg="+msg.what);
        iSV.addNewCard((MusicData) msg.obj);
    }
}
