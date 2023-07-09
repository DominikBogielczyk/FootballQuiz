package com.example.footballquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.footballquiz.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private var musicOn = true
    private var soundExtra = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        musicOn = intent.getBooleanExtra("music", true)
        soundExtra = intent.getBooleanExtra("sound", true)

        val buttonQuizMap = mapOf(
            binding.btnWorldCup to "WorldCup",
            binding.btnChampionsLeague to "ChampionsLeague",
            binding.btnPolishFootball to "Polish",
            binding.btnCuriosities to "Curiosities"
        )

        buttonQuizMap.forEach { (btnCategory, quizCategory) ->
            btnCategory.setOnClickListener {
                startQuiz(quizCategory)
            }
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

        Intent(this, QuizActivity::class.java).apply {
            putExtra("category", cat)
            putExtra("music", musicOn)
            putExtra("sound", soundExtra)
            startActivity(this)
        }

        if(musicOn)
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
        if(musicOn)
            BackgroundMusic.music_player?.start()
    }
}