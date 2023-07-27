package fr.swisaif.common.utilities.data

import io.reactivex.Observable

/**
 * Permet de récupérer votre unique entité métier
 */
interface FindRepository<E> : Repository<E> {
    fun find(): Observable<E>
}

/**
 * Le Type I vous permet de choisir ce que vous voulez pour votre recherche, ça peut être un String ou un objet plus complexe
 */
interface FindByIdRepository<E, I> : Repository<E> {
    fun findById(id: I): Observable<E>
}

/**
 * Renvoi toutes les entitées métiers
 */
interface FindAllRepository<E> : Repository<E> {
    fun findAll(): Observable<List<E>>
}