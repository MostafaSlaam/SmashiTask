package com.example.livesmashi.util

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.example.livesmashi.model.VideoModel
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util
import com.kiddowz.nursery.network_connection.Connection


// extension function for show toast
fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

class PlayerViewAdapter {
    companion object {
        // for hold all players generated
        private var playersMap: MutableMap<String, SimpleExoPlayer> = mutableMapOf()

        // for hold current player
        private var currentPlayingVideo: Pair<String, SimpleExoPlayer>? = null

        private var playersMapDownloded: MutableMap<String, SimpleExoPlayer> = mutableMapOf()
        private var currentPlayingVideoDownloded: Pair<String, SimpleExoPlayer>? = null


        fun releaseAllPlayers() {
            playersMap.map {
                it.value.release()
            }
        }

        fun releaseAllDownlodedPlayers() {
            playersMapDownloded.map {
                it.value.release()
            }
        }

        fun pauseAll() {
            playersMap.map {
                it.value.playWhenReady = false
            }
        }

        fun downloadVideo(url: String, context: Context) {
            if (Connection.isInternetAvailable(context)) {
                val downloadRequest: DownloadRequest =
                    DownloadRequest.Builder(url, Uri.parse(url)).build()
                DownloadService.sendAddDownload(
                    context,
                    VideoDownloadService::class.java,
                    downloadRequest,  /* foreground= */
                    false
                )
            }
        }

        fun getDownloads(context: Context): ArrayList<VideoModel> {

            // This will only happen once, because getDownloadManager is guaranteed to be called only once
            // in the life cycle of the process.
            var list = ArrayList<VideoModel>()
            val downloadManager = DemoUtil.getDownloadManager( /* context= */context)
            var downloadIndex = downloadManager.downloadIndex
            var downloadCursor = downloadIndex.getDownloads(Download.STATE_COMPLETED)
            while (downloadCursor.moveToNext())
                list.add(VideoModel(downloadCursor.download.request.id))

            return list
        }


        // call when item recycled to improve performance
        fun releaseRecycledPlayers(index: String) {
            playersMap[index]?.release()
        }

        fun releaseDownlodedRecycledPlayers(index: String) {
            playersMapDownloded[index]?.release()
        }

        // call when scroll to pause any playing player
        fun pauseCurrentPlayingVideo() {
            if (currentPlayingVideo != null) {
                currentPlayingVideo?.second?.playWhenReady = false
            }
        }

        fun pauseCurrentDownlodedPlayingVideo() {
            if (currentPlayingVideoDownloded != null) {
                currentPlayingVideoDownloded?.second?.playWhenReady = false
            }
        }

        fun playIndexThenPausePreviousPlayer(index: String) {
//            if (playersMap.get(index)?.playWhenReady == false) {
                pauseCurrentPlayingVideo()
                playersMap.get(index)?.playWhenReady = true
                currentPlayingVideo = Pair(index, playersMap.get(index)!!)
//            }

        }

        fun playIndexThenPausePreviousPlayerDownloded(index: String) {
            if (playersMapDownloded.get(index)?.playWhenReady == false) {
                pauseCurrentDownlodedPlayingVideo()
                playersMapDownloded.get(index)?.playWhenReady = true
                currentPlayingVideoDownloded = Pair(index, playersMapDownloded.get(index)!!)
            }

        }

        /*
        *  url is a url of stream video
        *  progressbar for show when start buffering stream
        * thumbnail for show before video start
        * */
        @JvmStatic
        @BindingAdapter(
            value = ["video_link", "on_state_change","loader", "item_index", "play_when_ready"],
            requireAll = false
        )
        fun PlayerView.loadVideo(
            url: String?, callback: PlayerStateCallback?,
            progressbar: ProgressBar?,
//                                 thumbnail: ImageView,
            item_index: String? = null,
            play_when_ready: Boolean
        ) {


            if (url == null) return

//            if (Connection.isInternetAvailable(context)) {
//                val downloadRequest: DownloadRequest =
//                    DownloadRequest.Builder(url, Uri.parse(url)).build()
//                DownloadService.sendAddDownload(
//                    context,
//                    VideoDownloadService::class.java,
//                    downloadRequest,  /* foreground= */
//                    false
//                )
//            }

//            val httpDataSourceFactory = DefaultHttpDataSourceFactory(
//                Util.getUserAgent(context, "test.myuser")
//            )

            val cacheDataSourceFactory: DataSource.Factory = CacheDataSource.Factory()
                .setCache(DemoUtil.getDownloadCache(context))
                .setUpstreamDataSourceFactory(DemoUtil.getHttpDataSourceFactory(context))
                .setCacheWriteDataSinkFactory(null) // Disable writing.


            val player = SimpleExoPlayer.Builder(context)
                .setMediaSourceFactory(
                    DefaultMediaSourceFactory(cacheDataSourceFactory)
                )
                .build()


//            val player = SimpleExoPlayer.Builder(context).build()

            player.playWhenReady = play_when_ready

            player.repeatMode = Player.REPEAT_MODE_OFF
            // When changing track, retain the latest frame instead of showing a black screen
            setKeepContentOnPlayerReset(true)
            // We'll show the controller, change to true if want controllers as pause and start
            this.useController = true
            val mediaItem: MediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.prepare()
            this.player = player

            // add player with its index to map
            if (playersMap.containsKey(item_index))
                playersMap.remove(item_index)
            if (item_index != null)
                playersMap[item_index] = player

            if (play_when_ready && item_index != null)
                currentPlayingVideo = Pair(item_index, playersMap.get(item_index)!!)

            this.player!!.addListener(object : Player.EventListener {

                override fun onPlayerError(error: ExoPlaybackException) {
                    super.onPlayerError(error)
                    progressbar?.visibility=View.GONE
                    this@loadVideo.context.toast("Oops! Error occurred while playing media.")
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)


                    if (playbackState==Player.STATE_IDLE)
                    {
//                        player.retry()
                    }

                    if (playbackState == Player.STATE_BUFFERING) {
                        if (callback != null)
                            callback.onVideoBuffering(player)
                        // Buffering..
                        // set progress bar visible here
                        // set thumbnail visible
//                        thumbnail.visibility = View.VISIBLE
                        progressbar?.visibility = View.VISIBLE
                    }

                    if (playbackState == Player.STATE_READY) {
                        // [PlayerView] has fetched the video duration so this is the block to hide the buffering progress bar
                        progressbar?.visibility = View.GONE
//                        // set thumbnail gone
//                        thumbnail.visibility = View.GONE
                        if (callback != null)
                            callback.onVideoDurationRetrieved(
                                this@loadVideo.player!!.duration,
                                player
                            )
                    }

                    if (playbackState == Player.STATE_READY && player.playWhenReady) {
                        // [PlayerView] has started playing/resumed the video
                        if (callback != null)
                            callback.onStartedPlaying(player)
                    }
                    if (playbackState == Player.STATE_ENDED) {
                        if (callback != null)
                            callback.onFinishedPlaying(player)
                    }
                }
            })
        }


