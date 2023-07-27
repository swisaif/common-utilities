package fr.swisaif.common.utilities.network.data

import android.content.Context
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.swisaif.common.utilities.network.data.repository.NetworkRepository
import fr.swisaif.common.utilities.network.data.repository.NetworkRepositoryImpl
import fr.swisaif.common.utilities.network.data.repository.NetworkRepositorySDKMImpl
import fr.swisaif.common.utilities.network.data.repository.NetworkRepositorySDKBeforeMImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkRepository(context: Context): NetworkRepository = when {
        Build.VERSION.SDK_INT == Build.VERSION_CODES.M -> NetworkRepositorySDKMImpl(context)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> NetworkRepositoryImpl(context)
        else -> NetworkRepositorySDKBeforeMImpl(context)
    }
}