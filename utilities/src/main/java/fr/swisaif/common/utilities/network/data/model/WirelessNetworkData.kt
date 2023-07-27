package fr.swisaif.common.utilities.network.data.model

interface WirelessNetworkData : NetworkData {
    val qualitySignal: NetworkSignalStrength?
}