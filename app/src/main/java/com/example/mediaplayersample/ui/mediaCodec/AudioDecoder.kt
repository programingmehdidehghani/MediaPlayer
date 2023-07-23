package com.example.mediaplayersample.ui.mediaCodec

import android.media.*
import android.util.Log
import java.nio.ByteBuffer

class AudioDecoder {

    companion object {
        private const val TIMEOUT_US = 1_000L
    }

    private lateinit var extractorAudio: MediaExtractor
    private lateinit var decoderAudio: MediaCodec

    private var endOfReceived = false
    private var sampleRate = 0

    fun startPlay(file: String) {
        endOfReceived = false
        extractorAudio = MediaExtractor()
        try {
            extractorAudio.setDataSource(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //var channel = 0
        var audioFormat : MediaFormat ? = null
        (0 until extractorAudio.trackCount).forEach { trackNumber ->
            val format = extractorAudio.getTrackFormat(trackNumber)
            format.getString(MediaFormat.KEY_MIME).takeIf {
                it?.startsWith("audio/") == true
            }?.let {
                audioFormat = format
                extractorAudio.selectTrack(trackNumber)
                Log.d("TEMP", "format : $format")
                format.getByteBuffer("csd-0")?.let { csd ->
                    (0 until csd.capacity()).forEach {
                        Log.e("TEMP", "csd : ${csd.array()[it]}")
                    }
                }
                sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                //channel = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                return@forEach
            }
        }
        decoderAudio = MediaCodec.createDecoderByType("audio/mp4a-latm")
        decoderAudio.configure(audioFormat, null, null, 0)
        decoderAudio.start()
        Thread(aacDecoderAndPlayRunnable).start()
    }

    private var aacDecoderAndPlayRunnable = Runnable { AACDecoderAndPlay() }

    private fun AACDecoderAndPlay() {
        val inputBuffers: Array<ByteBuffer> = decoderAudio.inputBuffers
        var outputBuffers: Array<ByteBuffer> = decoderAudio.outputBuffers
        val info = MediaCodec.BufferInfo()
        val buffsize: Int = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        var audioTrack =  AudioTrack(
            AudioManager.STREAM_MUSIC, sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT,
            buffsize,
            AudioTrack.MODE_STREAM
        )
        audioTrack.play()
        while (!endOfReceived) {
            val inIndex: Int = decoderAudio.dequeueInputBuffer(TIMEOUT_US)
            if (inIndex >= 0) {
                val buffer = inputBuffers[inIndex]
                val sampleSize: Int = extractorAudio.readSampleData(buffer, 0)
                if (sampleSize < 0) {
                    Log.d("DecodeActivity", "InputBuffer BUFFER_FLAG_END_OF_STREAM")
                    decoderAudio.queueInputBuffer(
                        inIndex,
                        0,
                        0,
                        0,
                        MediaCodec.BUFFER_FLAG_END_OF_STREAM
                    )
                } else {
                    decoderAudio.queueInputBuffer(
                        inIndex,
                        0,
                        sampleSize,
                        extractorAudio.sampleTime ?: 0L,
                        0
                    )
                    extractorAudio.advance()
                }
                val outIndex: Int = decoderAudio.dequeueOutputBuffer(info, TIMEOUT_US) ?: -1
                when (outIndex) {
                    MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                        Log.d("DecodeActivity", "INFO_OUTPUT_BUFFERS_CHANGED")
                        outputBuffers = decoderAudio.outputBuffers
                    }
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        val format: MediaFormat = decoderAudio.outputFormat
                        Log.d("DecodeActivity", "New format $format")
                        audioTrack.playbackRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                    }
                    MediaCodec.INFO_TRY_AGAIN_LATER -> Log.d(
                        "DecodeActivity",
                        "dequeueOutputBuffer timed out!"
                    )
                    else -> {
                        val outBuffer = outputBuffers[outIndex]
                        Log.v(
                            "DecodeActivity",
                            "We can't use this buffer but render it due to the API limit, $outBuffer"
                        )
                        val chunk = ByteArray(info.size)
                        outBuffer[chunk]
                        outBuffer.clear()
                        audioTrack.write(
                            chunk,
                            info.offset,
                            info.offset + info.size
                        )
                        decoderAudio.releaseOutputBuffer(outIndex, false)
                    }
                }

                if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    Log.d("DecodeActivity", "OutputBuffer BUFFER_FLAG_END_OF_STREAM")
                    break
                }
            }
        }
        decoderAudio.stop()
        decoderAudio.release()
        extractorAudio.release()
        audioTrack.stop()
        audioTrack.release()
    }

    fun stop() {
        endOfReceived = true
    }
}