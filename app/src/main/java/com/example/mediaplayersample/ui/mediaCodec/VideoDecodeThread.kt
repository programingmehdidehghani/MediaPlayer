package com.example.mediaplayersample.ui.mediaCodec

import android.media.*
import android.util.Log
import android.view.Surface

class VideoDecodeThread : Thread() {

    companion object {
        private const val VIDEO = "video/"
        private const val TAG = "VideoDecoder"
    }

    private lateinit var extractorVideo: MediaExtractor
    private lateinit var decodeVideo: MediaCodec

    private var isStop = false

    fun init(surface: Surface, videoPath: String): Boolean {
        isStop = false
        try {
            Log.i(TAG,"instance mediaExtractorVideo .....")
            extractorVideo = MediaExtractor()
            extractorVideo.setDataSource(videoPath)

            (0..extractorVideo.trackCount).forEach { index ->
                val format = extractorVideo.getTrackFormat(index)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith(VIDEO) == true) {
                    extractorVideo.selectTrack(index)
                    decodeVideo = MediaCodec.createDecoderByType(mime)
                    try {
                        Log.d(TAG, "format : $format")
                        decodeVideo.configure(format, surface, null, 0)
                    } catch (e: java.lang.IllegalStateException) {
                        Log.e(TAG, "codec $mime failed configuration. $e")
                        return false
                    }
                    decodeVideo.start()
                    return true
                }

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun run() {
        super.run()
        val newBufferInfo = MediaCodec.BufferInfo()
        var isFirst = false
        var startWhen = 0L
        while (isStop.not()) {
            decodeVideo.dequeueInputBuffer(1000).takeIf { it >= 0 }?.let { index ->
                val inputBuffer = decodeVideo.getInputBuffer(index)
                if (inputBuffer != null) {
                    Log.i(TAG,"start stream video")
                    val sampleSize = extractorVideo.readSampleData(inputBuffer, 0)
                    if (extractorVideo.advance() && sampleSize > 0) {
                        decodeVideo.queueInputBuffer(
                            index,
                            0,
                            sampleSize,
                            extractorVideo.sampleTime,
                            0
                        )
                    } else {
                        Log.i(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM")
                        decodeVideo.queueInputBuffer(
                            index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                    }
                } else {
                    Log.i(TAG, "InputBuffer for decoder is null")
                }
            }

            when (val outIndex = decodeVideo.dequeueOutputBuffer(newBufferInfo, 1000)) {
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    Log.i(TAG, "INFO_OUTPUT_FORMAT_CHANGED format : " + decodeVideo.outputFormat)
                }
                MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    Log.i(TAG, "INFO_TRY_AGAIN_LATER")
                }
                else -> {
                    if (outIndex >= 0) {
                        if (isFirst.not()) {
                            startWhen = System.currentTimeMillis()
                            isFirst = true
                        }
                        try {
                            val sleepTime: Long =
                                newBufferInfo.presentationTimeUs / 1000 - (System.currentTimeMillis() - startWhen)
                            if (sleepTime > 0) {
                                sleep(sleepTime)
                            }
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        decodeVideo.releaseOutputBuffer(outIndex, true)
                    }
                }
            }
            if (newBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                break
            }
        }
        decodeVideo.stop()
        decodeVideo.release()
        extractorVideo.release()
    }

    fun close() {
        isStop = true
    }
}