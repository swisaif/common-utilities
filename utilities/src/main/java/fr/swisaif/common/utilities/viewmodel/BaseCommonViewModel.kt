package fr.swisaif.common.utilities.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * ViewModel de base qui doit être étendu pour tout ViewModel Reloaded
 * - Mise à disposition d'un disposableManager par défaut, pour les flux
 */
abstract class BaseCommonViewModel : ViewModel() {
    @JvmField
    @Deprecated("Ne pas faire de subscription dans le viewModel mais convertir l'objet rx en liveData")
    protected val disposableManager = CompositeDisposable()

    override fun onCleared() {
        if (!disposableManager.isDisposed) {
            disposableManager.dispose()
            disposableManager.clear()
        }
        super.onCleared()
    }

    private val err = MutableLiveData<Throwable>()

    val errors: LiveData<Throwable> = err.toSingleLiveEvent()

    /**
     * Combinaison de liveData
     */
    protected open fun <T, K, R> LiveData<T>.combineWith(
        liveData: LiveData<K>, block: (T?, K?) -> R
    ): LiveData<R> {
        val result = MediatorLiveData<R>()
        result.addSource(this) {
            result.value = block(this.value, liveData.value)
        }
        result.addSource(liveData) {
            result.value = block(this.value, liveData.value)
        }
        return result
    }

    protected open fun <T> onSuccess(data: T): Result<T> = Result.success(data)

    protected open fun <T> onError(throwable: Throwable): Result<T> {
        err.value = throwable
        return Result.error(throwable)
    }

    private fun <T> Flowable<T>.toLiveData() = LiveDataReactiveStreams.fromPublisher(this)

    @Deprecated("A ne pas utiliser (Result doit rester masquer)")
    @JvmName("toLiveDataResult")
    protected fun <T> Observable<Result<T>>.toLiveData() =
        this.toFlowable(BackpressureStrategy.LATEST).cache().toLiveData().map { it.data }

    protected fun <T> Observable<T>.toLiveData(onErrorReturn: Function<Throwable, T>) =
        this.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
            .doOnError { Timber.e(it) } // Est fait avant le onErrorReturn car sinon pas d'erreur donc pas de log
            .onErrorReturn(onErrorReturn).secureDispose().cache()
            .toFlowable(BackpressureStrategy.LATEST).toLiveData()

    protected fun <T> Observable<T>.toLiveData(): LiveData<T> =
        MediatorLiveData<T>().also { filter ->
            // Utilisation d'une référence faible au ViewModel afin d'éviter les fuites mémoires
            val vm = WeakReference(this@BaseCommonViewModel)

            filter.addSource(this.map { vm.get()?.onSuccess(it) ?: Result.success(it) }
                .toLiveData { vm.get()?.onError(it) ?: Result.error(it) }) { result ->
                if (result.status == Result.Status.SUCCESS) {
                    filter.value = result.data!!
                }
            }
        }

    @Deprecated("Les UseCases doivent sortir des observables")
    protected fun <T> Single<T>.toLiveData(onErrorReturn: Function<Throwable, T>) =
        this.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
            .doOnError { Timber.e(it) } // Est fait avant le onErrorReturn car sinon pas d'erreur donc pas de log
            .onErrorReturn(onErrorReturn).toFlowable().cache().toLiveData()

    @Deprecated("Les UseCases doivent sortir des observables")
    protected fun <T> Single<T>.toLiveData() = MediatorLiveData<T>().also { filter ->
        filter.addSource(this.map { data: T -> onSuccess(data) }
            .toLiveData { onError(it) }) { result ->
            if (result.status == Result.Status.SUCCESS) {
                filter.value = result.data!!
            }
        }
    }

    protected fun <T> Observable<T>.toSingleLiveEvent(onErrorReturn: Function<Throwable, T>) =
        toLiveData(onErrorReturn).toSingleLiveEvent()

    protected fun <T> Observable<T>.toSingleLiveEvent() = toLiveData().toSingleLiveEvent()

    @Deprecated("Les UseCases doivent sortir des observables")
    protected fun <T> Single<T>.toSingleLiveEvent(onErrorReturn: Function<Throwable, T>) =
        toLiveData(onErrorReturn).toSingleLiveEvent()

    @Deprecated("Les UseCases doivent sortir des observables")
    protected fun <T> Single<T>.toSingleLiveEvent() = toLiveData().toSingleLiveEvent()

    protected fun Completable.toLiveData(onErrorReturn: Function<Throwable, Boolean>) =
        this.andThen(Observable.just(true)).toLiveData(onErrorReturn)

    protected fun Completable.toLiveData() = this.andThen(Observable.just(true)).toLiveData()

    protected fun Completable.toSingleLiveEvent(onErrorReturn: Function<Throwable, Boolean>) =
        toLiveData(onErrorReturn).toSingleLiveEvent()

    protected fun Completable.toSingleLiveEvent() = toLiveData().toSingleLiveEvent()

    protected fun <T> LiveData<T>.toSingleLiveEvent(): LiveData<T> =
        object : MediatorLiveData<T>() {
            private var lastEmittedValue: T? = null

            override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
                if (hasActiveObservers()) {
                    Timber.w("Multiple observers registered but only one will be notified of changes.")
                }

                super.observe(owner, {
                    if (lastEmittedValue != it) {
                        lastEmittedValue = it
                        observer.onChanged(it)
                    }
                })
            }

            override fun observeForever(observer: Observer<in T>) {
                throw UnsupportedOperationException()
            }
        }.apply {
            addSource(this@toSingleLiveEvent) {
                value = it
            }
        }

    private fun <T> Observable<T>.secureDispose(): Observable<T> {
        val vm = WeakReference(this@BaseCommonViewModel)
        return this.doOnSubscribe {
            vm.get()?.disposableManager?.add(it)
        }
    }

    /**
     * A generic class that holds a value with its loading status.
     * @param <T>
    </T> */
    data class Result<out T>(val status: Status, val data: T?, val throwable: Throwable?) {
        companion object {
            fun <T> success(data: T?): Result<T> {
                return Result(
                    Status.SUCCESS, data, null
                )
            }

            fun <T> error(throwable: Throwable): Result<T> {
                return Result(
                    Status.ERROR, null, throwable
                )
            }
        }

        enum class Status {
            SUCCESS, ERROR
        }
    }
}