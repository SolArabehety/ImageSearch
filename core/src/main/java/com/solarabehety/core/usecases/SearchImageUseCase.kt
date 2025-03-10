package com.solarabehety.core.usecases

import com.solarabehety.core.model.Image
import com.solarabehety.core.model.SearchImageError
import com.solarabehety.core.networking.Result


/**
 * Implementation of the [SearchImageUseCase] that handles the logic for searching images.
 *
 * This class interacts with the [ImageRepository] to fetch images based on a search query.
 * It handles different error scenarios, such as no internet connection, server errors,
 * and unexpected errors, and returns a result that can either be a list of images or an error.
 *
 * @param imageRepository The repository used to fetch images based on the search query.
 */
interface SearchImageUseCase {

    /**
     * Executes the image search operation by passing the query to the repository.
     *
     * This method tries to fetch the images from the repository. If successful, it returns
     * a [Result.Success] containing the list of images. If there is a connection error,
     * a server error, or any other unexpected error, it returns an appropriate [Result.Error].
     *
     * @param query The search query used to fetch images.
     * @return A [Result] containing either a list of images or an error.
     */
    suspend fun invoke(
        query: String,
    ): Result<List<Image>, SearchImageError>
}
