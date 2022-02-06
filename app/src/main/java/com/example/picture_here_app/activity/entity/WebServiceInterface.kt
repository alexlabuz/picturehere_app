package com.example.picture_here_app.activity.entity

import com.example.picture_here_app.activity.entity.login.Token
import com.example.picture_here_app.activity.entity.login.UserLogin
import com.example.picture_here_app.activity.entity.login.UserRegister
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.entity.user.User
import com.example.picture_here_app.activity.entity.user.Utilisateur
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface WebServiceInterface {
    @POST("api/user/login")
    fun login(@Body userLogin: UserLogin) : Call<Token>

    @POST("api/user/register")
    fun register(@Body userLogin: UserRegister) : Call<MessageResponse>

    @GET("api/profil/connected")
    fun connected(@Header("Authorization") authorization: String) : Call<User>
}