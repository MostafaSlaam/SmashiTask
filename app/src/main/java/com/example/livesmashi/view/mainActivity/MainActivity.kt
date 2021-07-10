package com.example.livesmashi.view.mainActivity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.livesmashi.R
import com.example.livesmashi.databinding.ActivityMainBinding
import com.example.livesmashi.model.VideoModel
import com.example.livesmashi.util.OnVideoPlayerClickListener
import com.example.livesmashi.util.PlayerStateCallback
import com.example.livesmashi.util.PlayerViewAdapter.Companion.downloadVideo
import com.example.livesmashi.util.PlayerViewAdapter.Companion.loadVideo
import com.example.livesmashi.util.PlayerViewAdapter.Companion.pauseAll
import com.example.livesmashi.util.PlayerViewAdapter.Companion.playIndexThenPausePreviousPlayer
import com.example.livesmashi.util.PlayerViewAdapter.Companion.releaseAllPlayers
import com.example.livesmashi.view.downloadsVideosActivity.DowloadsVideosViewModel
import com.example.livesmashi.view.downloadsVideosActivity.DownloadsVideos
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    var fullscreen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.viewModel=viewModel
        binding.viedoView.findViewById<ImageButton>(R.id.exo_play).setOnClickListener {
            if (binding.viedoView.player!!.playbackState==Player.STATE_IDLE)
                binding.viedoView.player!!.prepare()

            playIndexThenPausePreviousPlayer(viewModel.urlLiveVideo)
        }
        binding.viedoView.findViewById<ImageButton>(R.id.btn_fullscreen).setOnClickListener {
            if (fullscreen) {
                binding.viedoView.findViewById<ImageButton>(R.id.btn_fullscreen).setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.exo_controls_fullscreen_enter
                    )
                );
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                if (supportActionBar != null) {
                    supportActionBar!!.show()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val params = binding.viedoView.getLayoutParams()
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = (300 * applicationContext.resources.displayMetrics.density).toInt()
                binding.viedoView.setLayoutParams(params)
                fullscreen = false
            } else {
                binding.viedoView.findViewById<ImageButton>(R.id.btn_fullscreen).setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.exo_controls_fullscreen_exit
                    )
                );
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                if (supportActionBar != null) {
                    supportActionBar!!.hide()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val params = binding.viedoView.getLayoutParams()
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                binding.viedoView.setLayoutParams(params)
                fullscreen = true
            }
        }
        initializeRecycler()
        viewModel.navigateToDownloads.observe(this, Observer {
            startActivity(Intent(this,DownloadsVideos::class.java))
        })
    }

    fun initializeRecycler() {
        binding.rv.adapter = RecycleVideosAdapter(viewModel.listOfVidos, object : OnVideoPlayerClickListener {
            override fun onItemClick(view: View?, position: Int, model: VideoModel?) {

            }
            override fun download(position: Int) {
                downloadVideo(viewModel.listOfVidos.get(position).url, this@MainActivity)
            }

        })

    }

    override fun onDestroy() {
        releaseAllPlayers()
        super.onDestroy()

    }
}