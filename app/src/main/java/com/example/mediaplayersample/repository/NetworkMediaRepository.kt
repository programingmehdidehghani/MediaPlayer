package com.example.mediaplayersample.repository

import com.example.mediaplayersample.data.api.ApiService
import javax.inject.Inject

class NetworkMediaRepository @Inject constructor(private val apiService: ApiService){
    fun getNetworkMedia() = apiService.getMediaServer()

}