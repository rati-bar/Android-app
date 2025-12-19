package com.screetime.child.di

import android.content.Context
import com.screetime.child.security.IntegrityChecker
import com.screetime.child.security.RootDetector
import com.screetime.child.security.TimeValidator
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
    fun provideTimeValidator(): TimeValidator {
        return TimeValidator()
    }

    @Provides
    @Singleton
    fun provideRootDetector(): RootDetector {
        return RootDetector()
    }

    @Provides
    @Singleton
    fun provideIntegrityChecker(
        @ApplicationContext context: Context
    ): IntegrityChecker {
        return IntegrityChecker(context)
    }
}
