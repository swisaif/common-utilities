package fr.swisaif.common.utilities.data

import io.reactivex.Single

/**
 * Insertion d'une entité
 */
interface InsertOneRepository<E> : Repository<E> {
    fun insert(entity : E) : Single<E>
}

/**
 * Insestion d'un ensemble d'entité
 */
interface InsertAllRepository<E> : Repository<E> {
    fun insert(entities : List<E>) : Single<List<E>>
}