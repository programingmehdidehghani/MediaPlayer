package com.example.mediaplayersample.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayersample.R
import com.example.mediaplayersample.adapters.ItemNetworkAdapter
import com.example.mediaplayersample.adapters.OnItemClickCallbackNetwork
import com.example.mediaplayersample.databinding.FragmentOfflineBinding
import com.example.mediaplayersample.databinding.FragmentOnlineBinding
import com.example.mediaplayersample.model.SampleModel
import com.example.mediaplayersample.ui.viewModels.NetworkMediaViewModel
import com.example.mediaplayersample.ui.viewModels.OfflineMediaViewModel
import com.example.mediaplayersample.util.Resource
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.MimeTypes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaOnlineFragment : Fragment(), OnItemClickCallbackNetwork {


    private lateinit var networkMediaViewModel: NetworkMediaViewModel
    private lateinit var binding: FragmentOnlineBinding
    private val itemNetworkAdapter = ItemNetworkAdapter(this)
    private var player: ExoPlayer? = null
    private var categoriesName: ArrayList<SampleModel> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnlineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkMediaViewModel = ViewModelProvider(requireActivity())[NetworkMediaViewModel::class.java]
        networkMediaViewModel.getAllMedia()
        networkMediaViewModel.getNetworkMedia.observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Resource.Success ->{
                    binding.progressInOnlineMediaFragment.visibility = View.GONE
                    binding.rvMediaInNetworkFragment.visibility = View.VISIBLE
                    categoriesName.add(result.data)
                    itemNetworkAdapter.updateList(categoriesName)
                    setUpCategoriesNameRecyclerView()
                }
                is Resource.Error -> {

                }
                is Resource.Loading ->{
                   binding.progressInOnlineMediaFragment.visibility = View.VISIBLE
                }
            }

        })

    }



    private fun setUpCategoriesNameRecyclerView(){
        var layoutManager
                = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvMediaInNetworkFragment.apply {
            this.layoutManager = layoutManager
            adapter = itemNetworkAdapter
        }
    }

    override fun onItemClickNetwork(name: String) {
        binding.progressInOnlineMediaFragment.visibility = View.GONE
        binding.rvMediaInNetworkFragment.visibility = View.GONE
        binding.exoplayerInOnlineFragment.visibility = View.VISIBLE
        player = ExoPlayer.Builder(requireContext())
            .build()
        val mediaItem = MediaItem.Builder()
            .setUri(name)
            .setMimeType(MimeTypes.APPLICATION_MP4)
            .build()
        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(requireContext())
        )
            .createMediaSource(mediaItem)
        player!!.apply {
            setMediaSource(mediaSource)
            playWhenReady = true
            seekTo(0, 0L)
            prepare()
        }.also {
            binding.exoplayerInOnlineFragment.player = it
        }
    }


}