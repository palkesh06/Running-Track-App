package com.example.maps.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.maps.data.db.dao.RunDao
import com.example.maps.data.db.mapper.Converters
import com.example.maps.data.model.Run


@Database(
    entities = [Run::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class FitPathDb : RoomDatabase() {
    companion object{
        const val DATABASE_NAME = "fit_path_db"
    }
    abstract fun getRunDao() : RunDao

}