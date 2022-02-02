package com.example.picture_here_app.activity

import com.example.picture_here_app.activity.entity.login.Token
import com.example.picture_here_app.activity.entity.login.UserLogin
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface WebServiceInterface {
    @POST("api/user/login")
    fun login(@Body userLogin: UserLogin) : Call<Token>

}