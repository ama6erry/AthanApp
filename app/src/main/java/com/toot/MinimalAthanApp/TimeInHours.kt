package com.toot.MinimalAthanApp

class TimeInHours(val hours: Int, val minutes: Int, val seconds: Int) {
    override fun toString(): String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}