package com.example.mediaplayersample.data.api

import com.example.mediaplayersample.model.SampleModel
import com.example.mediaplayersample.model.SampleModelItem
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    fun getMediaServer(): Observable<SampleModel>
}