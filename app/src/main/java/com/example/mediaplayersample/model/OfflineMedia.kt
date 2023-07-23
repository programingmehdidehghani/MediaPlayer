package com.example.mediaplayersample.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "media",
    indices = [Index(value = ["mediaName"], unique = true)]
)
data class OfflineMedia(
    val mediaName: String?
){
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null
}
