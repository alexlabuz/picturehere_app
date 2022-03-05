package com.example.picture_here_app.activity.entity.user

import androidx.annotation.Keep

@Keep
class Utilisateur {
    var id: Int = 0
    lateinit var username: String
    lateinit var roles: List<String>
    lateinit var profil: Profil


}