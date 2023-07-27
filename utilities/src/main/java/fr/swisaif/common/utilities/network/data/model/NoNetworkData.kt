package fr.swisaif.common.utilities.network.data.model

data class NoNetworkData(
    override val connected: Boolean = false,
    override val ip: String? = null,
    override val airPlaneMode: Boolean
) : NetworkData