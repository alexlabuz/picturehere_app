package com.example.picture_here_app.activity.entity.user

import android.support.annotation.Keep
import com.example.picture_here_app.activity.entity.post.Post
import java.util.*

@Keep
class Profil {
    lateinit var pseudo: String
    lateinit var posts: List<Post>
    lateinit var dateInscription: Date
    lateinit var followed: List<Profil>
    lateinit var followers: List<Profil>

}