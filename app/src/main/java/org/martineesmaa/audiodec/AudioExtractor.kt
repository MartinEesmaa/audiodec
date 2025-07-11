package org.martineesmaa.audiodec

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri

object AudioExtractor {
    fun extractAudioFormat(context: Context, inputUri: Uri): Pair<MediaExtractor, MediaFormat> {
        val extractor = MediaExtractor()
        val fileDescriptor = context.contentResolver.openFileDescriptor(inputUri, "r")?.fileDescriptor
        extractor.setDataSource(fileDescriptor!!)

        val audioTrackIndex = (0 until extractor.trackCount).firstOrNull { trackIndex ->
            extractor.getTrackFormat(trackIndex).getString(MediaFormat.KEY_MIME)?.startsWith("audio/") ?: false
        } ?: throw RuntimeException("No valid audio track found!")

        extractor.selectTrack(audioTrackIndex)
        val format = extractor.getTrackFormat(audioTrackIndex)

        val originalSampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
        println("Original Sample Rate: $originalSampleRate Hz")

        return Pair(extractor, format)
    }
}