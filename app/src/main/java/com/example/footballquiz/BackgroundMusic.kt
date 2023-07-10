package com.example.footballquiz

import android.content.Context
import android.media.MediaPlayer

object BackgroundMusic {
    var musicPlayer: MediaPlayer? = null
    private var rawId: Int = 0

    fun soundPlayer(ctx: Context?, raw_id: Int) {
        rawId = raw_id
        musicPlayer?.release()
        musicPlayer = MediaPlayer.create(ctx, raw_id)
        musicPlayer?.isLooping = true // Set looping

        musicPlayer?.start()
    }
    fun getRawId() :Int
    {
        return rawId
    }
}