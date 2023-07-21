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
    private lateinit var answerButtons: List<Button>

    private var quiz = Quiz(category = "", musicOn = true, soundOn = true, maxQuestions = 40, size = 0)
    private var score = Score(0, 0, false)

    //disable back press button
    override fun onBackPressed() {}

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

        answerButtons =
            listOf(binding.btnAnswerA, binding.btnAnswerB, binding.btnAnswerC, binding.btnAnswerD)

        val intent = intent
        quiz.category = intent.getStringExtra("category").toString()
        quiz.musicOn = intent.getBooleanExtra("music", true)
        quiz.soundOn = intent.getBooleanExtra("sound", true)

        if (!quiz.musicOn)
            binding.musicImage.setImageResource(R.drawable.music_off)

        if (!quiz.soundOn)
            binding.btnSound.setImageResource(R.drawable.sound_off)

        //SOUNDS AFTER USER ANSWER
        mediaPlayerCorrect = MediaPlayer.create(this, R.raw.correct_answer)
        mediaPlayerWrong = MediaPlayer.create(this, R.raw.wrong_answer)

        when (quiz.category) {
            getString(R.string.world_cup) -> {
                quiz.questions = quiz.readCsv(this, R.raw.world_cup)
            }
            getString(R.string.champions_legaue) -> {
                quiz.questions = quiz.readCsv(this, R.raw.champions_league)
            }
            getString(R.string.polish_football) -> {
                quiz.questions = quiz.readCsv(this, R.raw.polish_football)
            }
            getString(R.string.curiosities) -> {
                quiz.questions = quiz.readCsv(this, R.raw.curiosities)
            }
        }
        quiz.size = if(quiz.questions.size > quiz.maxQuestions) quiz.maxQuestions else quiz.questions.size

        //SHUFFLE QUESTIONS
        quiz.questions.shuffle()

        //SHOW FIRST QUESTION
        showNextQuestion()

        binding.btnAnswerA.setOnClickListener { checkAnswer(binding.btnAnswerA) }
        binding.btnAnswerB.setOnClickListener { checkAnswer(binding.btnAnswerB) }
        binding.btnAnswerC.setOnClickListener { checkAnswer(binding.btnAnswerC) }
        binding.btnAnswerD.setOnClickListener { checkAnswer(binding.btnAnswerD) }
        binding.btnInfo.setOnClickListener {
            if(!score.isCorrectAnswer) showInfo() else checkQuizCounter()
        }
        binding.btnNextQuestion.setOnClickListener{
            checkQuizCounter()
        }

        binding.btnMenu.setOnClickListener {
            Intent(this, CategoryActivity::class.java).apply{
                putExtra("music", quiz.musicOn)
                putExtra("sound", quiz.soundOn)
                startActivity(this)
            }
        }
        binding.btnSound.setOnClickListener {
            quiz.soundOn = !quiz.soundOn
            binding.btnSound.setImageResource(
                if (quiz.soundOn) R.drawable.sound_on else R.drawable.sound_off
            )
        }
        binding.musicImage.setOnClickListener {
            quiz.musicOn = !quiz.musicOn

            binding.musicImage.setImageResource(
                if (quiz.musicOn) R.drawable.music_on else R.drawable.music_off
            )

            BackgroundMusic.musicPlayer?.apply {
                if (quiz.musicOn) start() else pause()
            }
        }
    }

    override fun onPause(){
        super.onPause()
        BackgroundMusic.musicPlayer?.pause()
    }
    override fun onResume(){
        super.onResume()
        if(quiz.musicOn)
            BackgroundMusic.musicPlayer?.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerCorrect.release()
        mediaPlayerWrong.release()
    }

    private fun showNextQuestion() {
        binding.btnInfo.visibility = View.INVISIBLE
        binding.btnMenu.visibility = View.INVISIBLE
        binding.textInfo.visibility = View.INVISIBLE
        binding.btnNextQuestion.visibility = View.INVISIBLE

        timer.start()
        score.questionNum++

        answerButtons.forEach {
            it.setBackgroundColor(ContextCompat.getColor(this, R.color.light_yellow))
            it.isClickable = true
            it.visibility = View.VISIBLE
        }

        //READ ANT THEN REMOVE FIRST QUESTION FROM QUERY
        quiz.actualQuestion = quiz.questions.first()
        quiz.questions.removeFirst()

        binding.textQuestion.text =
            getString(R.string.question, score.questionNum.toString(), quiz.size.toString(), quiz.actualQuestion?.text)

        //USER INFO IF WRONG ANSWER
        binding.textInfo.text = getString(R.string.info, quiz.actualQuestion?.correctAnswer, quiz.actualQuestion?.info)

        //SHUFFLE ANSWERS
        quiz.actualQuestion?.answersList?.shuffle()

        answerButtons.forEachIndexed { index, answerBtn ->
            answerBtn.text = quiz.actualQuestion?.answersList!![index]
        }
    }

    private fun checkAnswer(btnAnswer: Button?) {
        binding.btnInfo.visibility = View.VISIBLE
        binding.btnMenu.visibility = View.VISIBLE
        answerButtons.forEach()
        {
            it.isClickable = false
        }

        timer.cancel() //STOP THE TIMER

        val userAnswer = btnAnswer?.text

        //IF CORRECT ANSWER
        if (userAnswer == quiz.actualQuestion?.correctAnswer) {
            btnAnswer?.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            score.points++
            score.isCorrectAnswer = true

            if (quiz.soundOn)
                mediaPlayerCorrect.start()
        }
        //IF WRONG ANSWER
        else {
            score.isCorrectAnswer = false
            if (quiz.soundOn)
                mediaPlayerWrong.start()

            btnAnswer?.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            //SET ORANGE BACKGROUND TO THE CORRECT ANSWER
            when (quiz.actualQuestion?.correctAnswer) {
                binding.btnAnswerA.text -> binding.btnAnswerA.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                binding.btnAnswerB.text -> binding.btnAnswerB.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                binding.btnAnswerC.text -> binding.btnAnswerC.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                binding.btnAnswerD.text -> binding.btnAnswerD.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
            }
        }
    }

    private fun checkQuizCounter() {
        //CHECK IF IT IS THE GAME END
        if (quiz.questions.size == 0 || score.questionNum == quiz.maxQuestions) {
            Intent(this, ResultActivity::class.java).apply {
                putExtra("score", score.points.toString())
                putExtra("lastCategory", quiz.category)
                putExtra("questions", score.questionNum.toString())
                putExtra("sound", quiz.soundOn)
                putExtra("music", quiz.musicOn)
                startActivity(this)
            }
        }
        else
            showNextQuestion()
    }

    private fun showInfo()
    {
        answerButtons.forEach()
        {
            it.visibility = View.INVISIBLE
        }
        binding.textInfo.visibility = View.VISIBLE
        binding.btnMenu.visibility = View.INVISIBLE
        binding.btnInfo.visibility = View.INVISIBLE
        binding.btnNextQuestion.visibility = View.VISIBLE
    }
}