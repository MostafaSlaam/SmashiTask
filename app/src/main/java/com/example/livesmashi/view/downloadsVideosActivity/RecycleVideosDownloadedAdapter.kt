package com.example.livesmashi.view.downloadsVideosActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.livesmashi.R
import com.example.livesmashi.databinding.ItemRecyclerDownloadVideoBinding
import com.example.livesmashi.model.VideoModel
import com.example.livesmashi.util.OnVideoPlayerClickListener
import com.example.livesmashi.util.PlayerStateCallback
import com.example.livesmashi.util.PlayerViewAdapter
import com.google.android.exoplayer2.Player


class RecycleVideosDownloadedAdapter(
    var items: ArrayList<VideoModel>,var listener:OnVideoPlayerClickListener
) : RecyclerView.Adapter<RecycleVideosDownloadedAdapter.ViewHolder>(), PlayerStateCallback {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_recycler_download_video,
            parent,
            false
        ) as ItemRecyclerDownloadVideoBinding
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onViewRecycled(holder: ViewHolder) {
        val position = holder.adapterPosition
        PlayerViewAdapter.releaseRecycledPlayers(items[position].url)
        super.onViewRecycled(holder)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = items[holder.adapterPosition]
        holder.binding.callback=this@RecycleVideosDownloadedAdapter
        holder.binding.model = item
        holder.binding.videoView.findViewById<ImageButton>(R.id.exo_play).setOnClickListener {
            PlayerViewAdapter.playIndexThenPausePreviousPlayerDownloded(item.url)
        }
        holder.binding.root.setOnClickListener {

        }


    }

    fun setList(list: ArrayList<VideoModel>) {
        this.items = list
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemRecyclerDownloadVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onVideoDurationRetrieved(duration: Long, player: Player) {
    }

    override fun onVideoBuffering(player: Player) {
    }

    override fun onStartedPlaying(player: Player) {

    }

    override fun onFinishedPlaying(player: Player) {

    }


}