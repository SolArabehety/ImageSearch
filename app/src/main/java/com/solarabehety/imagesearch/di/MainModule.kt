package com.solarabehety.imagesearch.di

import android.content.Context
import com.solarabehety.core.repository.ImageRepository
import com.solarabehety.core.usecases.SearchImageUseCase
import com.solarabehety.core.usecases.SearchImageUseCaseImpl
import com.solarabehety.imagesearch.ui.viewmodel.StringMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(ViewModelComponent::class)
internal object MainModule {

    @ViewModelScoped
    @Provides
    fun provideStringMapper(
        @ApplicationContext context: Context,
    ) = StringMapper(context.resources)

}