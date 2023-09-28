package ua.pasinfosc.domain.usecases

import ua.pasinfosc.domain.repositories.ConfigRepository
import java.io.InputStream

class GetBusIdUseCase(private val repository: ConfigRepository) {

    suspend operator fun invoke() = repository.getBusId()

}