package com.example.mediaplayersample.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayersample.adapters.ItemOfflineMedia
import com.example.mediaplayersample.adapters.OnItemClickCallback
import com.example.mediaplayersample.databinding.FragmentOfflineBinding
import com.example.mediaplayersample.model.OfflineMedia
import com.example.mediaplayersample.ui.PlayMediaActivity
import com.example.mediaplayersample.ui.viewModels.OfflineMediaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*

@AndroidEntryPoint
class MediaOfflineFragment : Fragment(), OnItemClickCallback {


    private lateinit var binding: FragmentOfflineBinding
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private val videoItemHashSet = HashSet<OfflineMedia>()
    private val itemAdapterOffline = ItemOfflineMedia(this)
    private lateinit var offlineViewModel: OfflineMediaViewModel
    private var receiptsList: ArrayList<OfflineMedia> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOfflineBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.prInOfflineFragment.visibility = View.VISIBLE
        offlineViewModel = ViewModelProvider(requireActivity())[OfflineMediaViewModel::class.java]
        uiScope.launch (Dispatchers.IO){
            getAllMedia()
              withContext(Dispatchers.Main) {
                binding.prInOfflineFragment.visibility = View.GONE
                binding.rvMediaFilesInOffline.visibility = View.VISIBLE
                offlineViewModel.getAllMedia()
                    .observe(viewLifecycleOwner) {
                        receiptsList = ArrayList<OfflineMedia>(it)
                        itemAdapterOffline.updateList(it)
                        setUpCategoriesNameRecyclerView()
                    }
            }
        }
        binding.etSearchInOfflineFragment.addTextChangedListener { editable ->
            editable?.let {
                if (editable.toString().isNotEmpty()) {
                    offlineViewModel.searchDatabase(editable.toString())
                        .observe(viewLifecycleOwner) {
                            itemAdapterOffline.updateList(it)
                            Log.i("search", "search is ...${it.size}")
                            setUpCategoriesNameRecyclerView()
                            binding.rvMediaFilesInOffline.visibility = View.VISIBLE
                        }
                } else {
                    itemAdapterOffline.updateList(receiptsList)
                    setUpCategoriesNameRecyclerView()
                    binding.rvMediaFilesInOffline.visibility = View.VISIBLE
                }
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun getAllMedia() {
        val projection =
            arrayOf(MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME)
        val cursor = requireContext().contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        try {
            cursor!!.moveToFirst()
            var bitmap: Bitmap?
            do {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)))
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                bitmap = retriever.getFrameAtTime(
                    100,
                    MediaMetadataRetriever.OPTION_CLOSEST
                )!!
                val media = OfflineMedia(
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                )
                offlineViewModel.insertCart(media)
                videoItemHashSet.add(media)

            } while (cursor.moveToNext())
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpCategoriesNameRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvMediaFilesInOffline.apply {
            this.layoutManager = layoutManager
            adapter = itemAdapterOffline
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onItemClick(name: String) {
        val intent = Intent(context, PlayMediaActivity::class.java)
        intent.putExtra("videoPath", name)
        startActivity(intent)
    }


}