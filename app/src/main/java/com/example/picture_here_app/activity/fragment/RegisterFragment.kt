package com.example.picture_here_app.activity.fragment

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.picture_here_app.R
import com.example.picture_here_app.activity.WebServiceInterface
import com.example.picture_here_app.activity.activity.LoginActivity
import com.example.picture_here_app.activity.entity.register.UserRegister
import com.example.picture_here_app.activity.singleton.RetrofitSingleton
import com.example.picture_here_app.databinding.FragmentRegisterBinding
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

class RegisterFragment : Fragment(){
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterBinding.inflate(inflater)

        binding.btnRegister.setOnClickListener { registerBtnClick() }
        binding.editRegisterPseudo.doAfterTextChanged { onChangedText() }
        binding.editRegisterUsername.doAfterTextChanged { onChangedText() }
        binding.editRegisterPassword.doAfterTextChanged { onChangedText() }
        return binding.root
    }

    private fun registerBtnClick(){
        errorMessage("")
        if(binding.editRegisterPassword.text.toString() != binding.editRegisterPasswordConfirm.text.toString()) {
            return errorMessage("Les 2 mot de passe ne corresponde pas")
        }
        if(binding.editRegisterPassword.text.length < 8){
            return errorMessage("Le mot de passe doit faire 8 caractère minimum")
        }

        val userRegister = UserRegister()
        userRegister.username = binding.editRegisterUsername.text.toString()
        userRegister.pseudo = binding.editRegisterPseudo.text.toString()
        userRegister.password = binding.editRegisterPseudo.text.toString()

        val webServiceInterface = RetrofitSingleton.getRetrofit().create(WebServiceInterface::class.java)
        val callRegister = webServiceInterface.register(userRegister)

        callRegister.enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                val data = response.body()
                if(data != null) {
                }else{
                    errorMessage("Une erreur s'est produite")
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun onChangedText(){
        binding.btnRegister.isEnabled = (binding.editRegisterUsername.text.toString().isNotBlank() &&
                binding.editRegisterPseudo.text.toString().isNotEmpty() &&
                binding.editRegisterPassword.text.toString().isNotEmpty())
    }

    private fun errorMessage(text: String){
        if(text.isNotEmpty()){
            binding.textRegisterIndication.text = text
            binding.textRegisterIndication.visibility = View.VISIBLE
        } else {
            binding.textRegisterIndication.visibility = View.GONE
        }
    }
}