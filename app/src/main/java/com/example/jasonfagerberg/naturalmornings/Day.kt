package com.example.jasonfagerberg.naturalmornings

class Day (
        val name: String,
        var isEnabled: Boolean = true,
        var time: Double? = -1.0,
        var lightActivated: Boolean = true,
        var playSound: Boolean = true,
        var openBlinds: Boolean = true,
        var openWindow: Boolean = true
) {
    override fun toString(): String {
        return "\t\t{" +
                "\n\t\t\t\"name\":\"$name\"," +
                "\n\t\t\t\"isEnabled\":$isEnabled, " +
                "\n\t\t\t\"time\":$time, " +
                "\n\t\t\t\"lightActivated\":$lightActivated, " +
                "\n\t\t\t\"playSound\":$playSound, " +
                "\n\t\t\t\"openBlinds\":$openBlinds, " +
                "\n\t\t\t\"openWindow\":$openWindow" +
                "\n\t\t}"
    }
}