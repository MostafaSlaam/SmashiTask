package com.example.livesmashi.view.downloadsVideosActivity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.livesmashi.R
import com.example.livesmashi.databinding.ActivityDownloadsBinding
import com.example.livesmashi.model.VideoModel
import com.example.livesmashi.util.OnVideoPlayerClickListener
import com.example.livesmashi.util.PlayerViewAdapter.Companion.downloadVideo
import com.example.livesmashi.util.PlayerViewAdapter.Companion.getDownloads
import com.example.livesmashi.util.PlayerViewAdapter.Companion.releaseAllDownlodedPlayers


class DownloadsVideos : AppCompatActivity() {
    lateinit var binding: ActivityDownloadsBinding
    lateinit var viewModel: DowloadsVideosViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_downloads)
        viewModel = ViewModelProvider(this).get(DowloadsVideosViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.listOfVideos = getDownloads(this)
        initializeRecycler()

    }

    fun initializeRecycler() {
        binding.rv.adapter = RecycleVideosDownloadedAdapter(viewModel.listOfVideos,
            object : OnVideoPlayerClickListener {
                override fun onItemClick(view: View?, position: Int, model: VideoModel?) {

                }

                override fun download(position: Int) {
                    downloadVideo(viewModel.listOfVideos.get(position).url, this@DownloadsVideos)
                }

            })

    }

    override fun onBackPressed() {
        releaseAllDownlodedPlayers()
        super.onBackPressed()
    }
}