        @JvmStatic
        @BindingAdapter(
            value = ["video_download_link", "on_state_change"],
            requireAll = false
        )
        fun PlayerView.loadDownloadVideo(
            url: String?, callback: PlayerStateCallback?,
//                                 progressbar: ProgressBar, thumbnail: ImageView,

        ) {


            if (url == null) return

//            if (Connection.isInternetAvailable(context)) {
//                val downloadRequest: DownloadRequest =
//                    DownloadRequest.Builder(url, Uri.parse(url)).build()
//                DownloadService.sendAddDownload(
//                    context,
//                    VideoDownloadService::class.java,
//                    downloadRequest,  /* foreground= */
//                    false
//                )
//            }

            val httpDataSourceFactory = DefaultHttpDataSourceFactory(
                Util.getUserAgent(context, "test.myuser")
            )

            val cacheDataSourceFactory: DataSource.Factory = CacheDataSource.Factory()
                .setCache(DemoUtil.getDownloadCache(context))
                .setUpstreamDataSourceFactory(DemoUtil.getHttpDataSourceFactory(context))
                .setCacheWriteDataSinkFactory(null) // Disable writing.


            val player = SimpleExoPlayer.Builder(context)
                .setMediaSourceFactory(
                    DefaultMediaSourceFactory(cacheDataSourceFactory)
                )
                .build()


//            val player = SimpleExoPlayer.Builder(context).build()

            player.playWhenReady = false

            player.repeatMode = Player.REPEAT_MODE_OFF
            // When changing track, retain the latest frame instead of showing a black screen
            setKeepContentOnPlayerReset(true)
            // We'll show the controller, change to true if want controllers as pause and start
            this.useController = true
            // Provide url to load the video from here
//            val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory("Demo"))
//                .createMediaSource(
//                    Uri.parse(url)
//                )
//            if (is_downloaded) {
//                val mediaSource: ProgressiveMediaSource =
//                    ProgressiveMediaSource.Factory(cacheDataSourceFactory)
//                        .createMediaSource(MediaItem.fromUri(url))
//                player.setMediaSource(mediaSource)
//            } else {
            val mediaItem: MediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
//            }

            player.prepare()
//            player.play()
            this.player = player

            // add player with its index to map
            if (playersMapDownloded.containsKey(url))
                playersMapDownloded.remove(url)
            if (url != null)
                playersMapDownloded[url] = player


//                playIndexThenPausePreviousPlayer(item_index)

            this.player!!.addListener(object : Player.EventListener {

                override fun onPlayerError(error: ExoPlaybackException) {
                    super.onPlayerError(error)
                    this@loadDownloadVideo.context.toast("Oops! Error occurred while playing media.")
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)

                    if (playbackState == Player.STATE_BUFFERING) {
                        if (callback != null)
                            callback.onVideoBuffering(player)
                        // Buffering..
                        // set progress bar visible here
                        // set thumbnail visible
//                        thumbnail.visibility = View.VISIBLE
//                        progressbar.visibility = View.VISIBLE
                    }

                    if (playbackState == Player.STATE_READY) {
                        // [PlayerView] has fetched the video duration so this is the block to hide the buffering progress bar
//                        progressbar.visibility = View.GONE
//                        // set thumbnail gone
//                        thumbnail.visibility = View.GONE
                        if (callback != null)
                            callback.onVideoDurationRetrieved(
                                this@loadDownloadVideo.player!!.duration,
                                player
                            )
                    }

                    if (playbackState == Player.STATE_READY && player.playWhenReady) {
                        // [PlayerView] has started playing/resumed the video
                        if (callback != null)
                            callback.onStartedPlaying(player)
                    }
                    if (playbackState == Player.STATE_ENDED) {
                        if (callback != null)
                            callback.onFinishedPlaying(player)
                    }
                }
            })
        }

    }
}