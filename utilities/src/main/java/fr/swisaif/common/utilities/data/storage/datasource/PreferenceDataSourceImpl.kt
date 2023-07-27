package fr.swisaif.common.utilities.data.storage.datasource

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import fr.swisaif.common.utilities.data.subject.doOnFirstSubscribeAndLastDispose
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class PreferenceDataSourceImpl(context: Context) : PreferenceDataSource {

    // Utilisation de la lib preference androidx lorsque common sera en sdk 31
    private val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
    private val eventSubject = PublishSubject.create<String>()
    private val onPreferenceChangeObservable = eventSubject
        .doOnFirstSubscribeAndLastDispose(
            onFirstSubscribe = {
                sharedPreference.registerOnSharedPreferenceChangeListener(listener.get())
            },
            onLastDispose = {
                sharedPreference.unregisterOnSharedPreferenceChangeListener(listener.get())
            }
        )

    private val listener: WeakReference<SharedPreferences.OnSharedPreferenceChangeListener> =
        WeakReference(SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            eventSubject.onNext(key)
        })

    private fun editor(): SharedPreferences.Editor = sharedPreference.edit()

    override fun getString(key: String, defaultValue: String?): String? =
        sharedPreference.getString(key, defaultValue)

    override fun getStringSet(key: String, defaultValue: Set<String>?): Set<String>? =
        sharedPreference.getStringSet(key, defaultValue)

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        sharedPreference.getBoolean(key, defaultValue)

    override fun getInt(key: String, defaultValue: Int): Int =
        sharedPreference.getInt(key, defaultValue)

    override fun getLong(key: String, defaultValue: Long): Long =
        sharedPreference.getLong(key, defaultValue)

    override fun getFloat(key: String, defaultValue: Float): Float =
        sharedPreference.getFloat(key, defaultValue)

    override fun getDate(key: String, defaultValue: Date?): Date? {
        val stringDate = sharedPreference.getString(key, null)
        return if (stringDate.isNullOrEmpty()) {
            defaultValue
        } else {
            SimpleDateFormat(PreferenceDataSource.DATE_FORMAT_ISO_8601, Locale.FRANCE)
                .parse(stringDate)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getLocalDate(key: String, defaultValue: LocalDate?): LocalDate? {
        val stringDate = sharedPreference.getString(key, null)
        return if (stringDate.isNullOrEmpty()) {
            defaultValue
        } else {
            LocalDate.parse(stringDate, DateTimeFormatter.ISO_DATE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getLocalDateTime(key: String, defaultValue: LocalDateTime?): LocalDateTime? {
        val stringDate = sharedPreference.getString(key, null)
        return if (stringDate.isNullOrEmpty()) {
            defaultValue
        } else {
            LocalDateTime.parse(stringDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getLocalTime(key: String, defaultValue: LocalTime?): LocalTime? {
        val stringDate = sharedPreference.getString(key, null)
        return if (stringDate.isNullOrEmpty()) {
            defaultValue
        } else {
            LocalTime.parse(stringDate, DateTimeFormatter.ISO_LOCAL_TIME)
        }
    }

    override fun save(key: String, value: String) = editor().putString(key, value).apply()

    override fun save(key: String, values: Set<String>) = editor().putStringSet(key, values).apply()

    override fun save(key: String, value: Boolean) = editor().putBoolean(key, value).apply()

    override fun save(key: String, value: Int) = editor().putInt(key, value).apply()

    override fun save(key: String, value: Long) = editor().putLong(key, value).apply()

    override fun save(key: String, value: Float) = editor().putFloat(key, value).apply()

    override fun save(key: String, value: Date) {
        val stringDate = SimpleDateFormat(PreferenceDataSource.DATE_FORMAT_ISO_8601, Locale.FRANCE)
            .format(value)
        editor().putString(key, stringDate).apply()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun save(key: String, value: LocalDate) = editor()
        .putString(key, value.format(DateTimeFormatter.ISO_LOCAL_DATE))
        .apply()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun save(key: String, value: LocalDateTime) = editor()
        .putString(key, value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        .apply()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun save(key: String, value: LocalTime) = editor()
        .putString(key, value.format(DateTimeFormatter.ISO_LOCAL_TIME))
        .apply()

    override fun delete(key: String) = editor().remove(key).apply()

    override fun contains(key: String): Boolean = sharedPreference.contains(key)

    override fun findAll(): Map<String, *> = sharedPreference.all

    override fun onPreferenceChange(): Observable<String> = onPreferenceChangeObservable
}