package fr.swisaif.common.utilities.network.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager

/**
 * Classe abstraite qui fournit une implémentation pour la version SDK avant N (API 24).
 * Hérite de la classe abstraite AbstractNetworkRepository.
 */
abstract class AbstractNetworkRepositorySDKBeforeNImpl(context: Context) :
    AbstractNetworkRepository(context) {
    /**
     * BroadcastReceiver pour écouter les changements d'état de la connexion réseau.
     * Cette méthode est appelée lorsque la connexion réseau change d'état.
     */
    private val receiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                onEvent()
            }
        }

    /**
     * Méthode pour s'abonner aux changements d'état de la connexion réseau en enregistrant le BroadcastReceiver.
     */
    override fun subscribeNetwork() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(receiver, filter)
    }

    /**
     * Méthode pour se désabonner des changements d'état de la connexion réseau en supprimant le BroadcastReceiver.
     */
    override fun disposeNetwork() {
        context.unregisterReceiver(receiver)
    }
}