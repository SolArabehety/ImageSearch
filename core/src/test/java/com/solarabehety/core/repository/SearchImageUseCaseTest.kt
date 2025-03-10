package com.solarabehety.core.repository

import com.solarabehety.core.model.Image
import com.solarabehety.core.model.SearchImageError
import com.solarabehety.core.networking.Result
import com.solarabehety.core.usecases.SearchImageUseCase
import com.solarabehety.core.usecases.SearchImageUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SearchImageUseCaseTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var useCase: SearchImageUseCase

    @MockK
    private lateinit var imageRepository: ImageRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = SearchImageUseCaseImpl(imageRepository)
    }

    @Test
    fun `given valid query, when repository returns images, should return Success`() = runTest {
        // GIVEN
        val query = "cats"
        val images = listOf(Image("url1"), Image("url2"))
        coEvery { imageRepository.search(query) } returns images

        // WHEN
        val result = useCase.invoke(query)

        // THEN
        assertTrue(result is Result.Success)
        assertEquals(images, (result as Result.Success).data)
    }

    @Test
    fun `given network error, when repository throws ConnectionErrorException, should return NoInternetConnection`() = runTest {
        // GIVEN
        val query = "dogs"
        coEvery { imageRepository.search(query) } throws ConnectionErrorException("No internet", mockk())

        // WHEN
        val result = useCase.invoke(query)

        // THEN
        assertTrue(result is Result.Error)
        assertEquals(SearchImageError.NoInternetConnection, (result as Result.Error).error)
    }

    @Test
    fun `given server error, when repository throws SearchImageServerException, should return ServerError`() = runTest {
        // GIVEN
        val query = "birds"
        coEvery { imageRepository.search(query) } throws SearchImageServerException("Server failure", mockk())

        // WHEN
        val result = useCase.invoke(query)

        // THEN
        assertTrue(result is Result.Error)
        assertEquals(SearchImageError.ServerError, (result as Result.Error).error)
    }

    @Test
    fun `given unknown error, when repository throws Exception, should return Unknown`() = runTest {
        // GIVEN
        val query = "landscape"
        coEvery { imageRepository.search(query) } throws IllegalStateException("Unexpected error")

        // WHEN
        val result = useCase.invoke(query)

        // THEN
        assertTrue(result is Result.Error)
        assertEquals(SearchImageError.Unknown, (result as Result.Error).error)
    }
}
