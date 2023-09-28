package ua.pasinfosc.domain.repositories

import android.net.Uri
import java.io.InputStream

interface ConfigRepository {

    suspend fun getBusId(): String
    suspend fun getStopRadius(): Float
    fun getBaseUrl(): String
    suspend fun getTimeForAd(): Long
    suspend fun getAdFile(): List<Uri>
}