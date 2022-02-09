package com.example.picture_here_app.activity.service

import com.example.picture_here_app.activity.entity.WebServiceInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitSingleton {
    val baseUrl : String = "https://s4-8035.nuage-peda.fr/public/picturehere_back/public/"
    fun getRetrofit(): WebServiceInterface {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebServiceInterface::class.java)
    }
}