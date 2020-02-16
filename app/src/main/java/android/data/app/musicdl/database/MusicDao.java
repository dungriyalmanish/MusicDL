package android.data.app.musicdl.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MusicDao {
    @Query("select * from MusicData")
    List<MusicData> getMusicData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MusicData musicData);

    @Delete
    void delete(MusicData musicData);

    @Update
    void update(MusicData musicData);

    @Query("select * from MusicData WHERE name LIKE :search OR artist LIKE :search OR album LIKE :search")
    List<MusicData> search(String search);

    @Query("select * from MusicData ORDER BY name ASC")
    List<MusicData> sortDataByName();

    @Query("select * from MusicData ORDER BY artist ASC")
    List<MusicData> sortDataByArtist();

    @Query("select * from MusicData ORDER BY album ASC")
    List<MusicData> sortDataByAlbum();

    @Query("select * from MusicData ORDER BY length ASC")
    List<MusicData> sortDataByLength();

    @Query("select * from MusicData ORDER BY size ASC")
    List<MusicData> sortDataBySIze();

    @Query("select * from MusicData ORDER BY downloads ASC")
    List<MusicData> sortDataByDownloads();

    @Query("select * from MusicData ORDER BY year DESC")
    List<MusicData> sortDataByYear();
}
