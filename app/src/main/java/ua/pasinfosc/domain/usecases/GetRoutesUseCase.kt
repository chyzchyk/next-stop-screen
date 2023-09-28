package ua.pasinfosc.domain.usecases

import ua.pasinfosc.domain.entities.Route
import ua.pasinfosc.domain.repositories.StopsRepository
import java.io.FileInputStream
import java.io.InputStream

class GetRoutesUseCase(private val repository: StopsRepository) {

    suspend operator fun invoke(file: InputStream): List<Route> = repository.getRoutes(file)

}