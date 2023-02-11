package com.example.newsapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class AppViewModel(application: Application) :
    AndroidViewModel(application) {

    private val appRepository: AppRepository = AppRepository(application)
    val newsContentData: LiveData<ArrayList<NewsItems>>
        get() = appRepository.newsContents
    val response: LiveData<Response<String>>
        get() = appRepository.response

    fun getNews(q: CharSequence) {
        appRepository.getNews(q)
    }

}