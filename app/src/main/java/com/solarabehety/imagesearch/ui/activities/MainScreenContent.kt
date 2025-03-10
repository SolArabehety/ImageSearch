package com.solarabehety.imagesearch.ui.activities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.solarabehety.imagesearch.R
import com.solarabehety.imagesearch.ui.theme.lobsterFamily
import com.solarabehety.imagesearch.ui.viewmodel.MainViewModel
import com.solarabehety.imagesearch.ui.viewmodel.SEARCH_MIN_QUERY_CHARS
import com.solarabehety.imagesearch.ui.viewmodel.SearchActivityUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.util.Collections.emptyList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreenContent(viewModel: MainViewModel) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontFamily = lobsterFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val uiState = viewModel.uiState.collectAsState().value

                SearchScreenContent(
                    uiState = uiState,
                    queryMinChars = SEARCH_MIN_QUERY_CHARS,
                    onSearch = { query ->
                        viewModel.searchImages(query)
                    },
                )
            }
        }
    )
}

@Composable
private fun SearchScreenContent(
    uiState: SearchActivityUiState,
    queryMinChars: Int,
    onSearch: (String) -> Unit,
) {
    val listState = remember { LazyListState() }
    var showSearchBar by remember { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0) {
                    showSearchBar = true
                } else if (available.y < 0) {
                    showSearchBar = false
                }
                return super.onPreScroll(available, source)
            }
        }
    }

    val searchBarHeight by animateDpAsState(
        targetValue = if (showSearchBar) 70.dp else 0.dp,
        label = "searchBarHeight"
    )

    val inputState = rememberFieldsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(searchBarHeight)
                .background(Color.White)
                .padding(horizontal = dimensionResource(id = R.dimen.padding_xsmall))
                .clip(RoundedCornerShape(8.dp))
        ) {
            this@Column.AnimatedVisibility(
                visible = showSearchBar,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                SearchTextField(
                    inputState,
                    onSearch
                )
            }
        }
        Crossfade(targetState = uiState, label = "") { state ->
            when (state) {
                is SearchActivityUiState.Initial -> InitialState(queryMinChars)
                is SearchActivityUiState.Loading -> LoadingState()
                is SearchActivityUiState.Error -> ErrorState(state.message)
                is SearchActivityUiState.Success -> SuccessState(state.images, listState)
            }
        }
    }
}

@Composable
private fun SearchTextField(
    searchState: FieldsState,
    onSearch: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchFlow = remember { MutableStateFlow(searchState.searchValue) }

    fun search(query: String) {
        onSearch(query)
        keyboardController?.hide()
    }

    LaunchedEffect(Unit) {
        launch {
            searchFlow
                .debounce(SEARCH_DEBOUNCE_TIME)
                .drop(1)
                .collect {
                    search(it)
                }
        }
    }

    TextField(
        value = searchState.searchValue,
        onValueChange = {
            searchState.updateQuery(it)
            searchFlow.value = it
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.rounded_ratio))),
        placeholder = {
            Text(stringResource(id = R.string.search_bar_hint), color = Color.Gray)
        },
        singleLine = true,
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                search(searchState.searchValue)
            }
        ),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        )
    )

}

@Composable
private fun InitialState(
    queryMinChars: Int,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        SearchStatusScreen(
            title = stringResource(id = R.string.search_welcome),
            description =
            String.format(stringResource(id = R.string.search_welcome_description), queryMinChars),
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        SearchStatusScreen(
            title = stringResource(id = R.string.search_error),
            description = message,
            status = TextStatus.ERROR,
        )
    }
}

@Composable
private fun SuccessState(images: List<String>, listState: LazyListState) {
    if (images.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            SearchStatusScreen(
                title = stringResource(id = R.string.no_results_title),
                description = stringResource(id = R.string.no_results_description),
            )
        }
    }

    LazyColumn(
        state = listState,
    ) {
        items(images.size, key = { it }) { index ->
            val url = images[index]
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Loaded image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.image_height)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimensionResource(id = R.dimen.image_height))
                                .background(MaterialTheme.colorScheme.tertiary)
                                .shimmerEffect(),
                        )
                    },
                )

        }
    }
}


@Composable
private fun rememberFieldsState(
    initialSearch: String = "",
): FieldsState = remember { FieldsState(initialSearch) }

private class FieldsState(
    query: String,
) {
    var searchValue by mutableStateOf(query)
        private set


    fun updateQuery(query: String) {
        searchValue = query
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchScreenInitial() {
    SearchScreenContent(
        uiState = SearchActivityUiState.Initial,
        queryMinChars = 3,
        onSearch = {}
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewSearchScreenNoResults() {
    SearchScreenContent(
        uiState = SearchActivityUiState.Success(emptyList()),
        queryMinChars = 3,
        onSearch = {}
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewSearchScreenError() {
    SearchScreenContent(
        uiState = SearchActivityUiState.Error("Error fetching images"),
        queryMinChars = 3,
        onSearch = {}
    )
}


const val SEARCH_DEBOUNCE_TIME = 1000L
