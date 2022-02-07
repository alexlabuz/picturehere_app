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
import com.example.picture_here_app.activity.PostListAdapter
import com.example.picture_here_app.activity.activity.AppActivity
import com.example.picture_here_app.activity.entity.WebServiceInterface
import com.example.picture_here_app.activity.entity.post.Post
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.singleton.RetrofitSingleton
import com.example.picture_here_app.databinding.FragmentThreadBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        val webServiceInterface = RetrofitSingleton.getRetrofit().create(WebServiceInterface::class.java)
        val callLogin = webServiceInterface.thread("Bearer ${appActivity.token}")

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
                        val gson = Gson()
                        val type = object : TypeToken<MessageResponse>() {}.type
                        val errorBody: MessageResponse = gson.fromJson(response.errorBody()!!.charStream(), type)
                        Toast.makeText(activity, errorBody.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, "Une erreur est survenu", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.threadSwipeLoad.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(activity, "Une erreur est survenu", Toast.LENGTH_SHORT).show()
                binding.threadSwipeLoad.isRefreshing = false
            }
        })
    }

    override fun onRefresh() {
        getPost()
    }
}