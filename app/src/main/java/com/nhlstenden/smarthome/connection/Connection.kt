package com.nhlstenden.smarthome.connection

import androidx.annotation.RequiresPermission
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nhlstenden.smarthome.utils.RSA
import java.io.BufferedReader
import java.net.Socket
import java.net.URL

/** Internet permission required for accessing some functions in this util */
const val INTERNET = "android.permission.INTERNET"

/** Url used for getting the users public ip */
const val MY_IP = "https://api4.my-ip.io/ip"

/**
 * Connection util for connecting to remote devices, and sending/receiving data
 *
 * @author Robert
 * @since 1.0
 */
class Connection private constructor(private val socket: Socket) {
    private val callbacks = mutableMapOf<PacketType, MutableList<(Packet) -> Unit>>()
    private var rsa: RSA? = null

    /** Gets wether the [Socket] is currently connected */
    val connected get() = socket.isConnected

    companion object {
        /** [Gson] instance for serializing and deserializing [Packet]s */
        private val gson = Gson()

        /**
         * Gets the current public ipv4 address, or null if an exception occurred
         * Sends a request to <a href="https://api4.my-ip.io/ip">my-ip.io</a>
         *
         * @return the clients public ip, or null if not found
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
                // TODO: Fix crashing issue with long messages(eg. large encryption keys)
                val line = reader.readLine() ?: continue
                val json = rsa?.decrypt(line) ?: line

                // Get base packet from json
                val base = gson.fromJson(json, Packet::class.java)
                val type = PacketType.parse(base.type)

                // Check if packet was recognized
                if (type == null) {
                    println("Received invalid packet type: ${base.type}")
                    continue
                }

                // Get actual packet from json
                val packet = gson.fromJson(json, type.clazz)

                // Invoke callbacks
                callbacks[type]?.forEach { it(packet as Packet) }
            } catch (e: JsonSyntaxException) {
                println("Failed to deserialize packet: ${e.message}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Sets the [RSA] instance for encrypting and decrypting packets
     * Set to null to disable
     */
    fun setEncryption(rsa: RSA?) {
        this.rsa = rsa
    }

    /** Sends the specified [packet] to the connected device */
    fun write(packet: Packet) {
        // Serialize packet and send
        val json = gson.toJson(packet)
        val encrypted = rsa?.encrypt(json) ?: json
        write(encrypted)
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
    fun <T : Packet> register(type: PacketType, callback: (T) -> Unit) {
        try {
            // Initialize list of callbacks if not already initialized
            callbacks.putIfAbsent(type, mutableListOf())

            // Save callback
            callbacks[type]!!.add(callback as (Packet) -> Unit)
        } catch (e: ClassCastException) {
            println("Invalid callback: ${e.message}")
        }
    }
}