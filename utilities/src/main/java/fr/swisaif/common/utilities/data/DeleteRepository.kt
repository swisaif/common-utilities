package fr.swisaif.common.utilities.data

import io.reactivex.Completable

/**
 * Supprime l'entité métier
 */
interface DeleteOneRepository<E> : Repository<E> {
    fun delete(entity: E): Completable
}

/**
 * Supprime toutes les entitées métiers
 */
interface DeleteAllRepository<E> : Repository<E> {
    fun deleteAll(): Completable
}