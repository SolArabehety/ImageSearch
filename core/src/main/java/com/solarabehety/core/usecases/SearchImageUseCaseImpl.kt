package com.solarabehety.core.usecases

import com.solarabehety.core.model.Image
import com.solarabehety.core.model.SearchImageError
import com.solarabehety.core.networking.Result
import com.solarabehety.core.repository.ConnectionErrorException
import com.solarabehety.core.repository.ImageRepository
import com.solarabehety.core.repository.SearchImageServerException

class SearchImageUseCaseImpl(private val imageRepository: ImageRepository) : SearchImageUseCase {

    override suspend fun invoke(query: String): Result<List<Image>, SearchImageError> {

        return try {
            val result = imageRepository.search(query)
            Result.Success(result)
        } catch (exception: ConnectionErrorException) {
            Result.Error(SearchImageError.NoInternetConnection)
        } catch (exception: SearchImageServerException) {
            Result.Error(SearchImageError.ServerError)
        } catch (exception: Exception) {
           Result.Error(SearchImageError.Unknown)
    }

}

companion object {
    private const val TAG = "SearchImageUseCaseImpl"
}

}
