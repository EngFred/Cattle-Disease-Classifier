package com.engineerfred.nassa

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

fun Uri.toBitmap(context: Context): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(this)
        BitmapFactory.decodeStream(inputStream).also {
            inputStream?.close() // Close the stream to prevent memory leaks
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

//fun loadFAQData(context: Context): List<FAQ> {
//    return try {
//        val jsonString = context.assets.open("faq.json").bufferedReader().use { it.readText() }
//        val listType = object : TypeToken<List<FAQ>>() {}.type
//        Gson().fromJson(jsonString, listType)
//    } catch (e: IOException) {
//        e.printStackTrace()
//        emptyList()
//    }
//}