package ua.pasinfosc.data.repository

import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import ua.pasinfosc.domain.repositories.ConfigRepository
import ua.pasinfosc.utils.outputFile
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class ConfigRepositoryImpl : ConfigRepository, KoinComponent {

    override suspend fun getBusId(): String {
        return withContext(Dispatchers.IO) {
            val file =
                File("${Environment.getExternalStoragePublicDirectory("/pasinfosc")}", "config.txt")
            val reader = BufferedReader(InputStreamReader(file.inputStream(), "UTF-8"))
            var line = reader.readLine()

            println(file)

            var id: String? = null

            while (line != null) {
                val str = line.toString()
                if (str.startsWith("4-")) {
                    id = str.substringAfter("4-").substringBefore("-(")
                    break
                } else line = reader.readLine()
            }

            return@withContext (id ?: "").also(::println)
        }
    }

    override suspend fun getStopRadius(): Float {
        return withContext(Dispatchers.IO) {
            val file =
                File("${Environment.getExternalStoragePublicDirectory("/pasinfosc")}", "config.txt")
            val reader = BufferedReader(InputStreamReader(file.inputStream(), "UTF-8"))
            var line = reader.readLine()

            println(file)

            var radius: String? = null

            while (line != null) {
                val str = line.toString()
                if (str.startsWith("3-")) {
                    radius = str.substringAfter("3-").substringBefore("-(")
                    break
                } else line = reader.readLine()
            }

            return@withContext (radius?.toFloat() ?: 40F).also(::println)
        }
    }

    override fun getBaseUrl(): String {
        val file =
            File("${Environment.getExternalStoragePublicDirectory("/pasinfosc")}", "config.txt")
        val reader = BufferedReader(InputStreamReader(file.inputStream(), "UTF-8"))
        var line = reader.readLine()

        var id: String? = null

        while (line != null) {
            val str = line.toString()
            if (str.startsWith("2-")) {
                id = str.substringAfter("2-").substringBefore("-(")
                break
            } else line = reader.readLine()
        }

        return id ?: ""
    }

    override suspend fun getTimeForAd(): Long {
        return withContext(Dispatchers.IO) {
            val file =
                File("${Environment.getExternalStoragePublicDirectory("/pasinfosc")}", "config.txt")
            val reader = BufferedReader(InputStreamReader(file.inputStream(), "UTF-8"))
            var line = reader.readLine()

            var timeAd: String? = null

            while (line != null) {
                val str = line.toString()
                if (str.startsWith("1-")) {
                    timeAd = str.substringAfter("1-").substringBefore("-(")
                    break
                } else line = reader.readLine()
            }

            return@withContext timeAd?.toInt()?.times(1000L) ?: 15_000L
        }
    }

    override suspend fun getAdFile(): List<Uri> {
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<Uri>()
            val files =
                File("${Environment.getExternalStoragePublicDirectory("/pasinfosc/ad/")}").listFiles()
            files?.forEach { item ->
                list += Uri.fromFile(outputFile("/ad/${item.name}"))
            }
            return@withContext list
        }
    }
}