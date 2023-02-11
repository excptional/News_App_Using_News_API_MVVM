package com.example.newsapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.*
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class NewsAdapter(private val context: Context, private val NewsItems: ArrayList<NewsItems>) :
RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_items, parent,false)
        return NewsViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = NewsItems[position]
        holder.title.text = currentItem.title
        holder.description.text = currentItem.description
        if(currentItem.author.toString() == "null") holder.author.visibility = View.GONE
        else holder.author.text = "- " + currentItem.author
        if(currentItem.imageUrl.toString() == "null") holder.contentImage.visibility = View.GONE
        else Glide.with(holder.itemView).load(currentItem.imageUrl).into(holder.contentImage)

        holder.cardView.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )
            builder.addDefaultShareMenuItem()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(currentItem.contentUrl))
        }

    }

    override fun getItemCount(): Int {
        return NewsItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNews(updateNewsItems: ArrayList<NewsItems>) {
        NewsItems.clear()
        NewsItems.addAll(updateNewsItems)
        notifyDataSetChanged()
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val contentImage: ImageView = itemView.findViewById(R.id.content_image)
        val author: TextView = itemView.findViewById(R.id.author)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }
}