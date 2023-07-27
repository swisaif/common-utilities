package fr.swisaif.common.utilities.domain.usecase

import fr.swisaif.common.utilities.data.InsertOneRepository
import io.reactivex.Single

/**
 * Permet d'insérer une entité dans un repository
 */
interface InsertOneUseCase<E> : UseCase {
    operator fun invoke(entity: E): Single<E>
}

open class InsertOneUseCaseImpl<E, R : InsertOneRepository<E>>(private val repository: R) :
    InsertOneUseCase<E> {

    override fun invoke(entity: E): Single<E> = repository.insert(entity)
}
