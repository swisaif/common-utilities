package fr.swisaif.common.utilities.data.storage

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.swisaif.common.utilities.data.storage.datasource.PreferenceDataSource
import fr.swisaif.common.utilities.data.storage.datasource.PreferenceDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {


    @Singleton
    @Provides
    fun providePreferenceDataSource(@ApplicationContext context: Context): PreferenceDataSource =
        PreferenceDataSourceImpl(context)
}