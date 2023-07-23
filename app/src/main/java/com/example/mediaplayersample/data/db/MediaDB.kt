package com.example.mediaplayersample.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mediaplayersample.model.OfflineMedia

@Database(
    entities = [OfflineMedia::class],
    version = 1
)
abstract class MediaDB : RoomDatabase(){

    abstract fun getDAO() : MediaDAO

}