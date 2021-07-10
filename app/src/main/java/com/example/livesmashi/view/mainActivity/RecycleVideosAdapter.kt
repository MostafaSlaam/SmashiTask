package com.example.livesmashi.view.mainActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.livesmashi.R
import com.example.livesmashi.databinding.ItemRecyclerVideoBinding
import com.example.livesmashi.model.VideoModel
import com.example.livesmashi.util.OnVideoPlayerClickListener
import com.example.livesmashi.util.PlayerStateCallback
import com.example.livesmashi.util.PlayerViewAdapter
import com.google.android.exoplayer2.Player


class RecycleVideosAdapter(
    var items: ArrayList<VideoModel>,var listener:OnVideoPlayerClickListener
) : RecyclerView.Adapter<RecycleVideosAdapter.ViewHolder>(), PlayerStateCallback {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_recycler_video,
            parent,
            false
        ) as ItemRecyclerVideoBinding
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
        holder.binding.callback=this@RecycleVideosAdapter
        holder.binding.model = item
        holder.binding.videoView.findViewById<ImageButton>(R.id.exo_play).setOnClickListener {
            if (holder.binding.videoView.player!!.playbackState==Player.STATE_IDLE)
                holder.binding.videoView.player!!.prepare()
            PlayerViewAdapter.playIndexThenPausePreviousPlayer(item.url)
        }
        holder.binding.root.setOnClickListener {

        }
        holder.binding.videoView.findViewById<ImageButton>(R.id.btn_download).setOnClickListener {
            listener.download(holder.adapterPosition)
        }


    }

    fun setList(list: ArrayList<VideoModel>) {
        this.items = list
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemRecyclerVideoBinding) :
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