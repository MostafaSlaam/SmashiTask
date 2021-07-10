package com.example.livesmashi.view.mainActivity

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.livesmashi.model.VideoModel
import com.example.livesmashi.util.PlayerViewAdapter
import com.example.livesmashi.util.SingleLiveEvent


class MainViewModel():ViewModel() {

    val navigateToDownloads = SingleLiveEvent<Any>()
    var urlLiveVideo =
        "https://528dc4ef17d725ed.mediapackage.eu-central-1.amazonaws.com/out/v1/6e9b108dce1c400fa64336c0568e8c1d/index.m3u8"
    var listOfVidos = ArrayList<VideoModel>()
    init {
        listOfVidos.add(VideoModel("https://s3.eu-central-1.amazonaws.com/smashi2019frank/media-convert/null/null_06_16_2021/default/hls/vod3Output1.m3u8"))
        listOfVidos.add(VideoModel("https://s3.eu-central-1.amazonaws.com/smashi2019frank/media-convert/null/null_07_08_2021/default/hls/DW+-+PodcastOutput1.m3u8"))
        listOfVidos.add(VideoModel("https://s3.eu-central-1.amazonaws.com/smashi2019frank/media-convert/Mornings+With+Smashi/Mornings+With+Smashi_07_01_2021/default/hls/20210701_smashi_morning_live_story+2Output1.m3u8"))
    }


    fun onViewAllBtnClick()
    {
        navigateToDownloads.call()
    }

}