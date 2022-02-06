package com.example.picture_here_app.activity.fragment.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.picture_here_app.activity.activity.AppActivity
import com.example.picture_here_app.databinding.FragmentProfilBinding

class ProfilFragment : Fragment() {
    private lateinit var binding: FragmentProfilBinding
    private lateinit var appActivity: AppActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfilBinding.inflate(inflater)
        appActivity = activity as AppActivity

        binding.btnLogout.setOnClickListener {
            appActivity.logout()
        }

        binding.textView3.text = appActivity.user.utilisateur.profil.pseudo

        return binding.root
    }
}