package com.example.picture_here_app.activity.fragment.app

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.picture_here_app.activity.OnClickBtnPost
import com.example.picture_here_app.activity.PostListAdapter
import com.example.picture_here_app.activity.activity.AccountActivity
import com.example.picture_here_app.activity.activity.AppActivity
import com.example.picture_here_app.activity.entity.post.Post
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.service.MessageResponseGet
import com.example.picture_here_app.activity.service.RetrofitSingleton
import com.example.picture_here_app.databinding.FragmentProfilBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ProfilFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, OnClickBtnPost {
    private lateinit var binding: FragmentProfilBinding
    private lateinit var appActivity: AppActivity

    var listPost: MutableList<Post> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfilBinding.inflate(inflater)
        appActivity = activity as AppActivity

        binding.profilBtnLogout.setOnClickListener { clickButtonLogout() }
        binding.profilBtnAccount.setOnClickListener { openAccountSetting() }

        onRefresh()

        binding.profilList.setHasFixedSize(true)
        binding.profilList.layoutManager = LinearLayoutManager(activity)
        binding.profilList.adapter = PostListAdapter(listPost, onClickBtnPost = this)

        binding.profilSwipeLoad.setOnRefreshListener(this)
        binding.profilSwipeLoad.isRefreshing = true
        return binding.root
    }

    private fun clickButtonLogout(){
        AlertDialog.Builder(activity)
            .setTitle("Se deconnecter ?")
            .setMessage("Voulez-vous vous déconnecter ?")
            .setPositiveButton("Se déconecter") { _, _ -> appActivity.logout() }
            .setNegativeButton("Annuler", null)
            .show()
    }

    @SuppressLint("SetTextI18n")
    fun getDataProfil(){
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("fr"))
        binding.profilTextPseudo.text = appActivity.user.utilisateur.profil.pseudo
        binding.profilDateDateRegister.text = "Inscrit le "+simpleDateFormat.format(appActivity.user.utilisateur.profil.dateInscription)
    }

    private fun getPost(){
        val callLogin = RetrofitSingleton.getRetrofit().postByUser("Bearer ${appActivity.token}", appActivity.user.utilisateur.id)
        callLogin.enqueue(object : retrofit2.Callback<List<Post>>{
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                try{
                    if(response.isSuccessful){
                        val data : List<Post>? = response.body()
                        listPost.clear()
                        listPost.addAll(data as MutableList<Post>)
                        binding.profilList.adapter?.notifyDataSetChanged()
                    }else{
                        val errorBody: MessageResponse = MessageResponseGet.getMessageResponse(response.errorBody()!!.charStream())
                        Toast.makeText(activity, errorBody.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Snackbar.make(appActivity.binding.frameLayoutApp, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
                } finally {
                    binding.profilSwipeLoad.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Snackbar.make(appActivity.binding.frameLayoutApp, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
                binding.profilSwipeLoad.isRefreshing = false
            }
        })
    }

    private fun openAccountSetting(){
        startActivity(Intent(activity, AccountActivity::class.java))
    }

    override fun onRefresh() {
        try {
            getDataProfil()
            getPost()
        }catch (e: Exception){}
    }


    override fun onClickDeletePost(post: Post) {
        AlertDialog.Builder(activity)
            .setTitle("Supprimer ce post ?")
            .setMessage("Voulez-vous vraiment faire ceci ?")
            .setPositiveButton("Oui") { _, _ -> deletePost(post) }
            .setNegativeButton("Non", null)
            .show()
    }


    fun deletePost(post: Post){
        Snackbar.make(appActivity.binding.frameLayoutApp, "Suppression en cours ...", Snackbar.LENGTH_SHORT).show()

        val callLogin = RetrofitSingleton.getRetrofit().deletePost("Bearer ${appActivity.token}", post.id)
        callLogin.enqueue(object : retrofit2.Callback<MessageResponse>{
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                try{
                    if(response.isSuccessful){
                        val data : MessageResponse? = response.body()
                        val i = listPost.indexOf(post)
                        listPost.remove(post)
                        binding.profilList.adapter?.notifyItemRemoved(i)
                        Snackbar.make(appActivity.binding.frameLayoutApp, data!!.message, Snackbar.LENGTH_SHORT).show()
                    }else{
                        val errorBody: MessageResponse = MessageResponseGet.getMessageResponse(response.errorBody()!!.charStream())
                        Toast.makeText(activity, errorBody.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, "Une erreur est survenu", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Toast.makeText(activity, "Une erreur est survenu", Toast.LENGTH_SHORT).show()
            }
        })
    }

}