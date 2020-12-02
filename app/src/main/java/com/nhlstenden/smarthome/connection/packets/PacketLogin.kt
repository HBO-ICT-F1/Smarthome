package com.nhlstenden.smarthome.connection.packets

import com.nhlstenden.smarthome.connection.Packet

data class PacketLogin(val key: String) : Packet()