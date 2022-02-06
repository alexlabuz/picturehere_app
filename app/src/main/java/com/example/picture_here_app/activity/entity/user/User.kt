package com.example.picture_here_app.activity.entity.user

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class User : Serializable{
    @SerializedName("Utilisateur")
    lateinit var utilisateur: Utilisateur

}