package com.example.mediaplayersample.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayersample.databinding.ItemsMediaBinding
import com.example.mediaplayersample.databinding.ItemsNetworkMediaBinding
import com.example.mediaplayersample.model.NetworkMedia
import com.example.mediaplayersample.model.OfflineMedia
import com.example.mediaplayersample.model.SampleModel
import com.example.mediaplayersample.model.SampleModelItem
import com.example.mediaplayersample.util.ImageLoader

interface OnItemClickCallbackNetwork {
    fun onItemClickNetwork(name: String)
}

@Suppress("DEPRECATION")
class ItemNetworkAdapter(private val onItemClickCallback: OnItemClickCallbackNetwork) :
    RecyclerView.Adapter<ItemNetworkAdapter.NetworkAdapterViewHolder>() {

    private val networkMediaList: ArrayList<SampleModel> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NetworkAdapterViewHolder {
        val binding =
            ItemsNetworkMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NetworkAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int = networkMediaList.size


    override fun onBindViewHolder(holder: NetworkAdapterViewHolder, position: Int) {
        holder.bind(networkMediaList[position], onItemClickCallback)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<SampleModel>) {
        this.networkMediaList.clear()
        this.networkMediaList.addAll(list)
        notifyDataSetChanged()
    }

    inner class NetworkAdapterViewHolder(private val binding: ItemsNetworkMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: SampleModel, onItemClickCallback: OnItemClickCallbackNetwork) {
            binding.tvNameMediaInItemNetwork.text = model[position].body
            itemView.setOnClickListener {
                onItemClickCallback.onItemClickNetwork(
                    model[position].body
                )
            }
        }
    }
}