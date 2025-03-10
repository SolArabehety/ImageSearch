package com.solarabehety.core.repository

import com.solarabehety.core.model.Image
import com.solarabehety.core.networking.Result


/**
 * ImageRepository interface defines the contract for retrieving images.
 */
interface ImageRepository {
    /**
     * Searches for images based on the query and returns a [Result] with the image list.
     *
     * @param query The search query to filter images.
     * @return [Result] A result that contains a list of [Image].
     */
    suspend fun search(query: String): List<Image>
}


class ConnectionErrorException(
    message: String,
    exception: Exception,
) : Exception(message, exception)


class SearchImageServerException(
    message: String,
    exception: Exception,
) : Exception(message, exception)

