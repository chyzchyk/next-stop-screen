package ua.pasinfosc.data.repository

import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ua.pasinfosc.data.network.BusesService
import ua.pasinfosc.domain.entities.Bus
import ua.pasinfosc.domain.repositories.BusesRepository

class BusesRepositoryImpl : BusesRepository, KoinComponent {

    private val busesService: BusesService by inject()

    override suspend fun getBuses(): MutableList<Bus.Data> {
        val json = JSONObject(busesService.getBuses())
        val buses = mutableListOf<Bus.Data>()
        json.keys().forEach { key ->
            val route = json.getJSONObject(key)
            route.keys().forEach { busKey ->
                if (busKey != "stops") {
                    val bus = route.getJSONObject(busKey)
                    buses += Bus.Data(
                        name = bus.getString("name"),
                        time = try {
                            bus.getLong("time")
                        } catch (e: JSONException) {
                            null
                        },
                        segId = try {
                            bus.getInt("segId")
                        } catch (e: JSONException) {
                            null
                        },
                        pos = try {
                            bus.getJSONArray("pos").let {
                                LatLng(
                                    it.getDouble(1),
                                    it.getDouble(0),
                                )
                            }
                        } catch (e: JSONException) {
                            null
                        },
                        busKey = busKey,
                        routeId = key
                    )
                }
            }
        }
        return buses
    }
}