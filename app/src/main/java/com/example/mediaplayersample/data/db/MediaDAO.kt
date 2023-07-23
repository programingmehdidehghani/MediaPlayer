package com.example.mediaplayersample.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mediaplayersample.model.OfflineMedia
import kotlinx.coroutines.flow.Flow


@Dao
interface MediaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(media: OfflineMedia)

    @Query("SELECT * FROM media WHERE mediaName LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): Flow<List<OfflineMedia>>

    @Query("SELECT * FROM media")
    fun getAllMedia() : LiveData<List<OfflineMedia>>

    @Query("SELECT * FROM media ORDER BY id ASC")
    fun readData(): Flow<List<OfflineMedia>>
}