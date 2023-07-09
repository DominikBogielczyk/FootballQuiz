package com.example.footballquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.footballquiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var musicOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        BackgroundMusic.soundPlayer(this, R.raw.menu)

        binding.btnPlay.setOnClickListener {
            Intent(this, CategoryActivity::class.java).apply {
                putExtra("music", musicOn)
                putExtra("sound", musicOn)
                startActivity(this)
            }
        }

        binding.btnMusic.setOnClickListener {
            musicOn = !musicOn
            val musicResource = if (musicOn) R.drawable.music_on else R.drawable.music_off
            binding.btnMusic.setImageResource(musicResource)

            BackgroundMusic.music_player?.apply {
                if (musicOn) start() else pause()
            }
        }
    }
    override fun onPause(){
        super.onPause()
        BackgroundMusic.music_player?.pause()
    }
    override fun onResume(){
        super.onResume()
        BackgroundMusic.music_player?.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        BackgroundMusic.music_player?.release()
    }
}