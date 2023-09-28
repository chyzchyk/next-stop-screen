package ua.pasinfosc.data.network

import retrofit2.http.*

interface BusesService {
    @GET("vehs/5")
    suspend fun getBuses(): String
}