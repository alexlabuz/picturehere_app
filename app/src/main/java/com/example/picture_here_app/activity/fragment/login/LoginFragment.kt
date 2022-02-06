package com.example.picture_here_app.activity.fragment.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.picture_here_app.R
import com.example.picture_here_app.activity.entity.WebServiceInterface
import com.example.picture_here_app.activity.activity.AppActivity
import com.example.picture_here_app.activity.activity.LoginActivity
import com.example.picture_here_app.activity.entity.login.Token
import com.example.picture_here_app.activity.entity.login.UserLogin
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.singleton.RetrofitSingleton
import com.example.picture_here_app.databinding.FragmentLoginBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception

@SuppressLint("SetTextI18n")
class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private lateinit var loginActivity: LoginActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        loginActivity = activity as LoginActivity

        binding.btnLogin.setOnClickListener { loginBtnClick() }
        binding.editLoginUsername.doAfterTextChanged { onChangedText() }
        binding.editLoginPassword.doAfterTextChanged { onChangedText() }

        return binding.root
    }

    private fun loginBtnClick(){
        displayMessage("")
        loginActivity.load(true)
        val userLogin = UserLogin();
        userLogin.username = binding.editLoginUsername.text.toString()
        userLogin.password = binding.editLoginPassword.text.toString()

        val webServiceInterface = RetrofitSingleton.getRetrofit().create(WebServiceInterface::class.java)
        val callLogin = webServiceInterface.login(userLogin)

        callLogin.enqueue(object : retrofit2.Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                try{
                    if(response.isSuccessful){
                        val data : Token? = response.body()
                        loginActivity.preference.edit()
                            ?.putString(getString(R.string.token), data!!.token)
                            ?.apply()
                        openAppActivity()
                    }else{
                        val gson = Gson()
                        val type = object : TypeToken<MessageResponse>() {}.type
                        val errorBody: MessageResponse = gson.fromJson(response.errorBody()!!.charStream(), type)
                        displayMessage(errorBody.message, true)
                    }
                } catch (e: Exception) {
                    displayMessage("Une erreur s'est produite", true)
                } finally {
                    loginActivity.load(false)
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                displayMessage("Une erreur s'est produite", true)
                loginActivity.load(false)
            }
        })
    }

    fun openAppActivity(){
        val intent = Intent(activity, AppActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun onChangedText(){
        binding.btnLogin.isEnabled = (binding.editLoginUsername.text.toString().isNotBlank() &&
                binding.editLoginPassword.text.toString().isNotEmpty())
    }

     fun displayMessage(text: String, error: Boolean = false){
        if(text.isNotEmpty()){
            binding.textLoginIndication.text = text
            binding.textLoginIndication.visibility = View.VISIBLE
            binding.textLoginIndication.setTextColor(ResourcesCompat.getColor(
                loginActivity.resources,
                if(error) R.color.danger else R.color.good,
                null
            ))
        } else {
            binding.textLoginIndication.visibility = View.GONE
        }
    }
}