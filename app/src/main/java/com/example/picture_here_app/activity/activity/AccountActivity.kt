package com.example.picture_here_app.activity.activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.picture_here_app.R
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.entity.user.User
import com.example.picture_here_app.activity.service.MessageResponseGet
import com.example.picture_here_app.activity.service.RetrofitSingleton
import com.example.picture_here_app.databinding.ActivityAccountBinding
import com.example.picture_here_app.databinding.ActivityAppBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountBinding
    lateinit var preference: SharedPreferences

    lateinit var token: String
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        preference = getSharedPreferences(getString(R.string.preference_app), Context.MODE_PRIVATE)
        token = preference.getString(getString(R.string.token), null).toString()
        getUser()
        setContentView(binding.root)
    }

    private fun displayData(){
        binding.editTextPseudo.setText(user.utilisateur.profil.pseudo)
    }

    private fun getUser(){
        val callUser = RetrofitSingleton.getRetrofit().connected("Bearer ${token}")
        callUser.enqueue(object : Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                try{
                    if(response.isSuccessful){
                        val data : User? = response.body()
                        user = data!!
                        displayData()
                    }else{
                        val errorBody: MessageResponse = MessageResponseGet.getMessageResponse(response.errorBody()!!.charStream())
                        Snackbar.make(binding.layoutAccount, errorBody.message, Snackbar.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Snackbar.make(binding.layoutAccount, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
                } finally {
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Snackbar.make(binding.layoutAccount, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
            }

        })
    }
}