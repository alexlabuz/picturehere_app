package com.example.picture_here_app.activity.entity.post

import android.support.annotation.Keep
import com.example.picture_here_app.activity.entity.user.Profil
import java.util.*

@Keep
class Post {
    var id: Int = 0
    lateinit var message: String
    lateinit var linkImage: String
    lateinit var date: Date
    lateinit var profil: Profil
}