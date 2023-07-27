package fr.swisaif.common.utilities.data

import io.reactivex.Observable

/**
 * Renvoi l'existence d'une entité ou pas
 */
interface ExistRepository<E> : Repository<E> {
    fun exist(): Observable<Boolean>
}