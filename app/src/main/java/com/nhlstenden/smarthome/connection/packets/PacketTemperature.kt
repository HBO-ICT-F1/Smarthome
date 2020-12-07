package com.nhlstenden.smarthome.connection.packets

import com.nhlstenden.smarthome.connection.Packet

/**
 * Packet used for sharing data such as room temperature and humidity with the client
 *
 * @author Robert
 * @since 1.0
 */
data class PacketRoomData(val temperature: Float, val humidity: Float) : Packet("RoomData")