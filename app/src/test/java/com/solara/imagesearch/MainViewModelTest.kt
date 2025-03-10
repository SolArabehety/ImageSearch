package com.solara.imagesearch

import com.solarabehety.core.model.Image
import com.solarabehety.core.model.SearchImageError
import com.solarabehety.core.usecases.SearchImageUseCase
import com.solarabehety.imagesearch.ui.viewmodel.MainViewModel
import com.solarabehety.imagesearch.ui.viewmodel.SearchActivityUiState
import com.solarabehety.imagesearch.ui.viewmodel.StringMapper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import com.solarabehety.core.networking.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private lateinit var viewModel: MainViewModel
    private val stringMapper: StringMapper = mockk()
    private val searchImageUseCase: SearchImageUseCase = mockk()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        viewModel = MainViewModel(stringMapper, searchImageUseCase)
    }

    @Test
    fun `given query length less than SEARCH_MIN_QUERY_CHARS, should set Initial state`() {
        // GIVEN
        val shortQuery = "ca"
        // WHEN
        viewModel.searchImages(shortQuery)
        // THEN
        assertEquals(SearchActivityUiState.Initial, viewModel.uiState.value)
    }

    @Test
    fun `given valid query, when search is successful, should update uiState to Success`() =
        runTest {
            // GIVEN
            val validQuery = "cats"
            val images = listOf(Image("url1"), Image("url2"))
            coEvery { searchImageUseCase.invoke(validQuery) } returns Result.Success(images)

            // WHEN
            viewModel.searchImages(validQuery)

            // THEN
            assertEquals(SearchActivityUiState.Loading, viewModel.uiState.value)
            advanceUntilIdle()
            assertEquals(
                SearchActivityUiState.Success(images.map { it.url }),
                viewModel.uiState.value
            )
        }

    @Test
    fun `given valid query, when search fails with NoInternetConnection, should update uiState to Error`() =
        runTest {
            // GIVEN
            val validQuery = "cats"
            val errorMessage = "No internet connection"
            coEvery { searchImageUseCase.invoke(validQuery) } returns Result.Error(
                SearchImageError.NoInternetConnection
            )

            coEvery { stringMapper.mapErrorString(any()) } returns errorMessage

            // WHEN
            viewModel.searchImages(validQuery)

            // THEN
            assertEquals(SearchActivityUiState.Loading, viewModel.uiState.value)
            advanceUntilIdle()
            assertEquals(
                SearchActivityUiState.Error(errorMessage),
                viewModel.uiState.value
            )
        }

    @Test
    fun `given valid query, when search fails with Unknown, should update uiState to Error`() =
        runTest {
            // GIVEN
            val validQuery = "cats"
            val errorMessage = "Unknown error"
            coEvery { searchImageUseCase.invoke(validQuery) } returns Result.Error(
                SearchImageError.Unknown
            )

            coEvery { stringMapper.mapErrorString(any()) } returns errorMessage

            // WHEN
            viewModel.searchImages(validQuery)

            // THEN
            assertEquals(SearchActivityUiState.Loading, viewModel.uiState.value)
            advanceUntilIdle()
            assertEquals(
                SearchActivityUiState.Error(errorMessage),
                viewModel.uiState.value
            )
        }

}

@ExperimentalCoroutinesApi
class MainCoroutineRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}