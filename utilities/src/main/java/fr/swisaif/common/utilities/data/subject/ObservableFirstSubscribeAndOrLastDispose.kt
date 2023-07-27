package fr.swisaif.common.utilities.data.subject

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.plugins.RxJavaPlugins
import java.util.concurrent.atomic.AtomicInteger

/**
 * Permet de gérer les cas des tous premiers abonnements et le tout dernier désabonnement
 *
 * actionOnFirstSubscribe: Action à trigger pour chaque "premier abonnement" à l'observable
 * actionOnLastDispose: Action à trigger pour chaque dernier "fin d'abonnement" à l'observable
 */

class ObservableFirstSubscribeAndOrLastDispose<T, O: Observable<T>>(
    source: O,
    onFirstSubscribe: (() -> Unit)? = null,
    onLastDispose: (() -> Unit)? = null
) : Observable<T>() {

    private val observersCount: AtomicInteger = AtomicInteger(0)

    private val source = source
        .doOnSubscribe {
            if (observersCount.getAndIncrement() == 0) {
                onFirstSubscribe?.let { it() }
            }
        }
        .doOnDispose {
            if (observersCount.decrementAndGet() == 0) {
                onLastDispose?.let { it() }
            }
        }

    override fun subscribeActual(observer: Observer<in T>) {
        source.subscribe(observer)
    }
}

fun <T> Observable<T>.doOnFirstSubscribe(action: (() -> Unit)): Observable<T> =
    RxJavaPlugins.onAssembly(ObservableFirstSubscribeAndOrLastDispose(this, onFirstSubscribe = action))

fun <T> Observable<T>.doOnLastDispose(action: (() -> Unit)): Observable<T> =
    RxJavaPlugins.onAssembly(ObservableFirstSubscribeAndOrLastDispose(this, onLastDispose = action))

fun <T> Observable<T>.doOnFirstSubscribeAndLastDispose(
    onFirstSubscribe: (() -> Unit),
    onLastDispose: (() -> Unit)
): Observable<T> = RxJavaPlugins.onAssembly(ObservableFirstSubscribeAndOrLastDispose(this, onFirstSubscribe, onLastDispose))
