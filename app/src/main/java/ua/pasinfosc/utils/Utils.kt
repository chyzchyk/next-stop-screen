package ua.pasinfosc.utils

import android.content.Context
import android.os.Environment
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import java.io.File
import java.io.FileWriter
import java.io.IOException

fun outputFile(name: String): File {
    val mediaStorageDir = File("${Environment.getExternalStoragePublicDirectory("/pasinfosc")}")
    return File(mediaStorageDir.path + File.separator + name)
}

fun hasPermission(context: Context, permission: String): Boolean {
    return checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED
}

fun pasinfoscLog(obj: Any) {
    val appDirectory = File("${Environment.getExternalStoragePublicDirectory("/pasinfosc")}")
    val logDirectory = File("$appDirectory/logs")

    if (!appDirectory.exists()) {
        appDirectory.mkdir()
    }

    if (!logDirectory.exists()) {
        logDirectory.mkdir()
    }

    val file = File("$logDirectory/logs.txt")

    if (!file.exists()) {
        file.createNewFile()
    }

    try {
        val writer = FileWriter(file, true)
        writer.write(obj.toString())
        writer.flush()
        writer.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}