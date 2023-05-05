package com.example.footballquiz

import android.content.Context
import android.media.MediaPlayer

object BackgroundMusic {
    var music_player: MediaPlayer? = null

    fun soundPlayer(ctx: Context?, raw_id: Int) {
        music_player?.release()
        music_player = MediaPlayer.create(ctx, raw_id)
        music_player?.isLooping = true // Set looping

        music_player?.start()
    }
}