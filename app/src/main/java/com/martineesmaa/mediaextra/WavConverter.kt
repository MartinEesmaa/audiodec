package com.martineesmaa.mediaextra

import java.io.RandomAccessFile
import android.media.MediaFormat

object WavConverter {
    fun finalizeWavFile(outputPath: String, format: MediaFormat, totalAudioLen: Int) {
        val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
        val channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
        val byteRate = sampleRate * channels * 2
        val totalFileSize = totalAudioLen + 36

        RandomAccessFile(outputPath, "rw").use { raf ->
            raf.seek(0)
            raf.write("RIFF".toByteArray())
            raf.writeInt(totalFileSize)
            raf.write("WAVE".toByteArray())
            raf.write("fmt ".toByteArray())
            raf.writeInt(16)
            raf.writeShort(1)
            raf.writeInt((channels * 2).toInt())
            raf.writeInt(sampleRate)
            raf.writeInt(byteRate)
            raf.writeInt((channels * 2).toInt())
            raf.writeShort(16)
            raf.write("data".toByteArray())
            raf.writeInt(totalAudioLen)
        }
    }
}