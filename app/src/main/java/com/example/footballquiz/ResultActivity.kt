package com.example.footballquiz

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.footballquiz.databinding.ActivityResultBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.system.exitProcess


class ResultActivity : AppCompatActivity() {

    //disable back press button
    override fun onBackPressed() {}

    private lateinit var binding: ActivityResultBinding
    private var musicOn = true
    private var soundOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val intent = intent
        val pts = intent.getStringExtra("score")
        val lastCategory = intent.getStringExtra("lastCategory")
        val questionsNum = intent.getStringExtra("questions")?.toInt()
        musicOn = intent.getBooleanExtra("music", true)
        soundOn = intent.getBooleanExtra("sound", true)

        if (pts != null && questionsNum != null)
            binding.textPoints.text = getString(R.string.result,
                lastCategory,
                pts,
                (100 * pts.toInt() / questionsNum).toString())

        binding.btnOnceMore.setOnClickListener()
        {
            Intent(this, CategoryActivity::class.java).apply {
                putExtra("music", musicOn)
                putExtra("sound", soundOn)
                startActivity(this)
            }
        }
        binding.btnClose.setOnClickListener()
        {
            finishAffinity() // Close all activites
            exitProcess(0)  // closing files, releasing resources
        }
        binding.btnShare.setOnClickListener()
        {
            //TAKE SCREENSHOT
            val filename = "screen.png"
            val bitmap = Bitmap.createBitmap(binding.root.width, binding.root.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            binding.root.draw(canvas)
            try
            {
                val fos = openFileOutput(filename, MODE_PRIVATE)
                bitmap.compress(CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
            }
            catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            catch (e: IOException) {
                e.printStackTrace()
            }

            //MESSENGER
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/png"
            shareIntent.setPackage("com.facebook.orca")
            val file = File(filesDir, filename)
            val uri = FileProvider.getUriForFile(this, "com.anni.shareimage.fileprovider", file)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            try {
                startActivity(Intent.createChooser(shareIntent, "Share image using"))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(this, "Facebook Messenger isn't installed", Toast.LENGTH_LONG).show()
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
}