package com.example.mediaplayersample.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mediaplayersample.databinding.LayoutSplashActivityMainBinding
import java.util.*


class SplashActivity : AppCompatActivity() {

    private val viewBinding by lazy { LayoutSplashActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        Timer().schedule(object : TimerTask() { override fun run() { startActivity(Intent(applicationContext, MainActivity::class.java)) } }, 1000)
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}