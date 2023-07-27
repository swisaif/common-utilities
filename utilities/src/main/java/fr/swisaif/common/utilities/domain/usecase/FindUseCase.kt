package fr.swisaif.common.utilities.domain.usecase

import fr.swisaif.common.utilities.data.FindRepository
import io.reactivex.Observable

/**
 * Permet de récupérer un élément du repository de l'entité
 */
interface FindUseCase<E> : UseCase {
    operator fun invoke(): Observable<E>
}

open class FindUseCaseImpl<E, R : FindRepository<E>>(private val repository: R) :
    FindUseCase<E> {

    override fun invoke(): Observable<E> = repository.find()
}
