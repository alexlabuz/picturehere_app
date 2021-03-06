package com.example.picture_here_app.activity.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.picture_here_app.R
import com.example.picture_here_app.activity.activity.LoginActivity
import com.example.picture_here_app.activity.entity.login.UserRegister
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.service.MessageResponseGet
import com.example.picture_here_app.activity.service.RetrofitSingleton
import com.example.picture_here_app.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Response

class RegisterFragment : Fragment(){
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var loginActivity: LoginActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        loginActivity = activity as LoginActivity

        binding.btnRegister.setOnClickListener { registerBtnClick() }
        binding.editRegisterPseudo.doAfterTextChanged { onChangedText() }
        binding.editRegisterUsername.doAfterTextChanged { onChangedText() }
        binding.editRegisterPassword.doAfterTextChanged { onChangedText() }
        return binding.root
    }

    private fun registerBtnClick(){
        displayMessage("")
        if(binding.editRegisterPassword.text.toString() != binding.editRegisterPasswordConfirm.text.toString()) {
            return displayMessage("Les 2 mots de passes ne corresponde pas", true)
        }
        if(binding.editRegisterPassword.text.length < 6){
            return displayMessage("Le mot de passe doit faire 6 caractères minimum", true)
        }

        val userRegister = UserRegister()
        userRegister.username = binding.editRegisterUsername.text.toString()
        userRegister.pseudo = binding.editRegisterPseudo.text.toString()
        userRegister.password = binding.editRegisterPassword.text.toString()

        register(userRegister)
    }

    private fun register(userRegister: UserRegister){
        loginActivity.load(true)

        val callRegister = RetrofitSingleton.getRetrofit().register(userRegister)
        callRegister.enqueue(object : retrofit2.Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                try{
                    if(response.isSuccessful){
                        val message: MessageResponse? = response.body()
                        loginActivity.loadFragment(loginActivity.loginFragment)
                        emptyEditText()
                        Snackbar
                            .make(loginActivity.binding.frameLayoutLogin, message?.message ?: "Inscription terminée", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ResourcesCompat.getColor(loginActivity.resources, R.color.good, null))
                            .show()
                    }else{
                        val errorBody: MessageResponse = MessageResponseGet.getMessageResponse(response.errorBody()!!.charStream())
                        displayMessage(errorBody.message, true)
                    }
                } catch (e: Exception) {
                    displayMessage("Une erreur s'est produite", true)
                } finally {
                    loginActivity.load(false)
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                displayMessage("Une erreur s'est produite", true)
                loginActivity.load(false)
            }
        })
    }

    private fun onChangedText(){
        binding.btnRegister.isEnabled = (binding.editRegisterUsername.text.toString().isNotBlank() &&
                binding.editRegisterPseudo.text.toString().isNotEmpty() &&
                binding.editRegisterPassword.text.toString().isNotEmpty())
    }

    private fun emptyEditText(){
        binding.editRegisterPassword.setText("")
        binding.editRegisterUsername.setText("")
        binding.editRegisterPasswordConfirm.setText("")
        binding.editRegisterPseudo.setText("")
    }

    fun displayMessage(text: String, error: Boolean = false){
        if(text.isNotEmpty()){
            binding.textRegisterIndication.text = text
            binding.textRegisterIndication.visibility = View.VISIBLE
            binding.textRegisterIndication.setTextColor(ResourcesCompat.getColor(
                loginActivity.resources,
                if(error) R.color.danger else R.color.good,
                null
            ))
        } else {
            binding.textRegisterIndication.visibility = View.GONE
        }
    }
}