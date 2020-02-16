package android.data.app.musicdl;

import android.content.Context;
import android.data.app.musicdl.database.MusicDao;
import android.data.app.musicdl.database.MusicData;
import android.data.app.musicdl.database.MusicDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;


public class MusicLoader extends Handler {
    private final String TAG = "MusicLoader";
    private static MusicDatabase md;
    ISongView iSV;
    Context mContext;
    DbThread dbThread;
    OnlineThread onlineThread;
    SearchThread searchThread;

    MusicLoader(SongsActivity sv) {
        Log.v(TAG, "MusicLoader");
        iSV = sv;
        mContext = sv;
    }

    //https://mp3.gisher.org/songs/
    void loadMusic(final int size) {
        if (md == null) {
            md = MusicDatabase.getInstance(mContext);
        }
        if (dbThread != null && dbThread.isAlive()) {
            dbThread.stopThread();
        }
        if (onlineThread != null && onlineThread.isAlive()) {
            onlineThread.stopThread();
        }
        dbThread = new DbThread(size);
        dbThread.start();
    }

    void searchMusic(String search) {
        if (md == null) {
            md = MusicDatabase.getInstance(mContext);
        }
        if (searchThread != null && searchThread.isAlive()) {
            searchThread.stopThread();
        }
        searchThread = new SearchThread(search);
        searchThread.start();
    }

    @Override
    public void handleMessage(Message msg) {
        Log.v(TAG, "handle Message msg=" + msg.what);
        iSV.addNewCard((MusicData) msg.obj);
    }

    private class DbThread extends Thread {
        MusicDao musicDao;
        int size;
        boolean exit = false;

        DbThread(int size) {
            musicDao = md.getMusicDao();
            this.size = size;
        }

        @Override
        public void run() {
            List<MusicData> musicList = musicDao.getMusicData();
            if (!musicList.isEmpty()) {
                Log.v(TAG, "Parsing data offline...");
                if (musicList.size() <= size) {
                    Log.v(TAG, "Parsing data online from " + musicList.size() + " to " + size);
                    parseOnline(musicList.size() + 1, size, false);
                }
                for (MusicData musicData : musicList) {
                    if (!exit) Message.obtain(MusicLoader.this, 1001, musicData).sendToTarget();
                }
            } else {
                parseOnline(1, size, true);
            }
        }

        public void stopThread() {
            exit = true;
        }
    }

    private void parseOnline(int start, int size, boolean post) {
        onlineThread = new OnlineThread(start, size, post);
        onlineThread.start();
    }

    private class OnlineThread extends Thread {
        int start, size;
        MusicDao musicDao;
        boolean post;
        private boolean exit = false;

        OnlineThread(int start, int size, boolean post) {
            this.start = start;
            this.size = size;
            this.post = post;
            musicDao = md.getMusicDao();
        }

        @Override
        public void run() {
            Document doc;
            Element e;
            MusicData musicData;
            String[] temp = new String[8];
            Log.v(TAG, "Parsing data online...");
            try {
                for (int i = start; i <= size; i++) {
                    doc = Jsoup.connect("https://mp3.gisher.org/songs/" + i).get();
                    e = doc.select("dl.details").eq(1).get(0);
                    temp[MusicConstants.ARTIST] = e.childNode(3).childNode(0).childNode(0).toString().trim();
                    temp[MusicConstants.ALBUM] = e.childNode(7).childNode(0).toString().trim();
                    temp[MusicConstants.YEAR] = e.childNode(11).childNode(0).toString().trim();
                    temp[MusicConstants.GENRE] = e.childNode(15).childNode(0).toString().trim();
                    temp[MusicConstants.LENGTH] = e.childNode(23).childNode(0).toString().trim();
                    temp[MusicConstants.SIZE] = e.childNode(35).childNode(0).toString().trim();
                    temp[MusicConstants.DOWNLOADS] = e.childNode(39).childNode(0).toString().trim();
                    temp[MusicConstants.SONG_NAME] = e.childNode(49).childNode(0).nextSibling().childNode(0).toString().trim();
                    musicData = new MusicData(i, temp);
                    musicDao.insert(musicData);
                    Log.v(TAG, "Loaded: " + md);
                    if (exit) break;
                    //if (post)
                    Message.obtain(MusicLoader.this, 1001, musicData).sendToTarget();
                }
            } catch (Exception ex) {
                Log.e(TAG, "error:" + ex);
            }
        }

        public void stopThread() {
            exit = true;
        }
    }

    private class SearchThread extends Thread {
        MusicDao musicDao;
        boolean exit = false;
        String search;

        public SearchThread(String search) {
            this.search = search;
        }

        @Override
        public void run() {
            musicDao = md.getMusicDao();
            List<MusicData> musicList = musicDao.search(search+"%");
            for (MusicData musicData : musicList) {
                if (exit) {
                    break;
                }
                Message.obtain(MusicLoader.this, 100, musicData).sendToTarget();
            }
        }

        public void stopThread() {
            exit = true;
        }
    }
}
