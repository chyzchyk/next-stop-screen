package ua.pasinfosc.data.repository

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import ua.pasinfosc.domain.entities.Route
import ua.pasinfosc.domain.entities.Stop
import ua.pasinfosc.domain.repositories.StopsRepository
import java.io.*

class StopsRepositoryImpl : StopsRepository, KoinComponent {

    override suspend fun getRoutes(file: InputStream): List<Route> {
        return withContext(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(file, "UTF-8"))
            var line = reader.readLine()

            val list = mutableListOf<Route>()
            val stops = mutableListOf<Stop>()
            var id: Pair<String, String>? = null
            var name: String? = null

            while (line != null) {
                val str = line.toString()
                when {
                    str.startsWith("|") -> {
                        if (stops.isNotEmpty()) {
                            list += Route(
                                id = id!!,
                                name = name ?: stops.last().name,
                                stops = stops.toList()
                            )

                            stops.clear()
                            id = null
                            name = null
                        }

                        id = str.substringAfter('|').substringBefore('-') to str.substringAfter('-')
                    }
                    str.startsWith("#(") -> {
                        if (stops.isNotEmpty()) {
                            list += Route(
                                id = id!!,
                                name = name ?: stops.last().name,
                                stops = stops.toList()
                            )

                            stops.clear()
                        }

                        val lng = str
                            .substringAfter(", ")
                            .substringBefore(") ")
                            .toDouble()
                        val stopName = str
                            .substringAfter(") ")
                            .substringBefore("^")
                        val lat = str
                            .substringAfter("#(")
                            .substringBefore(", ")
                            .toDouble()
                        val soundNames = str
                            .substringAfter("^")
                            .split("\\")

                        stops += Stop(
                            name = stopName,
                            latLng = LatLng(lat, lng),
                            isFinal = true,
                            state = Stop.State.FURTHER,
                            soundNextStop = "sound/${soundNames[0]}.wav",
                            soundStop = "sound/${soundNames[1]}.wav"
                        )
                    }
                    str.startsWith("(") -> {
                        val lng = str
                            .substringAfter(", ")
                            .substringBefore(") ")
                            .toDouble()
                        val stopName = str
                            .substringAfter(") ")
                            .substringBefore("^")
                        val lat = str
                            .substringAfter("(")
                            .substringBefore(", ")
                            .toDouble()
                        val soundNames = str
                            .substringAfter("^")
                            .split("\\")

                        stops += Stop(
                            name = stopName,
                            latLng = LatLng(lat, lng),
                            isFinal = false,
                            state = Stop.State.FURTHER,
                            soundNextStop = "sound/${soundNames[0]}.wav",
                            soundStop = "sound/${soundNames[1]}.wav"
                        )
                    }
                    else -> {
                        name = str
                    }
                }

                line = reader.readLine()

            }
            list += Route(
                id = id!!,
                name = name ?: stops.last().name,
                stops = stops.toList()
            )

            return@withContext list
        }
    }
}