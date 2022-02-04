package com.example.picture_here_app.activity

import com.example.picture_here_app.activity.entity.login.Token
import com.example.picture_here_app.activity.entity.login.UserLogin
import com.example.picture_here_app.activity.entity.register.UserRegister
import com.example.picture_here_app.activity.entity.response.MessageResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface WebServiceInterface {
    @POST("api/user/login")
    fun login(@Body userLogin: UserLogin) : Call<Token>

    @POST("api/user/register")
    fun register(@Body userLogin: UserRegister) : Call<MessageResponse>

}