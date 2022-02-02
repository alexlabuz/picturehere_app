package com.example.picture_here_app.activity.singleton

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitSingleton {
    val baseUrl : String = "https://s4-8035.nuage-peda.fr/public/picturehere_back/public/"
    fun getRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}