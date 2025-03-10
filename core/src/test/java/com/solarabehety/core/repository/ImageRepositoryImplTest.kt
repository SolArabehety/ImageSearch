package com.solarabehety.core.repository

import com.solarabehety.core.networking.ApiService
import com.solarabehety.core.networking.ImagesResponse
import com.solarabehety.core.networking.Photo
import com.solarabehety.core.networking.Source
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class ImageRepositoryImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var repository: ImageRepositoryImpl

    @MockK
    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = ImageRepositoryImpl(apiService)
    }

    @Test
    fun `given successful API response, when search is called, should return mapped images`() = runTest {
        // GIVEN
        val query = "cats"
        val apiResponse = ImagesResponse(
            photos = listOf(
                Photo(src = Source(original = "url1")),
                Photo(src = Source(original = "url2"))
            )
        )
        coEvery { apiService.getImages(query) } returns apiResponse

        // WHEN
        val result = repository.search(query)

        // THEN
        assertEquals(2, result.size)
        assertEquals("url1", result[0].url)
        assertEquals("url2", result[1].url)
    }

    @Test
    fun `given API returns empty list, when search is called, should return empty list`() = runTest {
        // GIVEN
        val query = "dogs"
        val apiResponse = ImagesResponse(photos = emptyList())
        coEvery { apiService.getImages(query) } returns apiResponse

        // WHEN
        val result = repository.search(query)

        // THEN
        assertTrue(result.isEmpty())
    }
}
