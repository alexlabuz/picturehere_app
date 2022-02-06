package com.example.picture_here_app.activity.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.picture_here_app.R

/**
 * Activité de démarrage (invisible pour l'utilisateur) qui vérifie si un token d'authentification est présent sur l'appareil
 * Si oui on connecte l'utilisateur avec le token
 * Si non on demande à l'utilisateur de se connecter
 */
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token: String? = getSharedPreferences(getString(R.string.preference_app), MODE_PRIVATE).getString(getString(R.string.token), null)
        if(token == null){
            startActivity(Intent(this, LoginActivity::class.java))
        }else{
            startActivity(Intent(this, AppActivity::class.java))
        }
        finish()
    }
}