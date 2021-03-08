package kg.asylbekov.mynews2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kg.asylbekov.mynews2.R
import kg.asylbekov.mynews2.dataClasses.NewsData
import kotlinx.android.synthetic.main.layout_holder.view.*


class RecyclerAdapter(private var listNews: ArrayList<NewsData>) : RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>() {
    private lateinit var viewGroupContext: Context
    private val pngs = "https://pbs.twimg.com/profile_images/467502291415617536/SP8_ylk9.png"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        viewGroupContext = parent.context
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_holder, parent, false)
    return RecyclerHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        val news: NewsData = listNews[position]
        setPropers(holder, news)
//        holder.title.setOnClickListener {
//            println("111111111111111111")
//        }
    }
    private fun setPropers(recHolder: RecyclerHolder, newS: NewsData){
        checkPngPicasso(newS, recHolder)
    recHolder.title.text = newS?.title
        recHolder.description.text=newS?.description
    }



    private fun checkPngPicasso(newsData: NewsData, recHolder: RecyclerHolder) {
       if(newsData.urlToImage == null || newsData.urlToImage.isEmpty()){
           Picasso.get().load(pngs)
                   .centerCrop()
                   .fit()
                   .into(recHolder.logo)
       }else{
           Picasso.get().load((newsData.urlToImage))
                   .fit()
                   .centerCrop().into(recHolder.logo)
       }
    }




    override fun getItemCount(): Int {
        return listNews.size
    }
//    class RecyclerHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
//     lateinit var imageView: ImageView
//        lateinit var title: TextView
//        lateinit var description: TextView
//
//        init {
//            title = itemView.findViewById(R.id.title)
//            description = itemView.findViewById(R.id.description)
//            imageView = itemView.findViewById(R.id.logo)
//        }
//    }


    fun setNews(newsD: ArrayList<NewsData>) {
        listNews = newsD
        notifyDataSetChanged()
    }
    inner class RecyclerHolder(private val itemview: View) : RecyclerView.ViewHolder(itemview) {

        val cards: CardView by lazy { itemview.c }
        val logo: ImageView by lazy { itemview.logo }
        val title: TextView by lazy { itemview.title }
        val description: TextView by lazy { itemview.description }
    }
}

