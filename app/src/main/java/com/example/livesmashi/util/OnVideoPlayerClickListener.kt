package com.example.livesmashi.util

import android.view.View
import com.example.livesmashi.model.VideoModel


interface OnVideoPlayerClickListener {
    fun onItemClick(
        view: View?,
        position: Int,
        model: VideoModel?
    )
    fun download(position: Int)
}