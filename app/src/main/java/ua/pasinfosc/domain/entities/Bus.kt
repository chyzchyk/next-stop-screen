package ua.pasinfosc.domain.entities

import com.google.android.gms.maps.model.LatLng

data class Bus(
    val id: List<Result>?,
) {
    data class Stops(
        val id: String,
        val idBus: String,
    )

    data class Result(
        val stop: List<Stops>?,
        val id: List<Data>
    )

    data class Data(
        val name: String,
        val time: Long?,
        val segId: Int?,
        val pos: LatLng?,
        val busKey: String,
        val routeId: String
    )
}