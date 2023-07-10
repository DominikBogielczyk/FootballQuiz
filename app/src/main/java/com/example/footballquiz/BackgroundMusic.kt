package com.example.footballquiz

import android.content.Context
import android.media.MediaPlayer

object BackgroundMusic {
    var musicPlayer: MediaPlayer? = null

    fun soundPlayer(ctx: Context?, raw_id: Int) {
        musicPlayer?.release()
        musicPlayer = MediaPlayer.create(ctx, raw_id)
        musicPlayer?.isLooping = true // Set looping

        musicPlayer?.start()
    }
}