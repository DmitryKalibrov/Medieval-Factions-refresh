package com.dansplugins.factionsystem.team

enum class TeamColor {
    RED,
    BLUE,
    YELLOW;

    companion object {
        fun fromString(value: String?): TeamColor? = when (value?.lowercase()) {
            "red" -> RED
            "blue" -> BLUE
            "yellow" -> YELLOW
            else -> null
        }
    }
}
