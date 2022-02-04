package com.example.picture_here_app.activity.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.picture_here_app.R
import com.example.picture_here_app.databinding.ActivityAppBinding
import com.example.picture_here_app.databinding.ActivityLoginBinding

class AppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}