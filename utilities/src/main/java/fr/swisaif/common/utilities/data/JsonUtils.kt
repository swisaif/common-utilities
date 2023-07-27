package fr.swisaif.common.utilities.data

import android.os.Build
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.buffer
import okio.source
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.nio.file.Paths

/**
 * Exemple pour la gestion des adapters
 * <p>
 * Votre classe qui doit étendre un HashMap ou d'un ArrayList etc... et qui est à la racine du fichier
 * Le MyClassWithExtendHashMap c'est une classe métier qui serait par exemple soit un HashMap
 * soit un ArrayList, etc... et qui corresond au type de la racine du fichier de conf.
 * Par exemple si la racine du fichier de conf c'est un [], alors on aurait
 * MyClassMetier extend ArrayList {} avec son Adapter comme cité dans l'exemple en commentaire.
 * De même si c'était un HashMap.
 * <p>
 * public class MyClassWithExtendHashMap extends HashMap<String, MyObject> {
 * public static class MyClassWithExtendHashMapToJsonAdapter {
 *
 * @ToJson public Map<String, MyObject> toJson(MyClassWithExtendHashMap dat) {
 * return (Map<String, MyObject>) dat;
 * }
 * @FromJson public MyClassWithExtendHashMap fromJson(Map<String, MyObject> json) {
 * MyClassWithExtendHashMap result = new MyClassWithExtendHashMap();
 * for (String key : json.keySet())
 * result.put(key, json.get(key));
 * return result;
 * }
 * }
 * }
 * <p>
 * // instanciation de l'adapter pour l'envoyer dans la méthode
 * List<Object> listAdapters = new ArrayList<>();
 * listAdapters.add(new MyClassWithExtendHashMap.MyClassWithExtendHashMapToJsonAdapter());
 */
object JsonUtils {

    /**
     * Convertit un json vers un objet
     *
     * @param content Json
     * @param myClass Classe vers laquelle convertir
     * @param <T>     Type de la class
     * @return Objet converti
     * @throws IOException
     */
    @JvmStatic
    @Throws(IOException::class)
    fun <T> convertJsonToObject(content: String, myClass: Class<T>): T? =
        convertJsonToObject(content, myClass, listOf())

    /**
     * Convertit un json vers un objet
     *
     * @param content      Json
     * @param myClass      Classe vers laquelle convertir
     * @param <T>          Type de la class
     * @param listAdapters List d'adapter si besoin
     * @return Objet converti
     * @throws IOException
     */
    @JvmStatic
    @Throws(IOException::class)
    fun <T> convertJsonToObject(content: String, myClass: Class<T>, listAdapters: List<Any>): T? =
        getJsonAdapterForClass<T>(myClass, listAdapters).fromJson(content)

    /**
     * Convertit un json vers un objet
     *
     * @param content Json
     * @param type    Type de l'objet complexe
     *                (par exemple Type type = Types.newParameterizedType(List.class, Card.class);
     *                pour un objet de type List<Card>
     * @param <T>     Type de la class
     * @return Objet converti
     * @throws IOException
     */
    @JvmStatic
    @Throws(IOException::class)
    fun <T> convertJsonToObject(content: String, type: Type): T? =
        convertJsonToObject(content, type, listOf())

    /**
     * Convertit un json vers un objet
     *
     * @param content      Json
     * @param type         Type de l'objet complexe
     *                     (par exemple Type type = Types.newParameterizedType(List.class, Card.class);
     *                     pour un objet de type List<Card>
     * @param listAdapters List d'adapter si besoin
     * @return Objet converti
     * @throws IOException
     */
    @JvmStatic
    @Throws(IOException::class)
    fun <T> convertJsonToObject(content: String, type: Type, listAdapters: List<Any>): T? =
        getJsonAdapterForType<T>(type, listAdapters).fromJson(content)

    /**
     * Conversion d'un fichier json en liste d'objet
     *
     * @param jsonPath Chemin complet du fichier
     * @param myClass  classe d'objet dans la liste de retour
     * @param <T>      Type de l'objet à convertir
     * @return Liste d'objets issue de la conversion
     * @throws IOException
     */
    @JvmStatic
    @Throws(IOException::class)
    fun <T> convertJsonToObjectList(jsonPath: String, myClass: Class<T>): List<T>? =
        convertJsonToObjectList(jsonPath, myClass, listOf())

