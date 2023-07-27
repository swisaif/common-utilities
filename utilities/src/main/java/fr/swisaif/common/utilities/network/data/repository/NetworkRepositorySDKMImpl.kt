package fr.swisaif.common.utilities.network.data.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.M)
class NetworkRepositorySDKMImpl(context: Context) :
    AbstractNetworkRepositorySDKBeforeNImpl(context), NetworkRepository {
    /**
     *  Retourne le type de réseau actif en utilisant les fonctionnalités de connectivité du système d'exploitation
     * pour les versions d'Android supérieures ou égales à M (API 23).
     *
     * @return Le type de réseau actif, soit WIFI ou CELLULAR, ou null si aucun réseau actif n'est disponible.
     * @throws SecurityException si l'application ne dispose pas des autorisations appropriées pour accéder aux informations de connectivité.

     */
    override fun findActiveNetworkType() = findActiveNetworkTypeFrom(context)
}