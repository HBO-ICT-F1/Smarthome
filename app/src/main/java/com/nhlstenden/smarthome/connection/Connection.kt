package com.nhlstenden.smarthome.connection

import androidx.annotation.RequiresPermission
import com.google.gson.Gson
import java.io.BufferedReader
import java.net.Socket
import java.net.URL

/** Internet permission required for accessing some functions in this util */
const val INTERNET = "android.permission.INTERNET"

/** Url used for getting the users public ip */
const val MY_IP = "https://api4.my-ip.io/ip"

/**
 * Connection util for connecting to the remote device, and for other useful functions related to connections
 *
 * @author Robert
 * @since 1.0
 */
class Connection private constructor(private val socket: Socket) {

    companion object {
        /** [Gson] instance for serializing and deserializing [Packet]s */
        private val gson = Gson()

        /**
         * Gets the current public ipv4 address, or null if an exception occurred.
         * Sends a request to <a href="https://api4.my-ip.io/ip">my-ip.io</a>
         *
         * @return the clients public ip, or null if not found.
         */
        @RequiresPermission(INTERNET)
        fun getIp() = try {
            // Connect to website and read page contents
            URL(MY_IP).readText()
        } catch (e: Exception) {
            // Print error message
            println("Failed to get public ip: ${e.message}")
            null
        }

        /**
         * Opens a [Connection] to the remote [ip]:[port] address.
         *
         * @return the connection that was established, or null if an exception occurred.
         */
        @RequiresPermission(INTERNET)
        fun connect(ip: String, port: Int) = try {
            // Open connection to target ip and port
            val socket = Socket(ip, port).apply {
                tcpNoDelay = true
                soTimeout = 0
            }

            // Create connection util and return
            Connection(socket)
        } catch (e: Exception) {
            // Print error message
            println("Failed to connect to $ip:$port: ${e.message}")
            null
        }
    }

    /** Runs the connection input handler, and blocks the current thread */
    fun run() {
        val stream = socket.getInputStream()
        val reader = BufferedReader(stream.reader())

        while (true) {
            try {
                // Read 1 packet
                val line = reader.readLine()
                line ?: continue

                // Get packet from json
                // TODO: Find superclass of packets and handle callbacks
                val packet = gson.fromJson(line, Packet::class.java)
                println("Received packet [$packet][$line]")
            } catch (e: Exception) {
                println("Failed to read packet: ${e.message}")
            }
        }
    }

    /** Sends the specified [packet] to the connected device */
    fun write(packet: Packet) {
        // Serialize packet and send
        write(gson.toJson(packet))
    }

    /** Writes the specified [input] to the connected device */
    private fun write(input: String) {
        // Convert input to byte array for sending
        val bytes = input.toByteArray()
        val stream = socket.getOutputStream()

        // Write bytes and flush
        stream.write(bytes)
        stream.flush()
    }

    /** Registers a [callback] for when a specific [Packet] was received */
    fun <T : Packet> on(callback: (Connection, T) -> Unit) {
        // TODO: Register callback
    }

    /** Registers a [callback] for when a specific [Packet] was received */
    fun <T : Packet> on(callback: (T) -> Unit) {
        // TODO: Register callback
    }
}