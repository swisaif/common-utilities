package fr.swisaif.common.utilities.data.storage.datasource

import fr.swisaif.common.utilities.data.datasource.DataSource
import io.reactivex.Observable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

interface PreferenceDataSource : DataSource<Any> {

    companion object {
        const val DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ssZ"
    }

    /**
     * Récupère le String associé à la key, sinon defaultValue sera renvoyée
     *
     * @param key          clé
     * @param defaultValue valeur par défaut si null
     * @return String ou null
     */
    fun getString(key: String, defaultValue: String?): String?

    /**
     * Récupère le Set<String> associé à la key, sinon defaultValue sera renvoyée
     *
     * @param key          clé
     * @param defaultValue valeur par défaut si null
     * @return Set<String> ou null
     */
    fun getStringSet(key: String, defaultValue: Set<String>?): Set<String>?

    /**
     * Récupère le boolean associé à la key, sinon defaultValue sera renvoyée
     *
     * @param key          clé
     * @param defaultValue valeur par défaut si null
     * @return boolean
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    /**
     * Récupère le int associé à la key, sinon defaultValue sera renvoyée
     *
     * @param key          clé
     * @param defaultValue valeur par défaut si non trouvée
     * @return int
     */
    fun getInt(key: String, defaultValue: Int): Int

    /**
     * Récupère le long associé à la key, sinon defaultValue sera renvoyée
     *
     * @param key          clé
     * @param defaultValue valeur par défaut si non trouvée
     * @return long
     */
    fun getLong(key: String, defaultValue: Long): Long

    /**
     * Récupère le float associé à la key, sinon defaultValue sera renvoyée
     *
     * @param key          clé
     * @param defaultValue valeur par défaut si non trouvée
     * @return float
     */
    fun getFloat(key: String, defaultValue: Float): Float

    /**
     * Récupère la date associée à la key, sinon defaultValue sera renvoyée
     * avec le format [DATE_FORMAT_ISO_8601]
     *
     * @param key          clé
     * @param defaultValue valeur par défaut si non trouvée
     * @return Date
     */
    fun getDate(key: String, defaultValue: Date?): Date?

    /**
     * Récupère la LocalDate associée à la key, sinon defaultValue sera renvoyée
     * avec le format [DateTimeFormatter.ISO_DATE]
     *
     * @param key          clé
     * @param defaultValue valeur par défaut si non trouvée
     * @return LocalDate
     */
    fun getLocalDate(key: String, defaultValue: LocalDate?): LocalDate?

    /**
     * Récupère la LocalDateTime associée à la key, sinon defaultValue sera renvoyée
     * avec le format [DateTimeFormatter.ISO_LOCAL_DATE_TIME]
     *
     * @param key          clé
     * @param defaultValue valeur par défaut si non trouvée
     * @return LocalDateTime
     */
    fun getLocalDateTime(key: String, defaultValue: LocalDateTime?): LocalDateTime?

    /**
     * Récupère la LocalTime associée à la key, sinon defaultValue sera renvoyée
     * avec le format [DateTimeFormatter.ISO_LOCAL_TIME]
     *
     * @param key          clé
     * @param defaultValue valeur par défaut si non trouvée
     * @return LocalTime
     */
    fun getLocalTime(key: String, defaultValue: LocalTime?): LocalTime?

    /**
     * Sauvegarde la value pour la key de manière asynchrone
     *
     * @param key   clé
     * @param value valeur à sauvegarder
     */
    fun save(key: String, value: String)

    /**
     * Sauvegarde la value pour la key de manière asynchrone
     *
     * @param key    clé
     * @param values valeur à sauvegarder
     */
    fun save(key: String, values: Set<String>)

    /**
     * Sauvegarde la value pour la key de manière asynchrone
     *
     * @param key   clé
     * @param value valeur à sauvegarder
     */
    fun save(key: String, value: Boolean)

    /**
     * Sauvegarde la value pour la key de manière asynchrone
     *
     * @param key   clé
     * @param value valeur à sauvegarder
     */
    fun save(key: String, value: Int)

    /**
     * Sauvegarde la value pour la key de manière asynchrone
     *
     * @param key   clé
     * @param value valeur à sauvegarder
     */
    fun save(key: String, value: Long)

    /**
     * Sauvegarde la value pour la key de manière asynchrone
     *
     * @param key   clé
     * @param value valeur à sauvegarder
     */
    fun save(key: String, value: Float)

    /**
     * Sauvegarde la value pour la key de manière asynchrone
     * avec le format [DATE_FORMAT_ISO_8601]
     *
     * @param key   clé
     * @param value valeur à sauvegarder
     */
    fun save(key: String, value: Date)

    /**
     * Sauvegarde la value pour la key de manière asynchrone
     * avec le format [DateTimeFormatter.ISO_DATE]
     *
     * @param key   clé
     * @param value valeur à sauvegarder
     */
    fun save(key: String, value: LocalDate)

    /**
     * Sauvegarde la value pour la key de manière asynchrone
     * avec le format [DateTimeFormatter.ISO_LOCAL_DATE_TIME]
     *
     * @param key   clé
     * @param value valeur à sauvegarder
     */
    fun save(key: String, value: LocalDateTime)

    /**
     * Sauvegarde la value pour la key de manière asynchrone
     * avec le format [DateTimeFormatter.ISO_LOCAL_TIME]
     *
     * @param key   clé
     * @param value valeur à sauvegarder
     */
    fun save(key: String, value: LocalTime)

    /**
     * Suppression de la key de manière asynchrone
     *
     * @param key clé à supprimer
     */
    fun delete(key: String)

    /**
     * True si la key existe
     *
     * @param key clé à chercher
     * @return boolean true si la clé existe
     */
    operator fun contains(key: String): Boolean

    /**
     * Récupère toutes les clés/valeurs sauvegardées dans les preferences
     *
     * @return Map<String, ?> Map de Key / value
     */
    fun findAll(): Map<String, *>

    /**
     * Permet d'écouter les changements d'état pour les préférences.
     *
     * @return Observable<String> Elle emet les key qui sont modifiées de manière réactive
     */
    fun onPreferenceChange(): Observable<String>
}