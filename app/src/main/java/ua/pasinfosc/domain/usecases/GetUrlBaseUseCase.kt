package ua.pasinfosc.domain.usecases

import ua.pasinfosc.domain.repositories.ConfigRepository
import java.io.InputStream

class GetUrlBaseUseCase(private val repository: ConfigRepository) {

    operator fun invoke() = repository.getBaseUrl()

}