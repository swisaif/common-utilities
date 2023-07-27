package fr.swisaif.common.utilities.network.data.model

data class CellularNetworkData(
    override val connected: Boolean,
    override val ip: String? = null,
    override val airPlaneMode: Boolean
) : NetworkData