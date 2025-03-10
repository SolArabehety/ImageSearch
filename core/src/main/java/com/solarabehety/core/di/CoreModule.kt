package com.solarabehety.core.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.solarabehety.core.networking.ApiKeys
import com.solarabehety.core.networking.ApiService
import com.solarabehety.core.repository.ImageRepository
import com.solarabehety.core.repository.ImageRepositoryImpl
import com.solarabehety.core.usecases.SearchImageUseCase
import com.solarabehety.core.usecases.SearchImageUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * CoreModule provides necessary dependencies for this module.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object CoreModule {
    private const val IMAGES_API_DOMAIN = "https://api.pexels.com/v1/"

    /**
     * Provides a [Json] instance with configuration to ignore unknown keys during JSON parsing.
     *
     * @return [Json] The configured JSON parser.
     */
    @Provides
    @Singleton
    fun provideJson() = Json {
        ignoreUnknownKeys = true
    }

    /**
     * Provides an [OkHttpClient] instance with logging and authentication interceptors.
     *
     * @return [OkHttpClient] The configured OkHttp client.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", ApiKeys.CLIENT_ID)
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    /**
     * Provides a [Retrofit] instance configured with the provided [Json] and [OkHttpClient].
     *
     * @param json The JSON parser to be used for converting responses.
     * @param okHttpClient The HTTP client to be used for making network requests.
     * @return [Retrofit] The configured Retrofit instance.
     */
    @Provides
    @Singleton
    fun provideRetrofit(json: Json, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(IMAGES_API_DOMAIN)
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        )
        .client(okHttpClient)
        .build()

    /**
     * Provides an instance of [ApiService] created using the provided [Retrofit].
     *
     * @param retrofit The Retrofit instance used to create the [ApiService].
     * @return [ApiService] The created ApiService instance.
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    fun provideImageRepository(
        apiService: ApiService,
    ): ImageRepository = ImageRepositoryImpl(
        apiService = apiService
    )

    @Provides
    fun provideSearchImageUseCase(
        imageRepository: ImageRepository,
    ): SearchImageUseCase = SearchImageUseCaseImpl(
        imageRepository = imageRepository,
    )
}
