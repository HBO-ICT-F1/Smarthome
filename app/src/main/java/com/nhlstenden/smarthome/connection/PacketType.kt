package com.nhlstenden.smarthome.connection

import com.nhlstenden.smarthome.connection.packets.PacketLogin
import com.nhlstenden.smarthome.connection.packets.PacketRoomData

/**
 * A collection of all valid [Packet]s to send and/or receive
 *
 * @author Robert
 * @since 1.0
 */
enum class PacketType(val clazz: Class<*>) {
    Login(PacketLogin::class.java), RoomData(PacketRoomData::class.java);

    companion object {

        /** Parses the specified [type] to a [PacketType], or null if not found */
        fun parse(type: String) =
            values().find { it.name.equals(type, true) }
    }
}