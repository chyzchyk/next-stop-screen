package ua.pasinfosc.domain.usecases

import ua.pasinfosc.domain.repositories.BusesRepository

class GetBusesUseCase(private val repository: BusesRepository) {

    suspend operator fun invoke() = repository.getBuses()
}