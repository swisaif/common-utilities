package fr.swisaif.common.utilities.network.data.model

interface NetworkData {
    val connected: Boolean
    val ip: String?
    val airPlaneMode: Boolean
}