package com.example.picture_here_app.activity.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.picture_here_app.R
import com.example.picture_here_app.activity.WebServiceInterface
import com.example.picture_here_app.activity.activity.LoginActivity
import com.example.picture_here_app.activity.entity.login.Token
import com.example.picture_here_app.activity.entity.login.UserLogin
import com.example.picture_here_app.activity.singleton.RetrofitSingleton
import com.example.picture_here_app.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

@SuppressLint("SetTextI18n")
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater)

        binding.btnLogin.setOnClickListener { loginBtnClick() }

        return binding.root
    }

    private fun loginBtnClick(){
        errorMessage("")
        val userLogin = UserLogin();
        userLogin.username = binding.editLoginUsername.text.toString()
        userLogin.password = binding.editLoginPassword.text.toString()

        val webServiceInterface = RetrofitSingleton.getRetrofit().create(WebServiceInterface::class.java)
        val callLogin = webServiceInterface.login(userLogin)

        callLogin.enqueue(object : retrofit2.Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                val data : Token? = response.body()
                if(data != null) {
                    (activity as LoginActivity).preference.edit()
                        ?.putString(getString(R.string.token), data.token)
                        ?.apply()
                }else{
                    errorMessage("Les identifiants sont incorrect")
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun errorMessage(text: String){
        if(text.isNotEmpty()){
            binding.textLoginIndication.text = text
            binding.textLoginIndication.visibility = View.VISIBLE
        } else {
            binding.textLoginIndication.visibility = View.GONE
        }
    }
}