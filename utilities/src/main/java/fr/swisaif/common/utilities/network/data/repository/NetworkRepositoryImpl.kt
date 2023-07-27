package fr.swisaif.common.utilities.network.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import fr.swisaif.common.utilities.network.data.model.NetworkType

/**
 * Implémentation de l'interface [NetworkRepository] pour l'API 24 (Nougat).
 *Cette classe utilise un [ConnectivityManager.NetworkCallback] pour écouter les changements d'état du réseau.
 */
@RequiresApi(Build.VERSION_CODES.N)
class NetworkRepositoryImpl(context: Context) : AbstractNetworkRepository(context),
    NetworkRepository {

    /**
     * Callback pour être notifié des changements d'état du réseau.
     * Appelle onEvent() en cas de disponibilité ou de perte de connexion.
     */
    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            onEvent()
        }

        override fun onLost(network: Network) {
            onEvent()
        }
    }

    /**
     * S'abonne aux changements d'état de la connexion en utilisant un [ConnectivityManager.NetworkCallback].
     */
    override fun subscribeNetwork() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.registerDefaultNetworkCallback(callback)
    }

    /**
     * Se désabonne des changements d'état de la connexion en annulant le [ConnectivityManager.NetworkCallback].
     */
    override fun disposeNetwork() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        connectivityManager?.unregisterNetworkCallback(callback)
    }

    /**
     * Récupère le type de réseau actif.
     * Cette méthode est appelée depuis [AbstractNetworkRepository.getNetwork] pour obtenir l'état du réseau.
     * @return le type de réseau actif ou null s'il n'y en a pas.
     */
    override fun findActiveNetworkType() = findActiveNetworkTypeFrom(context)
}

/**
 * Récupère le type de réseau actif à partir d'un [Context].
 * @param context le contexte pour récupérer le service [ConnectivityManager].
 * @return le type de réseau actif ou null s'il n'y en a pas.
 */
@RequiresApi(Build.VERSION_CODES.M)
fun findActiveNetworkTypeFrom(context: Context): NetworkType? =
    (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)?.let {
        val capabilities = it.getNetworkCapabilities(it.activeNetwork)
        when {
            capabilities == null -> null
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            else -> null
        }
    }