package com.nhlstenden.smarthome.connection

/**
 * Base packet class, used for creating separate packets to send and receive data
 *
 * @author Robert
 * @since 1.0
 */
open class Packet protected constructor(val type: String)