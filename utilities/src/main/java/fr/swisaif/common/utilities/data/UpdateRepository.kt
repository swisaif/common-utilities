package fr.swisaif.common.utilities.data

import io.reactivex.Single

/**
 * Met à jour l'entité métier
 */
interface UpdateOneRepository<E> : Repository<E> {
    fun update(entity: E): Single<E>
}

/**
 * Met à jour toutes les entitées métiers.
 */
interface UpdateAllRepository<E> : Repository<E> {
    fun update(entities: List<E>): Single<List<E>>
}