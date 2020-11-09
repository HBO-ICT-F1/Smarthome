package com.nhlstenden.smarthome.utils

import androidx.annotation.RequiresPermission
import com.google.gson.Gson
import com.nhlstenden.smarthome.utils.serialized.IP

/** Internet permission required for accessing some functions in this util */
const val INTERNET = "android.permission.INTERNET"

/**
 * Connection util for connecting to the remote device, and for other useful functions related to connections
 *
 * @author Robert
 * @since 1.0
 */
class Connection {

    companion object {

        /**
         * Gets the current public ip, or null if not connected or found.
         * Sends a request to <a href="https://www.my-ip.io/">www.my-ip.io</a>
         */
        @RequiresPermission(INTERNET)
        fun getIp() = try {
            val contents = java.net.URL("https://api.my-ip.io/ip.json").readText()
            Gson().fromJson(contents, IP::class.java).address
        } catch (e: Exception) {
            println("Get public ip failed: ${e.message}")
            null
        }
    }
}