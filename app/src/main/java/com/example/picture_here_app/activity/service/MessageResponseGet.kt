package com.example.picture_here_app.activity.service

import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader

object MessageResponseGet {

    fun getMessageResponse(response: Reader) : MessageResponse{
        val gson = Gson()
        val type = object : TypeToken<MessageResponse>() {}.type
        return gson.fromJson(response, type)
    }

}