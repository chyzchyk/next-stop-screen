package ua.pasinfosc

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ua.pasinfosc.domain.entities.Bus
import ua.pasinfosc.domain.entities.Route
import ua.pasinfosc.domain.entities.Stop
import ua.pasinfosc.domain.usecases.GetBusesUseCase
import ua.pasinfosc.domain.usecases.GetRoutesUseCase
import ua.pasinfosc.domain.usecases.GetStopRadiusUseCase
import ua.pasinfosc.utils.pasinfoscLog
import ua.pasinfosc.utils.outputFile
import java.io.FileInputStream
import java.io.InputStream

@SuppressLint("StaticFieldLeak")
object BusApi : KoinComponent {

    private const val UPDATE_INTERVAL = 10_000L // ms
    private var CURRENT_STOP_RADIUS = 40f // meters

    private val getBusesUseCase: GetBusesUseCase by inject()
    private val getRoutesUseCase: GetRoutesUseCase by inject()
    private val getStopRadius: GetStopRadiusUseCase by inject()

    private lateinit var busId: String
    private lateinit var context: Context
    private lateinit var listener: (
        finalStopName: String,
        routeId: String,
        stops: List<Stop>,
        endless: Boolean,
        sound: String,
    ) -> Unit

    private var bus: Bus.Data? = null
    private lateinit var routes: List<Route>

    private var currentRouteIndex = 0
    private var lastStop1: Stop? = null
    private var lastStop2: Stop? = null

    private var finalStop: String? = null

