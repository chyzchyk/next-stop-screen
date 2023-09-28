package ua.pasinfosc.domain.repositories

import ua.pasinfosc.domain.entities.Route
import java.io.FileInputStream
import java.io.InputStream

interface StopsRepository {

    suspend fun getRoutes(file: InputStream): List<Route>

}