package com.example.picture_here_app.activity.fragment.app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.picture_here_app.R
import com.example.picture_here_app.activity.OnClickBtnPost
import com.example.picture_here_app.activity.PostListAdapter
import com.example.picture_here_app.activity.activity.AppActivity
import com.example.picture_here_app.activity.entity.WebServiceInterface
import com.example.picture_here_app.activity.entity.post.Post
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.singleton.RetrofitSingleton
import com.example.picture_here_app.databinding.FragmentProfilBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ProfilFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, OnClickBtnPost {
    private lateinit var binding: FragmentProfilBinding
    private lateinit var appActivity: AppActivity
    val fragment = this

    var listPost: MutableList<Post> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfilBinding.inflate(inflater)
        appActivity = activity as AppActivity

        binding.profilBtnLogout.setOnClickListener {
            appActivity.logout()
        }

        getDataProfil()
        getPost()

        binding.profilList.setHasFixedSize(true)
        binding.profilList.layoutManager = LinearLayoutManager(activity)
        binding.profilList.adapter = PostListAdapter(listPost, onClickBtnPost = this)

        binding.profilSwipeLoad.setOnRefreshListener(this)
        binding.profilSwipeLoad.isRefreshing = true
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    fun getDataProfil(){
        appActivity.getData()
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("fr"))
        binding.profilTextPseudo.text = appActivity.user.utilisateur.profil.pseudo
        binding.profilDateDateRegister.text = "Inscrit le "+simpleDateFormat.format(appActivity.user.utilisateur.profil.dateInscription)
    }

    private fun getPost(){
        val webServiceInterface = RetrofitSingleton.getRetrofit().create(WebServiceInterface::class.java)
        val callLogin = webServiceInterface.postByUser("Bearer ${appActivity.token}", appActivity.user.utilisateur.id)

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
                        val gson = Gson()
                        val type = object : TypeToken<MessageResponse>() {}.type
                        val errorBody: MessageResponse = gson.fromJson(response.errorBody()!!.charStream(), type)
                        Toast.makeText(activity, errorBody.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, "Une erreur est survenu", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.profilSwipeLoad.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(activity, "Une erreur est survenu", Toast.LENGTH_SHORT).show()
                binding.profilSwipeLoad.isRefreshing = false
            }
        })
    }

    override fun onRefresh() {
        getPost()
        getDataProfil()
    }

    override fun onClickDeletePost(post: Post) {
        Toast.makeText(activity, "Suppression en cours ...", Toast.LENGTH_SHORT).show()
        val webServiceInterface = RetrofitSingleton.getRetrofit().create(WebServiceInterface::class.java)
        val callLogin = webServiceInterface.deletePost("Bearer ${appActivity.token}", post.id)

        callLogin.enqueue(object : retrofit2.Callback<MessageResponse>{
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                try{
                    if(response.isSuccessful){
                        val data : MessageResponse? = response.body()
                        val i = listPost.indexOf(post)
                        listPost.remove(post)
                        binding.profilList.adapter?.notifyItemRemoved(i)
                        Toast.makeText(activity, data!!.message, Toast.LENGTH_SHORT).show()
                    }else{
                        val gson = Gson()
                        val type = object : TypeToken<MessageResponse>() {}.type
                        val errorBody: MessageResponse = gson.fromJson(response.errorBody()!!.charStream(), type)
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