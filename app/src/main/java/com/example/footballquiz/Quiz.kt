package com.example.footballquiz

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class Quiz (var category: String, var musicOn: Boolean, var soundOn: Boolean, var maxQuestions: Int, var size: Int)
{
    var questions = mutableListOf<Question>()
    var actualQuestion :Question? = null

    fun readCsv(c : Context, id :Int) : MutableList<Question> {
        val questions = mutableListOf<Question>()
        val inputStream: InputStream = c.resources.openRawResource(id)
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
        reader.readLines().forEach {
            //check if isn't only '/n' sign
            if(it.length > 10)
            {
                val items = it.split(";")
                Log.i("Split: ", items.toString())
                val q = Question(items[0], items[1], items.subList(1,5) as MutableList<String>, items.getOrNull(5) ?: "")
                questions.add(q)
            }
        }
        Log.i("Size: ", questions.size.toString())
        return questions
    }
}