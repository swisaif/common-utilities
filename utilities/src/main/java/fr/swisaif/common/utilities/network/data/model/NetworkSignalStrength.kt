package fr.swisaif.common.utilities.network.data.model

enum class NetworkSignalStrength(private val level: Int) {
    POOR(0),
    GOOD(1),
    EXCELLENT(2);

    companion object {
        private val signalStrengthByLevel = values().associateBy { it.level }

        @JvmStatic
        fun fromLevel(level: Int): NetworkSignalStrength = signalStrengthByLevel[level]
            ?: throw IllegalArgumentException("$level can't be converted ton NetworkSignalStrength")
    }
}