package com.example.footballquiz

import android.content.Intent
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
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
        const val MAX_QUESTIONS = 40
    }

    private val timer = object: CountDownTimer(20000, 50) {
        override fun onTick(millisUntilFinished: Long) {
            val t = 20000 - millisUntilFinished.toInt()
            binding.progressBar.progress = t
            binding.progressBar.progressTintList = when(t){
                in 0..10000 ->  ColorStateList.valueOf(ContextCompat.getColor(this@QuizActivity, R.color.green))
                in 10001..15000 -> ColorStateList.valueOf(ContextCompat.getColor(this@QuizActivity, R.color.light_yellow))
                else -> ColorStateList.valueOf(ContextCompat.getColor(this@QuizActivity, R.color.red))
            }
        }
        override fun onFinish() {
            checkAnswer(null)
        }
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
            binding.btnSound.setImageResource(R.drawable.sound_off)

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
        binding.btnSound.setOnClickListener {
            soundOn = !soundOn
            binding.btnSound.setImageResource(
                if (soundOn) R.drawable.sound_on else R.drawable.sound_off
            )
        }
        binding.musicImage.setOnClickListener {
            musicOn = !musicOn

            binding.musicImage.setImageResource(
                if (musicOn) R.drawable.music_on else R.drawable.music_off
            )

            BackgroundMusic.musicPlayer?.apply {
                if (musicOn) start() else pause()
            }
        }
    }

    override fun onPause(){
        super.onPause()
        BackgroundMusic.musicPlayer?.pause()
    }
    override fun onResume(){
        super.onResume()
        if(musicOn)
            BackgroundMusic.musicPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerCorrect.release()
        mediaPlayerWrong.release()
    }

    private fun showNextQuestion() {
        timer.start()

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

    private fun checkAnswer(btnAnswer: Button?) {
        val userAnswer = btnAnswer?.text

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

            btnAnswer?.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            //SET ORANGE BACKGROUND TO THE CORRECT ANSWER
            when (correctAnswer) {
                binding.btnAnswerA.text -> binding.btnAnswerA.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                binding.btnAnswerB.text -> binding.btnAnswerB.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                binding.btnAnswerC.text -> binding.btnAnswerC.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                binding.btnAnswerD.text -> binding.btnAnswerD.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
            }
        }
        binding.btnNext.visibility = View.VISIBLE
        binding.btnMenu.visibility = View.VISIBLE

        val answerButtons =
            listOf(binding.btnAnswerA, binding.btnAnswerB, binding.btnAnswerC, binding.btnAnswerD)
        answerButtons.forEach()
        {
            it.isClickable = false
        }
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