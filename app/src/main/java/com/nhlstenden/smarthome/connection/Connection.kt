package com.nhlstenden.smarthome.connection

import androidx.annotation.RequiresPermission
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.nhlstenden.smarthome.utils.Arduino
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import kotlin.concurrent.thread

/** Internet permission required for accessing some functions in this util */
const val INTERNET = "android.permission.INTERNET"

/** Url used for getting the users public ip */
const val MY_IP = "https://api4.my-ip.io/ip"

/**
 * Util for sending requests to an [Arduino]
 *
 * @author Robert
 * @since 1.0
 */
object Connection {
    /** [Gson] instance used for deserializing */
    private val gson by lazy { GsonBuilder().setLenient().create() }

    /**
     * Gets the current public ipv4 address, or null if an exception occurred
     * Sends a request to <a href="https://api4.my-ip.io/ip">my-ip.io</a>
     *
     * @return the clients public ip, or null if not found
     */
    @RequiresPermission(INTERNET)
    fun getIp() = read(MY_IP)

    /** Attempts to pair with the target at [ip]:[port] */
    @RequiresPermission(INTERNET)
    fun pair(ip: String, port: Int) = try {
        // Send request to target
        val contents = read("http://$ip:$port/pair")

        // Parse to PairResponse and return
        gson.fromJson(contents, PairResponse::class.java)
    } catch (e: JsonParseException) {
        // Print error message
        println("Received invalid json while pairing to ($ip:$port)")
        null
    }

    /** Send request to the specified [Arduino] with [endpoint] and invoke [callback] on response */
    @RequiresPermission(INTERNET)
    fun request(arduino: Arduino, endpoint: String = "", callback: (Response) -> Unit) = thread(true) {
        val host = "${arduino.ip}:${arduino.port}"

        try {
            // Read contents from api and check if data was received
            val contents = read("http://$host/$endpoint")

            // Parse to Response
            val response = gson.fromJson(contents, Response::class.java)

            // Invoke callback with received data
            callback(response)
        } catch (e: Exception) {
            // Print error message
            println("Failed to handle request to ($host): ${e.message}")
        }
    }

    /** Reads all contents from the specified url */
    @RequiresPermission(INTERNET)
    private fun read(url: String) = try {
        // Connect to website and read page contents
        URL(url).readText()
    } catch (e: IOException) {
        // Print error message
        println("Failed to read contents from ($url)")
        null
    } catch (e: MalformedURLException) {
        // Print error message
        println("Invalid url ($url)")
        null
    }
}