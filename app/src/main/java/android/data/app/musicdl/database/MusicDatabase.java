package android.data.app.musicdl.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MusicData.class}, version = 1, exportSchema = false)
public abstract class MusicDatabase extends RoomDatabase {
    public static MusicDatabase instance = null;

    public static synchronized MusicDatabase getInstance(Context mContext) {
        if (instance == null) {
            instance = Room.databaseBuilder(mContext, MusicDatabase.class, "MusicDB").build();
        }
        return instance;
    }

    public abstract MusicDao getMusicDao();
}