    fun init(
        context: Context,
        busId: String,
        listener: (finalStopName: String, routeId: String, stops: List<Stop>, endless: Boolean, sound: String) -> Unit,
    ) {
        this.busId = busId
        this.context = context
        this.listener = listener

        CoroutineScope(Dispatchers.IO).launch {
            CURRENT_STOP_RADIUS = getStopRadius()
            updateBus()
            findRoute()

            while (isActive) try {
                val lastCoordinates = bus?.pos ?: LatLng(0.0, 0.0)
                updateBus()
                pasinfoscLog("Bus coordinates: ${bus?.pos} (last coordinates: $lastCoordinates)")

                if (lastCoordinates != (bus?.pos ?: LatLng(0.0, 0.0))) {
                    val associatedWithFirst = routes[0].stops.also(::pasinfoscLog)
                        .associateWith { stop ->
                            SphericalUtil.computeDistanceBetween(
                                bus?.pos ?: LatLng(0.0, 0.0),
                                stop.latLng
                            )
                        }
                    val associatedWithSecond = routes.getOrNull(1)?.stops?.also(::pasinfoscLog)
                        ?.associateWith { stop ->
                            SphericalUtil.computeDistanceBetween(
                                bus?.pos ?: LatLng(0.0, 0.0),
                                stop.latLng
                            )
                        }

                    proceedStops(associatedWithFirst, 0) { currentStop ->
                        val list = associatedWithFirst.keys.toList()
                        val indexOfCurrent = list.indexOfFirst { it.name == currentStop.name }

                        pasinfoscLog("currentStop 1 $currentStop")
                        pasinfoscLog("indexOfCurrent 1 $indexOfCurrent")

                        if (routes.size == 1 || currentRouteIndex == 0) {
                            routes = routes.mapIndexed { rIndex, route ->
                                if (rIndex != 0) route else {
                                    route.copy(
                                        stops = list.mapIndexed { index, stop ->
                                            stop.copy(
                                                state = when {
                                                    index < indexOfCurrent -> Stop.State.PAST
                                                    index == indexOfCurrent -> Stop.State.CURRENT
                                                    else -> Stop.State.FURTHER
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        val indexOfLast = list.indexOfFirst { it.name == lastStop1?.name }
                        if (indexOfLast > indexOfCurrent) {
                            pasinfoscLog("reversing ($indexOfLast>$indexOfCurrent)")
                            currentRouteIndex = 1
                            finalStop = routes[1].name
                        }

                        lastStop1 = routes[0].stops.find { it.state == Stop.State.CURRENT }
                        withContext(Dispatchers.Main) {
                            pasinfoscLog(
                                "calling listener for $currentRouteIndex (${routes[currentRouteIndex].name}) " +
                                        "route for ${if (currentRouteIndex == 0) lastStop1 else lastStop2}) stop"
                            )
                            listener(
                                routes[currentRouteIndex].name,
                                routes[currentRouteIndex].id.second,
                                routes[currentRouteIndex].stops,
                                routes.size == 1,
                                (if (currentRouteIndex == 0) lastStop1 else null)?.soundStop ?: ""
                            )
                        }
                    }
                    if (associatedWithSecond != null) proceedStops(associatedWithSecond, 1) { currentStop ->
                        val list = associatedWithSecond.keys.toList()
                        val indexOfCurrent = list.indexOfFirst { it.name == currentStop.name }

                        pasinfoscLog("currentStop 2 $currentStop")
                        pasinfoscLog("indexOfCurrent 2 $indexOfCurrent")

                        if (currentRouteIndex == 0 && lastStop1 == null && lastStop2 == null) {
                            currentRouteIndex = 1
                        }
                        if (currentRouteIndex == 1) {
                            routes = routes.mapIndexed { rIndex, route ->
                                if (rIndex != 1) route else {
                                    route.copy(
                                        stops = list.mapIndexed { index, stop ->
                                            stop.copy(
                                                state = when {
                                                    index < indexOfCurrent -> Stop.State.PAST
                                                    index == indexOfCurrent -> Stop.State.CURRENT
                                                    else -> Stop.State.FURTHER
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        val indexOfLast = list.indexOfFirst { it.name == lastStop2?.name }
                        if (indexOfLast > indexOfCurrent) {
                            pasinfoscLog("reversing ($indexOfLast>$indexOfCurrent)")
                            currentRouteIndex = 0
                            finalStop = routes[0].name
                        }

                        lastStop2 = routes[1].stops.find { it.state == Stop.State.CURRENT }

                        withContext(Dispatchers.Main) {
                            pasinfoscLog(
                                "calling listener for $currentRouteIndex (${routes[currentRouteIndex].name}) " +
                                        "route for ${if (currentRouteIndex == 0) lastStop1 else lastStop2}}) stop"
                            )
                            listener(
                                routes[currentRouteIndex].name,
                                routes[currentRouteIndex].id.second,
                                routes[currentRouteIndex].stops,
                                routes.size == 1,
                                (if (currentRouteIndex == 1) lastStop2 else null)?.soundStop
                                    ?: ""
                            )
                        }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                delay(UPDATE_INTERVAL)
                continue
            }
        }
    }

    private suspend fun proceedStops(
        list: Map<Stop, Double>,
        routeId: Int,
        onCurrent: suspend (Stop) -> Unit,
    ) {
        pasinfoscLog("Stop with minimum distance: ${list.minBy { it.value }.key.name} (${list.minBy { it.value }.value}m)")

        if (list.any { it.value <= CURRENT_STOP_RADIUS }) {
            onCurrent(list.minBy { it.value }.key)
        } else {
            val indexOfCurrent =
                routes[currentRouteIndex].stops.indexOfFirst { it.state == Stop.State.CURRENT }
            pasinfoscLog("index of current in next $indexOfCurrent")

            if (indexOfCurrent != -1 && routeId == currentRouteIndex) {
                val indexOfNext: Int

                if (indexOfCurrent == routes[currentRouteIndex].stops.size - 1) {
                    currentRouteIndex = if (currentRouteIndex == 1 || routes.size == 1) 0 else 1
                    routes = routes.mapIndexed { rIndex, route ->
                        if (rIndex != currentRouteIndex) route else
                            route.copy(
                                stops = route.stops.mapIndexed { index, stop ->
                                    stop.copy(
                                        state = if (index == 0) {
                                            Stop.State.NEXT
                                        } else {
                                            Stop.State.FURTHER
                                        }
                                    )
                                },
                            )
                    }
                    lastStop1 = null
                    lastStop2 = null
                    finalStop = routes[currentRouteIndex].name
                    indexOfNext = 0
                } else {
                    indexOfNext = indexOfCurrent + 1
                    routes = routes.mapIndexed { rIndex, route ->
                        if (rIndex != currentRouteIndex) route else
                            route.copy(
                                stops = route.stops.mapIndexed { index, stop ->
                                    stop.copy(
                                        state = when {
                                            index < indexOfNext -> Stop.State.PAST
                                            index == indexOfNext -> Stop.State.NEXT
                                            else -> Stop.State.FURTHER
                                        }
                                    )
                                }
                            )
                    }
                }

                pasinfoscLog("index of next $indexOfNext")
                pasinfoscLog("The next station is ${routes[currentRouteIndex].stops[indexOfNext].name}")

                withContext(Dispatchers.Main) {
                    pasinfoscLog(
                        "calling listener for $currentRouteIndex (${routes[currentRouteIndex].name}) " +
                                "route for $indexOfNext (${routes[currentRouteIndex].stops[indexOfNext]}) stop"
                    )
                    listener(
                        routes[currentRouteIndex].name,
                        routes[currentRouteIndex].id.second,
                        routes[currentRouteIndex].stops,
                        routes.size == 1,
                        routes[currentRouteIndex].stops[indexOfNext].soundNextStop
                    )
                }
            }
        }
    }

    private suspend fun updateBus() {
        bus = getBusesUseCase().find { it.busKey == busId }
    }

    private suspend fun findRoute() {
        val rout: InputStream = withContext(Dispatchers.IO) { FileInputStream(outputFile("r.txt")) }
        routes = getRoutesUseCase(rout)
            .also { pasinfoscLog(it) }
            .filter { it.id.first == bus!!.routeId }
            .also {
                finalStop = it[currentRouteIndex].name
            }
    }
}