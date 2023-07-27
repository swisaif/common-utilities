package fr.swisaif.common.utilities.domain.usecase

import fr.swisaif.common.utilities.data.FindAllRepository
import io.reactivex.Observable

interface FindAllUseCase<E> : UseCase {
    operator fun invoke(): Observable<List<E>>
}

open class FindAllUseCaseImpl<E, R : FindAllRepository<E>>(private val repository: R) :
    FindAllUseCase<E> {
    override fun invoke() = repository.findAll()
}
