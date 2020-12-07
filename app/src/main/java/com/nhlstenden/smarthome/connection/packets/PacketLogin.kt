package com.nhlstenden.smarthome.connection.packets

import com.nhlstenden.smarthome.connection.Packet

/**
 * Packet used for logging in and doing key exchange for encryption
 *
 * @author Robert
 * @since 1.0
 */
data class PacketLogin(val key: String) : Packet("Login")