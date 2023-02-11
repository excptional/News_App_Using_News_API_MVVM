package com.example.newsapp

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class AppRepository(private val application: Application) {

    private val responseLiveData = MutableLiveData<Response<String>>()
    val response: LiveData<Response<String>>
        get() = responseLiveData

    private val newsContentsLiveData = MutableLiveData<ArrayList<NewsItems>>()
    val newsContents: LiveData<ArrayList<NewsItems>>
        get() = newsContentsLiveData

    fun getNews(q: CharSequence) {
        val url = "https://newsapi.org/v2/everything?q=$q&from=2023-01-11&sortBy=publishedAt&apiKey=973d05c3a3a94267af61ce2c0366b69e"
        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null, {
                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<NewsItems>()
                for(i in 0 until  newsJsonArray.length()){
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = NewsItems(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("description"),
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("urlToImage"),
                        newsJsonObject.getString("url")
                    )
                    newsArray.add(news)
                }
                newsContentsLiveData.postValue(newsArray)
                responseLiveData.postValue(Response.Success())
            },
            {
                responseLiveData.postValue(Response.Failure(getErrorMassage(it)))
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }

        MySingleton.getInstance(application.applicationContext).addToRequestQueue(jsonRequest)
    }

    private fun getErrorMassage(e: Exception): String {
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 1)
    }

}