    /**
     * Conversion d'un fichier json en liste d'objet
     *
     * @param jsonPath     Chemin complet du fichier
     * @param myClass      classe d'objet dans la liste de retour
     * @param <T>          Type de l'objet à convertir
     * @param listAdapters Liste d'adapter si besoin
     * @return Liste d'objets issue de la conversion
     * @throws IOException
     */
    @JvmStatic
    @Throws(IOException::class)
    fun <T> convertJsonToObjectList(jsonPath: String, myClass: Class<T>, listAdapters: List<Any>): List<T>? =
        getJsonReaderForFile(jsonPath)?.let {
            convertJsonToObjectList(it, myClass, listAdapters)
        }

    /**
     * Conversion d'un jsonReader en liste d'objet
     *
     * @param jsonReader   flux Json contenant les données
     * @param myClass      classe d'objet dans la liste de retour
     * @param <T>          Type de l'objet à convertir
     * @param adapters Liste d'adapter si besoin
     * @return Liste d'objets issue de la conversion
     * @throws IOException
     */
    @JvmStatic
    @Throws(IOException::class)
    private fun <T> convertJsonToObjectList(jsonReader: JsonReader, myClass: Class<T>, adapters: List<Any>): List<T>? =
        createMoshi(adapters)
            .adapter<List<T>>(Types.newParameterizedType(List::class.java, myClass))
            .fromJson(jsonReader)

    /**
     * Convertit un objet en Json
     *
     * @param instance  objet à convertir
     * @param myClass Class de départ
     * @param <T>     Type de l'objet à convertir
     * @return Json
     */
    @JvmStatic
    fun <T> convertObjectToJson(instance: T, myClass: Class<T>): String =
        convertObjectToJson(instance, myClass, listOf())

    /**
     * Convertit un objet en Json
     *
     * @param instance       objet à convertir
     * @param myClass      Class de départ
     * @param listAdapters List d'adapter si besoin
     * @param <T>          Type de l'objet à convertir
     * @return Json
     */
    @JvmStatic
    fun <T> convertObjectToJson(instance: T, myClass: Class<T>, listAdapters: List<Any>): String =
        getJsonAdapterForClass(myClass, listAdapters).toJson(instance)

    /**
     * Convertit un objet en Json
     *
     * @param instance objet à convertir
     * @param type   Type de l'objet complexe
     *               (par exemple Type type = Types.newParameterizedType(List.class, Card.class);
     *               pour un objet de type List<Card>
     * @param <T>    Type de l'objet à convertir
     * @return Json
     */
    @JvmStatic
    fun <T> convertObjectToJson(instance: T, type: Type): String =
        convertObjectToJson(instance, type, listOf())

    /**
     * Convertit un objet en Json
     *
     * @param instance       objet à convertir
     * @param type         Type de l'objet complexe
     *                     (par exemple Type type = Types.newParameterizedType(List.class, Card.class);
     *                     pour un objet de type List<Card>
     * @param listAdapters List d'adapter si besoin
     * @param <T>          Type de l'objet à convertir
     * @return Json
     */
    @JvmStatic
    fun <T> convertObjectToJson(instance: T, type: Type, listAdapters: List<Any>): String =
        getJsonAdapterForType<T>(type, listAdapters).toJson(instance)

    /**
     * Prépare le builder pour la conversion JSON et récupère l'adapter en fonction de la class
     *
     * @param myClass      Class de départ
     * @param adapters List d'adapter si besoin
     * @param <T>          Type de l'objet à convertir
     * @return JsonAdapter pour la conversion
     */
    @JvmStatic
    private fun <T> getJsonAdapterForClass(myClass: Class<T>, adapters: List<Any>) =
        createMoshi(adapters).adapter(myClass)


    /**
     * Prépare le builder pour la conversion JSON et récupère l'adapter en fonction de la class
     *
     * @param type         Type de l'objet complexe
     *                     (par exemple Type type = Types.newParameterizedType(List.class, Card.class);
     *                     pour un objet de type List<Card>
     * @param adapters List d'adapter si besoin
     * @param <T>          Type de l'objet à convertir
     * @return JsonAdapter pour la conversion
     */
    @JvmStatic
    private fun <T> getJsonAdapterForType(type: Type, adapters: List<Any>) =
        createMoshi(adapters).adapter<T>(type)

    /**
     * Crée un JsonReader pour un fichier
     *
     * @param path Chemin complet du fichier
     * @return JsonReader créé
     */
    @JvmStatic
    @Throws(IOException::class)
    private fun getJsonReaderForFile(path: String) =
        // Création du reader en fonction de la version d'Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            JsonReader.of(Paths.get(path).toFile().source().buffer())
        } else {
            JsonReader.of(File(path).source().buffer())
        }

    private fun createMoshi(listAdapters: List<Any>) = Moshi.Builder().apply {
        listAdapters.stream().forEach {
            add(it)
        }
        addLast(KotlinJsonAdapterFactory())
    }.build()
}
