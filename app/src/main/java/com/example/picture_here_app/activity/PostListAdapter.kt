package com.example.picture_here_app.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.picture_here_app.activity.entity.post.Post
import com.example.picture_here_app.activity.service.RetrofitSingleton
import com.example.picture_here_app.databinding.PostCellBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class PostHolder(postCellBinding: PostCellBinding) : RecyclerView.ViewHolder(postCellBinding.root){
    private val binding = postCellBinding
    val deleteBtn: ImageButton = binding.postCellDeleteBtn
    val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy à HH:mm:ss", Locale("fr"))

    @SuppressLint("SimpleDateFormat")
    fun bindItems(post: Post, deleted: Boolean){
        binding.postCellPseudoText.text = post.profil.pseudo
        binding.postCellMessageText.text = post.message
        binding.postCellMessageText.visibility = if (post.message.isNotEmpty()) View.VISIBLE else View.GONE
        binding.postCellDateText.text = simpleDateFormat.format(post.date)
        if(!deleted) binding.postCellDeleteBtn.visibility = View.GONE
        Picasso.get().load(RetrofitSingleton.baseUrl + post.linkImage).into(binding.postCellImage)
    }
}

class PostListAdapter(postList: List<Post>, val deleted: Boolean = true, onClickBtnPost: OnClickBtnPost? = null) : RecyclerView.Adapter<PostHolder>(){
    private val dataSource = postList
    private val listener = onClickBtnPost

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val postCellBinding = PostCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(postCellBinding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bindItems(dataSource[position], deleted)
        holder.deleteBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                listener?.onClickDeletePost(dataSource[holder.adapterPosition])
            }
        })
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }
}