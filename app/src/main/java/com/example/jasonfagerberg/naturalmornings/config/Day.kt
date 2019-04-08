package com.example.jasonfagerberg.naturalmornings.config

class Day (
        var isEnabled: Boolean = false,
        var time: Int = 480,
        var lightActivated: Boolean = true,
        var playSound: Boolean = true,
        var openBlinds: Boolean = true,
        var openWindow: Boolean = true
) {
    override fun toString(): String {
        return "{" +
                "\n\t\t\t\"isEnabled\":$isEnabled, " +
                "\n\t\t\t\"time\":$time, " +
                "\n\t\t\t\"lightActivated\":$lightActivated, " +
                "\n\t\t\t\"playSound\":$playSound, " +
                "\n\t\t\t\"openBlinds\":$openBlinds, " +
                "\n\t\t\t\"openWindow\":$openWindow" +
                "\n\t\t}"
    }
}