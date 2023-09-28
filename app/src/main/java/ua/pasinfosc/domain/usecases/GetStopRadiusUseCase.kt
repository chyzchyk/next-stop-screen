package ua.pasinfosc.domain.usecases

import ua.pasinfosc.domain.repositories.ConfigRepository
import java.io.InputStream

class GetStopRadiusUseCase(private val repository: ConfigRepository) {

    suspend operator fun invoke() = repository.getStopRadius()

}