package com.martineesmaa.mediaextra

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.FileOutputStream

object AudioDecoder {
    fun decodeToPCM(extractor: MediaExtractor, format: MediaFormat, outputPath: String): Int {
        val codec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME)!!)
        codec.configure(format, null, null, 0)
        codec.start()

        var totalAudioLen = 0
        FileOutputStream(outputPath).use { outputStream ->
            val bufferInfo = MediaCodec.BufferInfo()

            while (extractor.advance()) {
                val inputBufferId = codec.dequeueInputBuffer(10000)
                if (inputBufferId >= 0) {
                    val inputBuffer = codec.getInputBuffer(inputBufferId)
                    val size = extractor.readSampleData(inputBuffer!!, 0)
                    codec.queueInputBuffer(inputBufferId, 0, size, extractor.sampleTime, 0)
                }

                val outputBufferId = codec.dequeueOutputBuffer(bufferInfo, 10000)
                if (outputBufferId >= 0) {
                    val outputBuffer = codec.getOutputBuffer(outputBufferId)
                    val pcmData = ByteArray(bufferInfo.size)

                    if (bufferInfo.size > 0) {
                        outputBuffer!!.get(pcmData)
                        outputStream.write(pcmData)
                        totalAudioLen += pcmData.size
                    }

                    codec.releaseOutputBuffer(outputBufferId, false)
                }
            }

            codec.stop()
            codec.release()
        }

        return totalAudioLen
    }
}