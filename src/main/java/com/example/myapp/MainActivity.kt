package com.example.myapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_MEDIA_PROJECTION = 1
    }

    private lateinit var projectionManager: MediaProjectionManager
    private var resultCode: Int = 0
    private var data: Intent? = null
    private var capturing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        savedInstanceState?.let {
            capturing = it.getBoolean("capturing", false)
            resultCode = it.getInt("resultCode", 0)
            data = it.getParcelable("data")
        }

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            if (!capturing) {
                startActivityForResult(projectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
            }
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            if (capturing) {
                stopCapture()
            }
        }

        updateButtons()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK && data != null) {
            this.resultCode = resultCode
            this.data = data
            startCapture()
        }
    }

    private fun startCapture() {
        val intent = Intent(this, ScreenshotService::class.java).apply {
            action = ScreenshotService.ACTION_START
            putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
            putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data)
        }
        startForegroundService(intent)
        capturing = true
        updateButtons()
    }

    private fun stopCapture() {
        val intent = Intent(this, ScreenshotService::class.java).apply {
            action = ScreenshotService.ACTION_STOP
        }
        startService(intent)
        capturing = false
        updateButtons()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("capturing", capturing)
        outState.putInt("resultCode", resultCode)
        outState.putParcelable("data", data)
    }

    private fun updateButtons() {
        findViewById<Button>(R.id.btn_start).isEnabled = !capturing
        findViewById<Button>(R.id.btn_stop).isEnabled = capturing
    }
}
