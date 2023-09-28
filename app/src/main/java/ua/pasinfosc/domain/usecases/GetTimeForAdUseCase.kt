package ua.pasinfosc.domain.usecases

import ua.pasinfosc.domain.repositories.ConfigRepository
import java.io.InputStream

class GetTimeForAdUseCase(private val repository: ConfigRepository) {

    suspend operator fun invoke() = repository.getTimeForAd()

}