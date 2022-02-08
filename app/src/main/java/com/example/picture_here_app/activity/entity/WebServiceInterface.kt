package com.example.picture_here_app.activity.entity

import com.example.picture_here_app.activity.entity.login.Token
import com.example.picture_here_app.activity.entity.login.UserLogin
import com.example.picture_here_app.activity.entity.login.UserRegister
import com.example.picture_here_app.activity.entity.post.Post
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.entity.user.User
import retrofit2.Call
import retrofit2.http.*

interface WebServiceInterface {
    @POST("api/user/login")
    fun login(@Body userLogin: UserLogin) : Call<Token>

    @POST("api/user/register")
    fun register(@Body userLogin: UserRegister) : Call<MessageResponse>

    @GET("api/profil/connected")
    fun connected(@Header("Authorization") authorization: String) : Call<User>

    @GET("api/post/thread")
    fun thread(@Header("Authorization") authorization: String) : Call<List<Post>>

    @GET("api/post/user/{id}")
    fun postByUser(@Header("Authorization") authorization: String, @Path("id") id : Int) : Call<List<Post>>

    @GET("api/post/delete/{id}")
    fun deletePost(@Header("Authorization") authorization: String, @Path("id") id : Int) : Call<MessageResponse>

    @Multipart
    @POST("api/post/add")
    fun sendPost(
        @Header("Authorization") authorization: String,
        @Part("picture") picture: Any,
        @Part("post") post: Post
    ) : Call<MessageResponse>
}