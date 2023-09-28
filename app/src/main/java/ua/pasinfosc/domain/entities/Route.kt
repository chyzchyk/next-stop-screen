package ua.pasinfosc.domain.entities

data class Route(
    val id: Pair<String, String>, //api id to user id
    val name: String,
    val stops: List<Stop>
) {

    override fun toString(): String {
        return "Route(id=${id.second}(${id.first}), name=$name, stops=${stops.map { "${it.name} (${it.latLng})" }})"
    }
}
