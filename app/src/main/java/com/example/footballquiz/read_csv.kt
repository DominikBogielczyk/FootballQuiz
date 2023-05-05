package com.example.footballquiz

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset


fun readCsv(c : Context, id :Int) : MutableList<MutableList<String>> {
    val result = mutableListOf<MutableList<String>>()

    val inputStream: InputStream = c.resources.openRawResource(id)

    val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
    reader.readLines().forEach {
        //check if isn't only '/n' sign
        if(it.length > 10)
        {
            val items = it.split(";")
            //Log.i("Split: ", items.toString())
            result.add(items.toMutableList())
        }
    }
    Log.i("Size: ", result.size.toString())
    return result
}

