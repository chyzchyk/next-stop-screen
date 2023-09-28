package ua.pasinfosc.domain.repositories

import com.google.android.gms.maps.model.LatLng
import ua.pasinfosc.domain.entities.Bus

interface BusesRepository {
    suspend fun getBuses(): List<Bus.Data>
}