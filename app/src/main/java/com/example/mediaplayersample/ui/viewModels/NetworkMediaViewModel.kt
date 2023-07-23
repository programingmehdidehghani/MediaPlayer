package com.example.mediaplayersample.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mediaplayersample.MediaApp
import com.example.mediaplayersample.model.NetworkMedia
import com.example.mediaplayersample.model.SampleModel
import com.example.mediaplayersample.model.SampleModelItem
import com.example.mediaplayersample.repository.NetworkMediaRepository
import com.example.mediaplayersample.util.Resource
import com.example.mediaplayersample.util.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class NetworkMediaViewModel @Inject constructor(
    application: Application,
    private val repository: NetworkMediaRepository
): AndroidViewModel(application) {

    val getNetworkMedia = MutableLiveData<Resource<SampleModel>>()
    private lateinit var disposable: Disposable


    fun getAllMedia(){
        getNetworkMedia.postValue(Resource.Loading)
        if (hasInternetConnection<MediaApp>()){
            val response = repository.getNetworkMedia()
            response.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMediaList())
        } else {
            getNetworkMedia.postValue(Resource.Error("No Internet Connection.!"))
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    private fun getMediaList(): Observer<SampleModel> {
        return object : Observer<SampleModel> {
            override fun onSubscribe(d: Disposable) {
                disposable = d
            }
            override fun onNext(t: SampleModel) {
                getNetworkMedia.postValue(Resource.Success(t))
            }
            override fun onError(e: Throwable) {
                getNetworkMedia.postValue(Resource.Error(e.toString()))
            }
            override fun onComplete() {
            }
        }
    }

 /*   private fun loadMedia(){
        _getNetworkMedia.postValue(Resource.Loading)
            if (hasInternetConnection<MediaApp>()){
                compositeDisposable.add(
                    repository.getNetworkMedia().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableObserver<List<NetworkMedia?>?>(){
                            override fun onNext(value: List<NetworkMedia?>?) {
                                //_getNetworkMedia.postValue(Resource.Success(value))
                            }

                            override fun onError(e: Throwable?) {
                                _getNetworkMedia.postValue(Resource.Error(e.toString()))
                            }

                            override fun onComplete() {

                            }

                        })
                )
            } else {
                _getNetworkMedia.postValue(Resource.Error("No Internet connection"))
            }
    }*/
}