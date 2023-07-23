package com.example.mediaplayersample.ui.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.example.mediaplayersample.model.OfflineMedia
import com.example.mediaplayersample.repository.OfflineMediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfflineMediaViewModel @Inject constructor(
    application: Application,
    private val mediaFileDBRepository: OfflineMediaRepository
): AndroidViewModel(application) {


    fun insertCart(media: OfflineMedia) = viewModelScope.launch {
        mediaFileDBRepository.insert(media)
    }

    fun searchDatabase(searchQuery: String) : LiveData<List<OfflineMedia>>{
        return mediaFileDBRepository.searchDatabase(searchQuery).asLiveData()
    }

    fun getAllMedia(): LiveData<List<OfflineMedia>> {
        return mediaFileDBRepository.getMedia()
    }


}