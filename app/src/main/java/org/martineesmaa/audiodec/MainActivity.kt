package org.martineesmaa.audiodec

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.martineesmaa.audiodec.R
import java.io.File

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_PICK_AUDIO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val selectFileBtn: Button = findViewById(R.id.selectFileBtn)
        selectFileBtn.setOnClickListener { selectFile() }
    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*" // Allows both video and audio selection
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/*", "audio/*")) // Filters for both
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_AUDIO && resultCode == Activity.RESULT_OK) {
            val fileUri: Uri? = data?.data
            fileUri?.let {
                val outputPath = getOutputPath()
                Thread {
                    val (extractor, format) = AudioExtractor.extractAudioFormat(this, it)
                    AudioDecoder.decodeToPCM(
                        extractor,
                        format,
                        outputPath
                    ) // Keep PCM extraction only
                    Log.d("AudioExtractor", "File URI: $fileUri")
                    runOnUiThread { Toast.makeText(this, "PCM saved at $outputPath", Toast.LENGTH_LONG).show() }
                }.start()
            }
        }
    }

    private fun getOutputPath(): String {
        val outputFile = File(getExternalFilesDir(null), "output.pcm")
        return outputFile.absolutePath
    }
}