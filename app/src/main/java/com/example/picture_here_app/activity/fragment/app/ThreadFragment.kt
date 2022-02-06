package com.example.picture_here_app.activity.fragment.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.picture_here_app.activity.activity.AppActivity
import com.example.picture_here_app.databinding.FragmentThreadBinding

class ThreadFragment : Fragment() {
    private lateinit var binding: FragmentThreadBinding
    private lateinit var appActivity: AppActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentThreadBinding.inflate(inflater)
        appActivity = activity as AppActivity

        return binding.root
    }
}