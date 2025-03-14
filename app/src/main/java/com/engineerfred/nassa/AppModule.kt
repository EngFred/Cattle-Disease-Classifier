package com.engineerfred.nassa

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesClassifier(
        @ApplicationContext context: Context
    ): CattleDiseaseClassifier {
        return CattleDiseaseClassifier(context)
    }

    @Provides
    @Singleton
    fun providesPrefrencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager = PreferencesManager(context)
}