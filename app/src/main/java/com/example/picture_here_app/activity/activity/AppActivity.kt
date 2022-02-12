package com.example.picture_here_app.activity.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.picture_here_app.R
import com.example.picture_here_app.activity.entity.post.Post
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.entity.user.User
import com.example.picture_here_app.activity.fragment.app.ProfilFragment
import com.example.picture_here_app.activity.fragment.app.ThreadFragment
import com.example.picture_here_app.activity.service.GetFile
import com.example.picture_here_app.activity.service.MessageResponseGet
import com.example.picture_here_app.activity.service.RetrofitSingleton
import com.example.picture_here_app.databinding.ActivityAppBinding
import com.example.picture_here_app.databinding.DialogSendPostBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityAppBinding
    lateinit var preference: SharedPreferences
    val threadFragment = ThreadFragment()
    val profilFragment = ProfilFragment()
    lateinit var token: String
    var appStart = true

    lateinit var user: User

    private val pictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) openDialogSendPost(result.data?.data!!)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)

        load(true)
        preference = getSharedPreferences(getString(R.string.preference_app), Context.MODE_PRIVATE)
        binding.bottomNavigationApp.setOnNavigationItemSelectedListener(this)
        token = preference.getString(getString(R.string.token), null).toString()

        getData()
        setContentView(binding.root)
    }

    fun logout(expireToken: Boolean = false){
        preference.edit().remove(getString(R.string.token)).apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(getString(R.string.expired_token), expireToken)
        startActivity(intent)
        finish()
    }

    fun load(enabled: Boolean){
        binding.load.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    fun openFilePicker(view: View? = null){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            return requestPermissions(listOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE).toTypedArray(), 0)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        pictureLauncher.launch(intent)
    }

    private fun openDialogSendPost(uri: Uri){
        val post = Post()
        val dialogSendPostBinding: DialogSendPostBinding = DialogSendPostBinding.inflate(layoutInflater)
        AlertDialog.Builder(this)
            .setTitle("Publier une photo")
            .setView(dialogSendPostBinding.root)
            .setPositiveButton("Publier") { _, _ ->
                run {
                    post.message = dialogSendPostBinding.editMessagePost.text.toString()
                    sendPost(uri, post)
                }
            }
            .setNegativeButton("Retour", null)
            .show()

        Picasso.get().load(uri).into(dialogSendPostBinding.imagePost)
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

    //region Request
    private fun sendPost(uri: Uri, post: Post){
        // Prepare file
        val file = GetFile.getFile(uri, this)
        val requestFile = RequestBody.create(MediaType.parse(contentResolver.getType(uri)!!), file)
        val postPicture = MultipartBody.Part.createFormData("picture", file.name, requestFile)

        load(true)
        val callLogin = RetrofitSingleton.getRetrofit().sendPost("Bearer $token", postPicture, post)

        callLogin.enqueue(object : Callback<MessageResponse>{
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                try{
                    if(response.isSuccessful){
                        val data : MessageResponse? = response.body()
                        Snackbar.make(binding.frameLayoutApp, data!!.message, Snackbar.LENGTH_SHORT).show()
                    }else{
                        val errorBody: MessageResponse = MessageResponseGet.getMessageResponse(response.errorBody()!!.charStream())
                        Snackbar.make(binding.frameLayoutApp, errorBody.message, Snackbar.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Snackbar.make(binding.frameLayoutApp, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
                } finally {
                    load(false)
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Snackbar.make(binding.frameLayoutApp, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
                load(false)
            }
        })
    }

    fun getData(){
        val callLogin = RetrofitSingleton.getRetrofit().connected("Bearer $token")
        callLogin.enqueue(object : Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                try{
                    if(response.isSuccessful){
                        val data : User? = response.body()
                        user = data!!
                        if (appStart) loadFragment(threadFragment)
                    }else{
                        val errorBody: MessageResponse = MessageResponseGet.getMessageResponse(response.errorBody()!!.charStream())
                        if(errorBody.message == "Expired JWT Token" || errorBody.message == "Invalid credentials."){
                            logout(true)
                        }else{
                            Snackbar.make(binding.frameLayoutApp, errorBody.message, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Snackbar.make(binding.frameLayoutApp, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
                } finally {
                    load(false)
                    appStart = false
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Snackbar.make(binding.frameLayoutApp, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
                load(false)
                appStart = false
            }
        })
    }
    //endregion

    // region override
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            0 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openFilePicker(null)
                }else{
                    Snackbar.make(binding.frameLayoutApp, "Permission de stockage non autorisé", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
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

    override fun onResume() {
        super.onResume()
        getData()
    }
    // endregion
}