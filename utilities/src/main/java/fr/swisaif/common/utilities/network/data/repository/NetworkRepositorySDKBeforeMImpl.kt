package fr.swisaif.common.utilities.network.data.repository

import android.content.Context
import android.net.ConnectivityManager
import fr.swisaif.common.utilities.network.data.model.NetworkType

class NetworkRepositorySDKBeforeMImpl(context: Context) :
    AbstractNetworkRepositorySDKBeforeNImpl(context), NetworkRepository {
    /**
     * Recherche le type de réseau actif (Wifi ou cellulaire) à l'aide du service de connectivité Android.
     * Cette méthode est utilisée sur les versions de l'API antérieures à M (API 23).
     * @return le type de réseau actif ou null si aucun réseau n'est disponible
     */
    @Suppress("DEPRECATION")
    override fun findActiveNetworkType(): NetworkType? =
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)?.let { mng ->
            mng.activeNetworkInfo?.let {
                when {
                    !it.isConnected -> null
                    it.type == ConnectivityManager.TYPE_WIFI -> NetworkType.WIFI
                    it.type == ConnectivityManager.TYPE_MOBILE -> NetworkType.CELLULAR
                    else -> null
                }
            }
        }
}