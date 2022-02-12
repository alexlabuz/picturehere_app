package com.example.picture_here_app.activity.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.picture_here_app.R
import com.example.picture_here_app.activity.fragment.login.LoginFragment
import com.example.picture_here_app.activity.fragment.login.RegisterFragment
import com.example.picture_here_app.databinding.ActivityLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityLoginBinding
    lateinit var preference : SharedPreferences
    val registerFragment = RegisterFragment()
    var loginFragment = LoginFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        preference = getSharedPreferences(getString(R.string.preference_app), Context.MODE_PRIVATE)

        binding.bottomNavigationLogin.setOnNavigationItemSelectedListener(this)

        val extra = intent.extras
        if(extra != null){
            val expiredToken = extra.getBoolean(getString(R.string.expired_token))
            if(expiredToken){
                Snackbar.make(binding.frameLayoutLogin, "Vous avez été déconnecté", Snackbar.LENGTH_SHORT).show()
            }
        }

        loadFragment(loginFragment)
        setContentView(binding.root)
    }

    fun load(enabled: Boolean){
        binding.load.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    fun loadFragment(fragment: Fragment?) : Boolean{
        if(fragment != null){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout_login, fragment)
                .commit()
            return true
        }
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_login){
            return loadFragment(loginFragment)
        }
        if(item.itemId == R.id.action_register){
            return loadFragment(registerFragment)
        }
        return false
    }
}