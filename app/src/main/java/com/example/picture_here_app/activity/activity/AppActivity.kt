package com.example.picture_here_app.activity.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.picture_here_app.R
import com.example.picture_here_app.activity.entity.WebServiceInterface
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.entity.user.User
import com.example.picture_here_app.activity.fragment.app.ProfilFragment
import com.example.picture_here_app.activity.fragment.app.ThreadFragment
import com.example.picture_here_app.activity.singleton.RetrofitSingleton
import com.example.picture_here_app.databinding.ActivityAppBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception


class AppActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityAppBinding
    lateinit var preference: SharedPreferences
    val threadFragment = ThreadFragment()
    val profilFragment = ProfilFragment()
    lateinit var token: String

    lateinit var user: User

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        load(true)
        preference = getSharedPreferences(getString(R.string.preference_app), Context.MODE_PRIVATE)
        binding.bottomNavigationApp.setOnNavigationItemSelectedListener(this)
        token = preference.getString(getString(R.string.token), null).toString()
        loadFragment(threadFragment)

        getData()
        setContentView(binding.root)
    }

    fun getData(){
        val webServiceInterface = RetrofitSingleton.getRetrofit().create(WebServiceInterface::class.java)
        val callLogin = webServiceInterface.connected("Bearer $token")

        callLogin.enqueue(object : retrofit2.Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                try{
                    if(response.isSuccessful){
                        val data : User? = response.body()
                        user = data!!
                    }else{
                        val gson = Gson()
                        val type = object : TypeToken<MessageResponse>() {}.type
                        val errorBody: MessageResponse = gson.fromJson(response.errorBody()!!.charStream(), type)
                        if(errorBody.message == "Expired JWT Token"){
                            Toast.makeText(this@AppActivity, "Vous avez été déconnecté", Toast.LENGTH_SHORT).show()
                            logout()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@AppActivity, "Une erreur est survenu", Toast.LENGTH_SHORT).show()
                } finally {
                    load(false)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@AppActivity, "Une erreur est survenu", Toast.LENGTH_SHORT).show()
                load(false)
            }
        })
    }

    fun logout(){
        preference.edit().remove(getString(R.string.token)).apply()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun load(enabled: Boolean){
        binding.load.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    private fun loadFragment(fragment: Fragment?) : Boolean{
        if(fragment != null){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout_app, fragment)
                .setReorderingAllowed(true)
                .commit()
            return true
        }
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_thread) {
            return loadFragment(threadFragment)
        }
        if (item.itemId == R.id.action_profil) {
            return loadFragment(profilFragment)
        }
        return false
    }
}