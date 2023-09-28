package ua.pasinfosc.domain.usecases

import ua.pasinfosc.domain.repositories.ConfigRepository

class GetFilesAdUseCase(private val repository: ConfigRepository) {

    suspend operator fun invoke() = repository.getAdFile()

}