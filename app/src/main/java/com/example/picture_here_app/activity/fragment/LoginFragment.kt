package com.example.picture_here_app.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.picture_here_app.activity.WebServiceInterface
import com.example.picture_here_app.activity.entity.login.Token
import com.example.picture_here_app.activity.entity.login.UserLogin
import com.example.picture_here_app.activity.singleton.RetrofitSingleton
import com.example.picture_here_app.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater)

        binding.btnLogin.setOnClickListener { loginBtnClick() }

        return binding.root
    }

    fun loginBtnClick(){
        val userLogin = UserLogin();
        userLogin.username = binding.editLoginUsername.text.toString()
        userLogin.password = binding.editLoginPassword.text.toString()

        val webServiceInterface = RetrofitSingleton.getRetrofit().create(WebServiceInterface::class.java)
        val callLogin = webServiceInterface.login(userLogin)

        callLogin.enqueue(object : retrofit2.Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                val data = response.body()
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}