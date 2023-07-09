package com.example.footballquiz

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.footballquiz.databinding.ActivityQuizBinding

class QuizActivity : AppCompatActivity() {

    private lateinit var mediaPlayerCorrect: MediaPlayer
    private lateinit var mediaPlayerWrong: MediaPlayer
    private lateinit var binding: ActivityQuizBinding
    private var correctAnswer = ""
    private var points = 0
    private var questionNum = 0
    private var quizSize = 0
    private var category = ""
    private var soundOn = true
    private var musicOn = true
    private var quiz = mutableListOf<MutableList<String>>()

    //disable back press button
    override fun onBackPressed() {}

    private companion object {
        const val MAX_QUESTIONS = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val intent = intent
        category = intent.getStringExtra("category").toString()
        musicOn = intent.getBooleanExtra("music", true)
        soundOn = intent.getBooleanExtra("sound", true)

        if (!musicOn)
            binding.musicImage.setImageResource(R.drawable.music_off)

        if (!soundOn)
            binding.soundImage.setImageResource(R.drawable.sound_off)

        //SOUNDS AFTER USER ANSWER
        mediaPlayerCorrect = MediaPlayer.create(this, R.raw.correct_answer)
        mediaPlayerWrong = MediaPlayer.create(this, R.raw.wrong_answer)

        when (category) {
            getString(R.string.world_cup) -> {
                quiz = readCsv(this, R.raw.world_cup)
            }
            getString(R.string.champions_legaue) -> {
                quiz = readCsv(this, R.raw.champions_league)
            }
            getString(R.string.polish_football) -> {
                quiz = readCsv(this, R.raw.polish_football)
            }
            getString(R.string.curiosities) -> {
                quiz = readCsv(this, R.raw.curiosities)
            }
        }
        quizSize = quiz.size

        //SHUFFLE QUESTIONS
        quiz.shuffle()
        showNextQuestion()

        binding.btnAnswerA.setOnClickListener { checkAnswer(binding.btnAnswerA) }
        binding.btnAnswerB.setOnClickListener { checkAnswer(binding.btnAnswerB) }
        binding.btnAnswerC.setOnClickListener { checkAnswer(binding.btnAnswerC) }
        binding.btnAnswerD.setOnClickListener { checkAnswer(binding.btnAnswerD) }
        binding.btnNext.setOnClickListener { checkQuizCounter() }

        binding.btnMenu.setOnClickListener {
            Intent(this, CategoryActivity::class.java).apply{
                putExtra("music", musicOn)
                putExtra("sound", soundOn)
                startActivity(this)
            }
        }

        binding.soundImage.setOnClickListener {
            soundOn = !soundOn
            if (soundOn)
                binding.soundImage.setImageResource(R.drawable.sound_on)
            else
                binding.soundImage.setImageResource(R.drawable.sound_off)
        }

        binding.musicImage.setOnClickListener {
            musicOn = !musicOn
            if (musicOn) {
                binding.musicImage.setImageResource(R.drawable.music_on)
                BackgroundMusic.music_player?.start()
            } else {
                binding.musicImage.setImageResource(R.drawable.music_off)
                BackgroundMusic.music_player?.pause()
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerCorrect.release()
        mediaPlayerWrong.release()
    }

    private fun showNextQuestion() {
        questionNum++
        binding.btnNext.visibility = View.INVISIBLE
        binding.btnMenu.visibility = View.INVISIBLE

        val answerButtons =
            listOf(binding.btnAnswerA, binding.btnAnswerB, binding.btnAnswerC, binding.btnAnswerD)
        answerButtons.forEach {
            it.setBackgroundColor(ContextCompat.getColor(this, R.color.light_yellow))
            it.isClickable = true
        }

        val record = quiz[0]

        //QUESTION
        val size = if (quizSize > MAX_QUESTIONS) MAX_QUESTIONS else quizSize

        binding.textQuestion.text =
            getString(R.string.question, questionNum.toString(), size.toString(), record[0])
        //RIGHT ANSWER
        correctAnswer = record[1]

        //REMOVE QUESTION FROM RECORD BEFORE SHUFFLE
        record.removeAt(0)

        //SHUFFLE ANSWERS
        record.shuffle()

        binding.btnAnswerA.text = record.getOrNull(0) ?: ""
        binding.btnAnswerB.text = record.getOrNull(1) ?: ""
        binding.btnAnswerC.text = record.getOrNull(2) ?: ""
        binding.btnAnswerD.text = record.getOrNull(3) ?: ""

        //REMOVE THIS QUESTION FROM QUERY
        quiz.removeAt(0)

    }

    private fun checkAnswer(btnAnswer: Button) {
        val userAnswer = btnAnswer.text

        //CORRECT ANSWER
        if (userAnswer == correctAnswer) {
            btnAnswer.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            points++

            if (soundOn)
                mediaPlayerCorrect.start()
        }
        //WRONG ANSWER
        else {
            if (soundOn)
                mediaPlayerWrong.start()

            btnAnswer.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            //SET ORANGE BACKGROUND IF TO THE CORRECT ANSWER
            when (correctAnswer) {
                binding.btnAnswerA.text -> binding.btnAnswerA.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                binding.btnAnswerB.text -> binding.btnAnswerB.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                binding.btnAnswerC.text -> binding.btnAnswerC.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                binding.btnAnswerD.text -> binding.btnAnswerD.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
            }

        }
        binding.btnNext.visibility = View.VISIBLE
        binding.btnMenu.visibility = View.VISIBLE

        binding.btnAnswerA.isClickable = false
        binding.btnAnswerB.isClickable = false
        binding.btnAnswerC.isClickable = false
        binding.btnAnswerD.isClickable = false
    }

    private fun checkQuizCounter() {
        if (quiz.size == 0 || questionNum == MAX_QUESTIONS) {
            Intent(this, ResultActivity::class.java).apply {
                putExtra("score", points.toString())
                putExtra("lastCategory", category)
                putExtra("questions", questionNum.toString())
                putExtra("sound", soundOn)
                putExtra("music", musicOn)
                startActivity(this)
            }
        }
        else
            showNextQuestion()
    }
}