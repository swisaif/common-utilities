package fr.swisaif.common.utilities.network.data.model

data class WifiNetworkData(
    val ssid: String? = null,
    override val qualitySignal: NetworkSignalStrength? = null,
    override val connected: Boolean = false,
    override val ip: String? = null,
    val macAddress: String?,
    override val airPlaneMode: Boolean
) : WirelessNetworkData {

    constructor(
        ssid: String,
        qualitySignal: NetworkSignalStrength,
        ip: String?,
        macAddress: String?,
        airPlaneMode: Boolean
    ) : this(
        ssid = ssid,
        qualitySignal = qualitySignal,
        connected = true,
        ip = ip,
        macAddress = macAddress,
        airPlaneMode = airPlaneMode
    )
}

