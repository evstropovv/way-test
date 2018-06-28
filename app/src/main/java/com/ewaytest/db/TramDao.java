package com.ewaytest.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface TramDao {

    @Query("SELECT * FROM tatatatat")
    LiveData<List<Coord>> getAll();

    @Insert(onConflict = REPLACE)
    void addTram(Coord... coord);

}
