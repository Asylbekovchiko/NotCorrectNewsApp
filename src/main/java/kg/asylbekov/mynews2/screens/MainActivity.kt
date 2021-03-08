package kg.asylbekov.mynews2.screens

import android.app.SearchManager
import android.content.Context
import io.reactivex.Observable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.disposables.CompositeDisposable
import kg.asylbekov.mynews2.R
import kg.asylbekov.mynews2.adapters.RecyclerAdapter
import kg.asylbekov.mynews2.dataClasses.NewsData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kg.asylbekov.mynews2.dataClasses.pointTopRaiting
import kg.asylbekov.mynews2.databinding.ActivityMainBinding
import kg.asylbekov.mynews2.interfaces.pointTopRaitingInterface
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    //    private lateinit var binding : ActivityMainBinding
    private val base_url = "https://newsapi.org/v2/"
    private lateinit var topPoint: pointTopRaitingInterface
    private val BASE_URL by lazy { "https://newsapi.org/v2/" }
    private lateinit var newsApi: String

    private lateinit var adapter: RecyclerAdapter
    private lateinit var newsList: ArrayList<NewsData>
    lateinit var topObserver: Observable<pointTopRaiting>
    private lateinit var compositeDisposable: CompositeDisposable
    lateinit var userKeywordInput: String
    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        newsApi = "7c08c9dfe0ce4e7ab2d9f3ba6d59b69c"
        binding.swipeRefresh.setOnRefreshListener(this)
        binding.swipeRefresh.setColorSchemeResources(R.color.colorAccent)
        userKeywordInput = ""
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        //запрос
        val retrofit: Retrofit = generateRetrofitBuilder()

        topPoint = retrofit.create(pointTopRaitingInterface::class.java)
        newsList = ArrayList()
        adapter = RecyclerAdapter(newsList)

        compositeDisposable = CompositeDisposable()
        binding.apply {
            recycler.layoutManager = LinearLayoutManager(this@MainActivity)
            recycler.adapter = adapter
        }

    }

    override fun onStart() {
        super.onStart()
        checkUserKeywordInput()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onRefresh() {
        checkUserKeywordInput()
    }


    private fun checkUserKeywordInput() {
        if (userKeywordInput.isEmpty()) {
            queryTopHeadlines()
        } else {
            getKeyWordQuery(userKeywordInput)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.menu, menu)
            setUpSearchMenuItem(menu)
        }
        return true
    }

    private fun setUpSearchMenuItem(menu: Menu) {
        val searchManager: SearchManager =
            (getSystemService(Context.SEARCH_SERVICE)) as SearchManager
        val searchView: SearchView = ((menu.findItem(R.id.action_search)?.actionView)) as SearchView
        val searchMenuItem: MenuItem = menu.findItem(R.id.action_search)

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Type any keyword to search..."
        searchView.setOnQueryTextListener(onQueryTextListenerCallback())
//        searchMenuItem.icon.setVisible(false, false)
    }

    //Gets immediately triggered when user clicks on search icon and enters something
    private fun onQueryTextListenerCallback(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(userInput: String?): Boolean {
                return checkQueryText(userInput)
            }

            override fun onQueryTextChange(userInput: String?): Boolean {
                return checkQueryText(userInput)
            }
        }
    }

    fun checkQueryText(userInput: String?): Boolean {
        if (userInput != null && userInput.length > 1) {
            userKeywordInput = userInput
            getKeyWordQuery(userInput)
        } else if (userInput != null || userInput == "") {
            userKeywordInput = ""
            queryTopHeadlines()
        }
        return false
    }


    fun getKeyWordQuery(userKeywordInput: String) {
        swipe_refresh.isRefreshing = true
        if (userKeywordInput != null && userKeywordInput.isNotEmpty()) {
            topObserver = topPoint.getUserSearchInput(newsApi, userKeywordInput)
            subscribeObservableOfArticle()
        } else {
            queryTopHeadlines()
        }


    }


    private fun queryTopHeadlines() {
        swipe_refresh.isRefreshing = true
        topObserver = topPoint.getTop("en", newsApi)
        subscribeObservableOfArticle()
    }


    private fun subscribeObservableOfArticle() {
        newsList.clear()
        compositeDisposable.add(
            topObserver.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable.fromIterable(it.listNews)
                }
                .subscribeWith(createArticleObserver())
        )
    }


    private fun createArticleObserver(): DisposableObserver<NewsData> {
        return object : DisposableObserver<NewsData>() {
            override fun onNext(newsR: NewsData) {
                if (!newsList.contains(newsR)) {
                    newsList.add(newsR)
                }
            }

            override fun onComplete() {
                showArticlesOnRecyclerView()
            }

            override fun onError(e: Throwable) {
                Log.e("createArticleObserver", "Article error: ${e.message}")
            }
        }
    }

    private fun showArticlesOnRecyclerView() {
        binding.apply {
            if (newsList.size > 0) {
                    emptyField.visibility = View.GONE
                    retryBtn.visibility = View.GONE
                    recycler.visibility = View.VISIBLE
                    adapter.setNews(newsList)

            } else {

                recycler.visibility = View.GONE
                emptyField.visibility = View.VISIBLE
                retryBtn.visibility = View.VISIBLE
                retryBtn.setOnClickListener { checkUserKeywordInput() }


            }
            swipeRefresh.isRefreshing = false
        }
    }
    private fun generateRetrofitBuilder(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

}

//val retrofit: Retrofit = Retrofit.Builder()
//    .addConverterFactory(GsonConverterFactory.create())
//    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//    .baseUrl(BASE_URL)
//    .build()





