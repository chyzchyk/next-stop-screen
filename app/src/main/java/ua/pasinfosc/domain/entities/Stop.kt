package ua.pasinfosc.domain.entities

import com.google.android.gms.maps.model.LatLng

data class Stop(
    val name: String,
    val latLng: LatLng,
    val isFinal: Boolean,
    val state: State,
    val soundNextStop: String,
    val soundStop: String
) {

    enum class State {
        FURTHER, NEXT, CURRENT, PAST
    }
}
