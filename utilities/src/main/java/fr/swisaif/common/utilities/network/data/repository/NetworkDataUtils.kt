package fr.swisaif.common.utilities.network.data.repository

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.provider.Settings
import fr.swisaif.common.utilities.network.data.model.CellularNetworkData
import fr.swisaif.common.utilities.network.data.model.NetworkData
import fr.swisaif.common.utilities.network.data.model.NetworkSignalStrength
import fr.swisaif.common.utilities.network.data.model.NoNetworkData
import fr.swisaif.common.utilities.network.data.model.WifiNetworkData
import timber.log.Timber
import java.math.BigInteger
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.UnknownHostException
import java.nio.ByteOrder

/**
 * Interface pour les utilitaires de création d'objets [NetworkData].
 * @param N le type d'objet [NetworkData] à créer
 */
abstract class NetworkDataUtils<N : NetworkData> {
    /**
     * Crée un objet [N] à partir du contexte fourni.
     * @param context le contexte à utiliser pour créer l'objet [N]
     * @return un objet [N] créé à partir du contexte fourni
     */
    abstract fun createFrom(context: Context): N

    /**
     * Méthode pour savoir si le mode avion est activé ou non.
     *
     * @return true si le mode avion est activé sinon false
     */
    protected fun isAirplaneModeOn(context: Context): Boolean =
        Settings.System.getInt(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON,
            0
        ) != 0
}

/**
 * Utilitaires pour créer des objets [WifiNetworkData].
 */
internal object WifiNetworkDataUtils : NetworkDataUtils<WifiNetworkData>() {
    /**
     * Crée un objet [WifiNetworkData] à partir du contexte fourni.
     *
     * @param context le contexte à utiliser pour créer l'objet [WifiNetworkData]
     * @return un objet [WifiNetworkData] créé à partir du contexte fourni
     */
    override fun createFrom(context: Context) =
        (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).run {
            WifiNetworkData(
                ssid = connectionInfo.ssid,
                qualitySignal = connectionInfo.getNetworkQualityStatus(),
                ip = connectionInfo.getFormattedIp(),
                macAddress = connectionInfo.macAddress,
                airPlaneMode = isAirplaneModeOn(context)
            )
        }

    /**
     * Obtient l'adresse IP du point d'accès Wi-Fi sous forme de chaîne formatée.
     *
     * @return l'adresse IP du point d'accès Wi-Fi sous forme de chaîne formatée, ou null si elle est inaccessible.
     */
    private fun WifiInfo.getFormattedIp(): String? = try {
        InetAddress.getByAddress(
            // Convert little-endian to big-endian if needed
            BigInteger.valueOf(
                if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                    Integer.reverseBytes(ipAddress)
                } else {
                    ipAddress
                }.toLong()
            ).toByteArray()
        ).hostAddress
    } catch (e: UnknownHostException) {
        Timber.e(e, "Unable to get host address.")
        null
    }

    /**
     * Obtient la force de signal du réseau Wi-Fi.
     *
     * @return la force de signal du réseau Wi-Fi, sous forme d'énumération [NetworkSignalStrength].
     */
    private fun WifiInfo.getNetworkQualityStatus() = NetworkSignalStrength.fromLevel(
        WifiManager.calculateSignalLevel(rssi, NetworkSignalStrength.values().size)
    )
}

/**
 * Utilitaires pour créer des objets [CellularNetworkData].
 */
internal object CellularNetworkDataUtils : NetworkDataUtils<CellularNetworkData>() {
    /**
     * Crée un objet [CellularNetworkData] à partir du contexte fourni.
     *
     * @param context le contexte à utiliser pour créer l'objet [CellularNetworkData]
     * @return un objet [CellularNetworkData] créé à partir du contexte fourni
     */
    override fun createFrom(context: Context) =
        CellularNetworkData(
            connected = true,
            ip = getMobileIP(),
            airPlaneMode = isAirplaneModeOn(context)
        )

    /**
     * Obtient l'adresse IP du réseau mobile.
     *
     * @return l'adresse IP du réseau mobile sous forme de chaîne formatée, ou null si elle est inaccessible.
     */
    private fun getMobileIP(): String? = NetworkInterface.getNetworkInterfaces()
        .toList()
        .map { it.inetAddresses.toList() }
        .flatten()
        .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }?.hostAddress
}

internal object NoNetworkDataUtils : NetworkDataUtils<NoNetworkData>() {

    override fun createFrom(context: Context): NoNetworkData {
        return NoNetworkData(airPlaneMode = isAirplaneModeOn(context))
    }

}