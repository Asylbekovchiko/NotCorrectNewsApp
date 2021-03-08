package kg.asylbekov.mynews2.interfaces


import io.reactivex.Observable
import kg.asylbekov.mynews2.dataClasses.pointTopRaiting
import retrofit2.http.GET
import retrofit2.http.Query

interface pointTopRaitingInterface {
    @GET("top-headlines")
    fun getTop(@Query("country") country:String,
    @Query("apiKey")apiKey: String):Observable<pointTopRaiting>




    @GET("top-headlines")
    fun getUserSearchInput(
        @Query("apiKey") apiKey: String,
        @Query("q") q: String
    ): Observable<pointTopRaiting>
}