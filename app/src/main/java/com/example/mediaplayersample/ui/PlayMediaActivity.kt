package com.example.mediaplayersample.ui

import android.os.Bundle
import android.view.SurfaceHolder

import androidx.appcompat.app.AppCompatActivity

import com.example.mediaplayersample.databinding.ActivityPlayMediaActvityBinding
import com.example.mediaplayersample.ui.mediaCodec.AudioDecoder
import com.example.mediaplayersample.ui.mediaCodec.VideoDecodeThread


class PlayMediaActivity : AppCompatActivity() , SurfaceHolder.Callback{

    private var videoDecodeThread : VideoDecodeThread? = null
    private var videoPath : String = ""

    private val viewBinding by lazy {
        ActivityPlayMediaActvityBinding.inflate(layoutInflater)
    }

    private val audioDecoder: AudioDecoder by lazy {
        AudioDecoder()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        videoPath = intent.getStringExtra("videoPath").toString()
        viewBinding.surfaceInPlayMediaActivity.holder.addCallback(this@PlayMediaActivity)
        videoDecodeThread = VideoDecodeThread()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (videoDecodeThread?.init(holder.surface, videoPath) == true) {
            audioDecoder.startPlay(videoPath)
            videoDecodeThread?.start()
        } else {
            videoDecodeThread = null
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        videoDecodeThread?.close()
    }
    override fun onPause() {
        super.onPause()
        videoDecodeThread?.close()
        audioDecoder.stop()
    }

}