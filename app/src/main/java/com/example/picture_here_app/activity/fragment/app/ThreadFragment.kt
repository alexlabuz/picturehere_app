package com.example.picture_here_app.activity.fragment.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.picture_here_app.activity.PostListAdapter
import com.example.picture_here_app.activity.activity.AppActivity
import com.example.picture_here_app.activity.entity.post.Post
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.service.RetrofitSingleton
import com.example.picture_here_app.activity.service.MessageResponseGet
import com.example.picture_here_app.databinding.FragmentThreadBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Response

class ThreadFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentThreadBinding
    private lateinit var appActivity: AppActivity

    private var listPost: MutableList<Post> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentThreadBinding.inflate(inflater)
        appActivity = activity as AppActivity

        binding.threadList.setHasFixedSize(true)
        binding.threadList.layoutManager = LinearLayoutManager(activity)
        binding.threadList.adapter = PostListAdapter(listPost, false)

        getPost()

        binding.threadSwipeLoad.setOnRefreshListener(this)
        binding.threadSwipeLoad.isRefreshing = true

        return binding.root
    }

    private fun getPost(){
        val callLogin = RetrofitSingleton.getRetrofit().thread("Bearer ${appActivity.token}")
        callLogin.enqueue(object : retrofit2.Callback<List<Post>>{
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                try{
                    if(response.isSuccessful){
                        val data : List<Post>? = response.body()
                        listPost.clear()
                        listPost.addAll(data as MutableList<Post>)
                        binding.threadList.adapter?.notifyDataSetChanged()
                    }else{
                        val errorBody: MessageResponse = MessageResponseGet.getMessageResponse(response.errorBody()!!.charStream())
                        Toast.makeText(activity, errorBody.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Snackbar.make(appActivity.binding.frameLayoutApp, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
                } finally {
                    binding.threadSwipeLoad.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Snackbar.make(appActivity.binding.frameLayoutApp, "Une erreur est survenu", Snackbar.LENGTH_SHORT).show()
                binding.threadSwipeLoad.isRefreshing = false
            }
        })
    }

    override fun onRefresh() {
        getPost()
    }
}