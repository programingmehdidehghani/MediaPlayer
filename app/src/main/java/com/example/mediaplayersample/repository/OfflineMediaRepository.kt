package com.example.mediaplayersample.repository

import com.example.mediaplayersample.data.db.MediaDB
import com.example.mediaplayersample.model.OfflineMedia
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineMediaRepository @Inject constructor(private val db: MediaDB) {
    suspend fun insert(media: OfflineMedia) = db.getDAO().insertCart(media)

    fun searchDatabase(searchQuery: String): Flow<List<OfflineMedia>> {
        return db.getDAO().searchDatabase(searchQuery)
    }

    fun getMedia() = db.getDAO().getAllMedia()

}