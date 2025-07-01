package com.example.myapp

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StorageManager {
    private const val DIR = "screenshots"
    private val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    fun save(bitmap: Bitmap, context: Context) {
        val dir = getScreenshotDir(context)
        if (!dir.exists()) dir.mkdirs()
        val name = "screenshot_${formatter.format(Date())}.png"
        val file = File(dir, name)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    fun getScreenshotDir(context: Context): File {
        return File(context.getExternalFilesDir(DIR), "")
    }
}
