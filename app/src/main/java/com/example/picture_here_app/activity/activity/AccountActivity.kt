package com.example.picture_here_app.activity.activity

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.picture_here_app.R
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.entity.user.UpdateProfil
import com.example.picture_here_app.activity.entity.user.User
import com.example.picture_here_app.activity.service.MessageResponseGet
import com.example.picture_here_app.activity.service.RetrofitSingleton
import com.example.picture_here_app.databinding.ActivityAccountBinding
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

    fun clickUpdateProfil(view: View){
        displayMessage("")
        if(binding.editTextPassword.text.toString() != binding.editTextPasswordConfirm.text.toString()) {
            return displayMessage("Les 2 mots de passes ne corresponde pas", true)
        }
        val passwordSize = binding.editTextPassword.text.length
        if(passwordSize > 0 && passwordSize < 6){
            return displayMessage("Le mot de passe doit faire 6 caractères minimum", true)
        }

        val updateProfil = UpdateProfil()
        updateProfil.pseudo = binding.editTextPseudo.text.toString()
        updateProfil.password = binding.editTextPassword.text.toString()

        update(updateProfil)
    }

    fun clickDeleteProfil(view: View){
        AlertDialog.Builder(this)
            .setTitle("Clôturer votre compte")
            .setMessage("Vos informations ainsi que vos Posts serons supprimés !")
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Confirmer") { _, _ ->
                run {
                    delete(user.utilisateur.id)
                }
            }
            .show()
    }

    private fun displayMessage(text: String, error: Boolean = false){
        if(text.isNotEmpty()){
            binding.textIndication.text = text
            binding.textIndication.visibility = View.VISIBLE
            binding.textIndication.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    if(error) R.color.danger else R.color.good,
                    null
            ))
        } else {
            binding.textIndication.visibility = View.GONE
        }
    }

    private fun displayData(){
        binding.editTextPseudo.setText(user.utilisateur.profil.pseudo)
    }

    private fun getUser(){
        val callUser = RetrofitSingleton.getRetrofit().connected("Bearer $token")
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
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Snackbar.make(binding.layoutAccount, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun update(updateProfil: UpdateProfil){
        val callUpdate = RetrofitSingleton.getRetrofit().updateprofil("Bearer $token", updateProfil)
        callUpdate.enqueue(object : Callback<MessageResponse>{
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                try{
                    if(response.isSuccessful){
                        val data : MessageResponse? = response.body()
                        displayMessage(data!!.message)
                        displayData()
                    }else{
                        val errorBody: MessageResponse = MessageResponseGet.getMessageResponse(response.errorBody()!!.charStream())
                        displayMessage(errorBody.message, true)
                    }
                } catch (e: Exception) {
                    displayMessage("Une erreur est survenu", true)
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                displayMessage("Une erreur est survenu", true)
            }
        })
    }

    private fun delete(id: Int){
        val callDelete = RetrofitSingleton.getRetrofit().deleteProfil("Bearer $token", id)
        callDelete.enqueue(object : Callback<MessageResponse>{
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                try{
                    if(response.isSuccessful){
                        finish()
                    }else{
                        val errorBody: MessageResponse = MessageResponseGet.getMessageResponse(response.errorBody()!!.charStream())
                        Snackbar.make(binding.layoutAccount, errorBody.message, Snackbar.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Snackbar.make(binding.layoutAccount, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Snackbar.make(binding.layoutAccount, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
            }

        })


    }
}