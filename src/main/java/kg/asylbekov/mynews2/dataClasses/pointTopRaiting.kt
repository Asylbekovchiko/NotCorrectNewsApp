package kg.asylbekov.mynews2.dataClasses

data class pointTopRaiting(
    //"status": "ok",
    //"totalResults": 35,



    val status: String, val totalResult: Int, val listNews: List<NewsData>
)
