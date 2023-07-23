package com.example.mediaplayersample.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayersample.databinding.ItemsMediaBinding
import com.example.mediaplayersample.model.OfflineMedia
import com.example.mediaplayersample.util.ImageLoader

interface OnItemClickCallback {
    fun onItemClick(name: String)
}

class ItemOfflineMedia(private val onItemClickCallback: OnItemClickCallback) :
    RecyclerView.Adapter<ItemOfflineMedia.OfflineMediaViewHolder>() {

    private val fileMedia: ArrayList<OfflineMedia> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfflineMediaViewHolder {
        val binding = ItemsMediaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OfflineMediaViewHolder(binding)
    }

    override fun getItemCount(): Int =
        fileMedia.size


    override fun onBindViewHolder(holder: OfflineMediaViewHolder, position: Int) {
        holder.bind(fileMedia[position],onItemClickCallback)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<OfflineMedia>) {
        this.fileMedia.clear()
        this.fileMedia.addAll(list)
        notifyDataSetChanged()
    }



    inner class OfflineMediaViewHolder(private val binding: ItemsMediaBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(model: OfflineMedia, onItemClickCallback: OnItemClickCallback) {
            binding.tvNameMusic.text = model.mediaName
            ImageLoader.loadImage(
                binding.ivPictureSong,
                model.mediaName.toString()
            )
            itemView.setOnClickListener {
                onItemClickCallback.onItemClick(
                    model.mediaName.toString()
                )
            }

        }
    }


}