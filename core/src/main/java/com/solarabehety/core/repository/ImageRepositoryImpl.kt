package com.solarabehety.core.repository

import com.solarabehety.core.model.Image
import com.solarabehety.core.networking.ApiService
import retrofit2.HttpException
import java.io.IOException

/**
 * Implementation of [ImageRepository] that interacts with the [ApiService] to fetch image data.
 */
class ImageRepositoryImpl(
    private val apiService: ApiService,
) : ImageRepository {


    override suspend fun search(query: String): List<Image> =
        try {
            apiService.getImages(query).photos?.map {
                Image(
                    url = it.src.original
                )
            } ?: emptyList()

        } catch (e: Exception) {
            throw when (e) {
                is IOException -> ConnectionErrorException("Connection error", e)
                is HttpException -> SearchImageServerException("Server error", e)
                else -> e
            }
        }


    companion object {
        private const val TAG = "ImageRepositoryImpl"
    }
}