package com.solarabehety.core.networking


import retrofit2.http.GET
import retrofit2.http.Query

/**
 * ApiService interface defines methods for interacting with the image API.
 */
interface ApiService {

    /**
     * Fetches a list of images based on the search query.
     *
     * @param query The search query to filter images.
     * @return [ImagesResponse] The response containing a list of images.
     */
    @GET("search")
    suspend fun getImages(
        @Query("query") query: String,
        @Query("orientation") orientation: String = "landscape",
        @Query("size") size: String = "small",
    ): ImagesResponse

}

object ApiKeys {
    /**
     * The client ID used for authentication with the API.
     */
    const val CLIENT_ID = "quKd01ok4X3p1viwIgY4fkfVR006amY5mjMhciQXoG3veLtyTp8oGdAh"
}