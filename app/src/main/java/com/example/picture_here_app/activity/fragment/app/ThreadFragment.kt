package com.example.picture_here_app.activity.fragment.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.picture_here_app.R
import com.example.picture_here_app.activity.activity.AppActivity
import com.example.picture_here_app.activity.entity.WebServiceInterface
import com.example.picture_here_app.activity.entity.post.Post
import com.example.picture_here_app.activity.entity.response.MessageResponse
import com.example.picture_here_app.activity.singleton.RetrofitSingleton
import com.example.picture_here_app.databinding.FragmentThreadBinding
import com.example.picture_here_app.databinding.PostCellBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Response
import java.lang.reflect.Array.get
import java.text.SimpleDateFormat
import java.util.*

class ThreadFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentThreadBinding
    private lateinit var appActivity: AppActivity

    private var listPost: List<Post> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentThreadBinding.inflate(inflater)
        appActivity = activity as AppActivity

        getPostThread()

        binding.threadList.setHasFixedSize(true)
        binding.threadList.layoutManager = LinearLayoutManager(activity)
        binding.threadList.adapter = ThreadAdapter(listPost)

        binding.threadSwipeLoad.setOnRefreshListener(this)
        binding.threadSwipeLoad.isRefreshing = true

        return binding.root
    }

    private fun getPostThread(){
        val token = appActivity.preference.getString(getString(R.string.token), "")
        val webServiceInterface = RetrofitSingleton.getRetrofit().create(WebServiceInterface::class.java)
        val callLogin = webServiceInterface.thread("Bearer $token")

        callLogin.enqueue(object : retrofit2.Callback<List<Post>>{
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                try{
                    if(response.isSuccessful){
                        val data : List<Post>? = response.body()
                        listPost = data!!
                        binding.threadList.adapter = ThreadAdapter(listPost)
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
        getPostThread()
    }
}

class PostHolder(postCellBinding: PostCellBinding) : RecyclerView.ViewHolder(postCellBinding.root){
    private val binding = postCellBinding
    val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy à HH:mm:ss", Locale("fr"))

    @SuppressLint("SimpleDateFormat")
    fun bindItems(post: Post){
        binding.postCellPseudoText.text = post.profil.pseudo
        binding.postCellMessageText.text = post.message
        binding.postCellDateText.text = simpleDateFormat.format(post.date)
        Picasso.get().load(RetrofitSingleton.baseUrl + post.linkImage).into(binding.postCellImage)
    }
}

class ThreadAdapter(postList: List<Post>) : RecyclerView.Adapter<PostHolder>(){
    private val dataSource = postList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val postCellBinding = PostCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(postCellBinding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bindItems(dataSource[position])
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

}