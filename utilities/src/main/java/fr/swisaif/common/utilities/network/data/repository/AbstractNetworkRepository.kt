package fr.swisaif.common.utilities.network.data.repository

import android.content.Context
import fr.swisaif.common.utilities.data.subject.doOnFirstSubscribeAndLastDispose
import fr.swisaif.common.utilities.network.data.model.NetworkData
import fr.swisaif.common.utilities.network.data.model.NetworkType
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * Classe abstraite pour le repository de réseau.
 *
 * Cette classe gère la création d'un objet de type NetworkData à partir des informations du réseau actuel.
 * Les classes concrètes qui étendent cette classe doivent implémenter les méthodes abstraites suivantes :
 * - subscribeNetwork() pour s'abonner aux changements d'état du réseau
 * - disposeNetwork() pour se désabonner des changements d'état du réseau
 * - findActiveNetworkType() pour trouver le type de réseau actif (Wifi, Cellulaire, etc.)
 *
 * Cette classe utilise un cache pour stocker l'objet NetworkData et émet des événements à chaque changement d'état du réseau.
 *
 * @property context le contexte de l'application
 */
abstract class AbstractNetworkRepository(protected val context: Context) {
    /**
     * Cache pour stocker l'objet NetworkData.
     */
    private val cacheNetworkData = BehaviorSubject.create<NetworkData>()

    /**
     * Observable qui émet des événements à chaque changement d'état du réseau.
     */
    private val observable = cacheNetworkData.doOnFirstSubscribeAndLastDispose({
        onEvent()
        subscribeNetwork()
    }, {
        disposeNetwork()
    })

    /**
     * Méthode appelée à chaque changement d'état du réseau.
     * Elle crée un objet NetworkData à partir des informations du réseau actuel.
     * Si l'objet a changé depuis le dernier appel de cette méthode, elle émet un événement.
     */
    fun onEvent() {
        createData().let { data ->
            if (!cacheNetworkData.hasValue() || cacheNetworkData.value != data) {
                Timber.i("onEvent isConnected=%s", data.connected)
                cacheNetworkData.onNext(data)
            }
        }
    }

    /**
     * Méthode qui crée un objet NetworkData à partir des informations du réseau actuel.
     * Si aucun réseau n'est actif, un objet NoNetworkData est créé.
     *
     * @return l'objet NetworkData créé
     */
    private fun createData() = findActiveNetworkType()?.let {
        when (it) {
            NetworkType.WIFI -> WifiNetworkDataUtils.createFrom(context)
            NetworkType.CELLULAR -> CellularNetworkDataUtils.createFrom(context)
        }
    } ?: NoNetworkDataUtils.createFrom(context)

    /**
     * Méthode qui retourne un Observable qui émet des objets NetworkData.
     *
     * @return l'Observable qui émet des objets NetworkData
     */
    fun find(): Observable<NetworkData> = observable

    /**
     * Méthode abstraite pour s'abonner aux changements d'état du réseau.
     */
    abstract fun subscribeNetwork()

    /**
     * Méthode abstraite pour se désabonner des changements d'état du réseau.
     */
    abstract fun disposeNetwork()

    /**
     * Méthode abstraite pour trouver le type de réseau actif (Wifi, Cellulaire, etc.).
     *
     * @return le type de réseau actif ou null si aucun réseau n'est actif
     */
    abstract fun findActiveNetworkType(): NetworkType?
}
