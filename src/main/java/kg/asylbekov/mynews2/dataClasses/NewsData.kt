package kg.asylbekov.mynews2.dataClasses




data class NewsData(
 val source: SourceData, val author: String, val title: String, val description: String, val url: String,
 val urlToImage: String, val publishedAt: String, val content: String
)
