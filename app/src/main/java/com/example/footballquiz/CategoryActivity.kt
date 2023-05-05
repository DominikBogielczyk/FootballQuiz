package com.example.footballquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.footballquiz.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCategoryBinding
    private lateinit var musicExtra :String
    private lateinit var soundExtra :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        musicExtra = intent.getStringExtra("music").toString()
        soundExtra = intent.getStringExtra("sound").toString()

        binding.btnWorldCup.setOnClickListener()
        {
           startQuiz("WorldCup")
        }
        binding.btnChampionsLeague.setOnClickListener()
        {
            startQuiz("ChampionsLeague")
        }
        binding.btnPolishFootball.setOnClickListener()
        {
            startQuiz("Polish")
        }
        binding.btnCuriosities.setOnClickListener()
        {
            startQuiz("Curiosities")
        }
    }
    private fun startQuiz(category: String) {
        val (cat, music) = when (category) {
            "WorldCup" -> Pair(binding.btnWorldCup.text, R.raw.world_cup_music)
            "ChampionsLeague" -> Pair(binding.btnChampionsLeague.text, R.raw.champions_league_anthem)
            "Polish" -> Pair(binding.btnPolishFootball.text, R.raw.polish_football_music)
            "Curiosities" -> Pair(binding.btnCuriosities.text, R.raw.menu)
            else -> return
        }

        val intent = Intent(this, QuizActivity::class.java).apply {
            putExtra("category", cat)
            putExtra("music", musicExtra)
            putExtra("sound", soundExtra)
        }
        startActivity(intent)
        if(musicExtra == "true")
            BackgroundMusic.soundPlayer(this, music)
        else
            BackgroundMusic.music_player?.pause()
    }
    override fun onPause(){
        super.onPause()
        BackgroundMusic.music_player?.pause()
    }
    override fun onResume(){
        super.onResume()
        if(musicExtra == "true")
            BackgroundMusic.music_player?.start()
    }
}