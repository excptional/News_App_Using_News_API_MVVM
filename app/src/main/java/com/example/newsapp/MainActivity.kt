package com.example.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private val appViewModel: AppViewModel
    get() = ViewModelProvider(this)[AppViewModel::class.java]
    private val refreshLayout: SwipeRefreshLayout
    get() = findViewById(R.id.refresh_layout)
    private val loadingShimmer: ShimmerFrameLayout
    get() = findViewById(R.id.loading_shimmer)
    private val recyclerview: RecyclerView
    get() = findViewById(R.id.recyclerview)
    private val whiteLayout: LinearLayout
    get() = findViewById(R.id.whiteLayout)
    private val loader: LottieAnimationView
    get() = findViewById(R.id.progressbar)
    private val searchEditText: TextInputEditText
    get() = findViewById(R.id.search_edittext)
    private val searchBtn: ImageButton
    get() = findViewById(R.id.search_btn)
    private val searchView: LinearLayout
    get() = findViewById(R.id.searchview)
    private val errorText: TextView
    get() = findViewById(R.id.error_text)
    private lateinit var newsAdapter: NewsAdapter
    private var query: CharSequence = "india"
    private var newsItemsArray = arrayListOf<NewsItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadingShimmer.startShimmer()
        loadingShimmer.visibility = View.VISIBLE
        recyclerview.visibility = View.GONE

        newsAdapter = NewsAdapter(this, newsItemsArray)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.setHasFixedSize(true)
        recyclerview.setItemViewCacheSize(20)
        recyclerview.adapter = newsAdapter

        getData("india")

        refreshLayout.setOnRefreshListener {
            loadingShimmer.startShimmer()
            loadingShimmer.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
            errorText.visibility = View.GONE
            searchView.visibility = View.VISIBLE
            getData(query)
        }

        searchBtn.setOnClickListener {
            if(searchEditText.text.toString().isNotEmpty()) {
                whiteLayout.visibility = View.VISIBLE
                loader.visibility = View.VISIBLE
                query = searchEditText.text.toString()
                getData(query)
                searchEditText.text = null
            }
        }
    }

    private fun getData(q: CharSequence) {
        appViewModel.getNews(q)
        appViewModel.response.observe(this@MainActivity) {
            when(it) {
                is Response.Success -> {
                    appViewModel.newsContentData.observe(this@MainActivity) {
                        if(it.isNotEmpty()) fetchData(it)
                        else Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
                is Response.Failure -> {
                    Toast.makeText(this@MainActivity, it.errorMassage, Toast.LENGTH_SHORT).show()
                    searchView.visibility = View.GONE
                    recyclerview.visibility = View.GONE
                    loadingShimmer.visibility = View.GONE
                    whiteLayout.visibility = View.GONE
                    loader.visibility = View.GONE
                    refreshLayout.isRefreshing = false
                    errorText.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun fetchData(list: ArrayList<NewsItems>) {
        newsItemsArray = arrayListOf()
        for (i in list) {
            newsItemsArray.add(i)
        }
        newsAdapter.updateNews(newsItemsArray)
        loadingShimmer.clearAnimation()
        loadingShimmer.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE
        whiteLayout.visibility = View.GONE
        loader.visibility = View.GONE
        refreshLayout.isRefreshing = false
    }

}