package com.example.picture_here_app.activity.entity.user

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
class User : Serializable{
    @SerializedName("Utilisateur")
    lateinit var utilisateur: Utilisateur

}