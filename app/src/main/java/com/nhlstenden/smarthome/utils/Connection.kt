package com.nhlstenden.smarthome.utils

import androidx.annotation.RequiresPermission

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
         * Gets the current public ipv4 address, or null if an exception occurred.
         * Sends a request to <a href="https://api4.my-ip.io/ip">my-ip.io</a>
         *
         * @return the clients public ip, or null if not found.
         */
        @RequiresPermission(INTERNET)
        fun getIp() = try {
            java.net.URL("https://api4.my-ip.io/ip").readText()
        } catch (e: Exception) {
            println("Get public ip failed: ${e.message}")
            null
        }
    }
}