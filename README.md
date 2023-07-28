# common-utilities

 Notre bibliothèque est une collection complète d'outils et de fonctionnalités conçue pour faciliter le développement d'applications métier sur Android. Elle regroupe un ensemble de fonctionnalités couramment utilisées, vous permettant de vous concentrer sur l'implémentation de la logique métier spécifique à votre application sans vous soucier de la réinvention de la roue pour chaque nouveau projet.

Gestion du Réseau : La bibliothèque contient des outils robustes pour la gestion de la partie réseau, y compris les appels API et le traitement des erreurs réseau. Cela simplifie la tâche de la mise en œuvre de la communication réseau dans vos applications.

Fonctionnalités pour la couche Repository : Nous fournissons des interfaces et des classes de base pour la mise en œuvre du pattern Repository. Cela vous permet d'implémenter une architecture propre et d'isoler la logique de récupération de données de votre application.

Fonctionnalités pour la couche Domaine : Notre bibliothèque contient des outils pour la gestion de la logique métier, y compris la définition des cas d'utilisation et la gestion des entités.

Gestion de ViewModel : Nous fournissons des classes de base et des helpers pour la mise en place du pattern ViewModel. Cela facilite la gestion de l'état de l'interface utilisateur et des données dans vos applications, en maintenant une séparation claire entre la logique de l'interface utilisateur et le reste de votre application.

Classes utilitaires : Nous fournissons également des classes utilitaires pour le traitement du JSON et la gestion des préférences de l'application, rendant le stockage et la récupération des données utilisateur plus simples et plus efficaces.

En utilisant notre bibliothèque, vous pouvez accélérer le développement de votre application métier, réduire les bugs et améliorer la qualité globale de votre code.

## Repository

Afin de garder quelque chose d'homogène sur les repository, un ensemble d'interface est mis à disposition pour que vous puissiez composer vos repository en fonction des besoin.repository

Un repository devra donc garder la même entité métier pour chacune des interface.

Voici la liste actuelle :

```kotlin
/**
 * Insertion d'une entité
 */
interface InsertOneRepository<E> : Repository<E> {
    fun insert(entity : E) : Single<E>
}

/**
 * Insestion d'un ensemble d'entité
 */
interface InsertAllRepository<E> : Repository<E> {
    fun insert(entities : List<E>) : Single<List<E>>
}

/**
 * Renvoi l'existence d'une entité ou pas
 */
interface ExistRepository<E> : Repository<E> {
    fun exist(): Observable<Boolean>
}

/**
 * Permet de récupérer votre unique entité métier
 */
interface FindRepository<E> : Repository<E> {
    fun find(): Observable<E>
}

/**
 * Le Type I vous permet de choisir ce que vous voulez pour votre recherche, ça peut être un String ou un objet plus complexe
 */
interface FindByIdRepository<E, I> : Repository<E> {
    fun findById(id: I): Observable<E>
}

/**
 * Renvoi toutes les entitées métiers
 */
interface FindAllRepository<E> : Repository<E> {
    fun findAll(): Observable<List<E>>
}

/**
 * Met à jour l'entité métier
 */
interface UpdateOneRepository<E> : Repository<E> {
    fun update(entity: E): Single<E>
}

/**
 * Met à jour toutes les entitées métiers.
 */
interface UpdateAllRepository<E> : Repository<E> {
    fun update(entities: List<E>): Single<List<E>>
}

/**
 * Supprime l'entité métier
 */
interface DeleteOneRepository<E> : Repository<E> {
    fun delete(entity: E): Completable
}

/**
 * Supprime toutes les entitées métiers
 */
interface DeleteAllRepository<E> : Repository<E> {
    fun deleteAll(): Completable
}
```
## Use Case

Afin de garder quelque chose d'homogène sur les usecases génériques, un ensemble d'interface est mis à disposition pour que vous puissiez avoir une base d'usecases générique.

Si un de vos usecase spécifique à besoin de plusieurs repository, alors il devient un agréga d'usecase. Donc il faudrait injecter les usescase "unitaire" de ces repo (les findUseCase par exemple etc..)

```kotlin
/**
 * Permet d'insérer une entité dans un repository
 */
interface InsertOneUseCase<E> : UseCase {
    operator fun invoke(entity: E): Single<E>
}

/**
 * Permet d'insérer une entité dans un repository
 */
interface InsertOneUseCase<E> : UseCase {
    operator fun invoke(entity: E): Single<E>
}
```

# Observable 1er Abonnement / dernier désabonnement

**Si vous n'avez pas besoin de ce pattern, alors pas besoin de l'utiliser**

Rx expose plusieurs types de sources d'observable cependant ils ne nous permettent pas de bien gérer les actions à faire lors d'un premier abonnement et lors du dernier désabonnement. Chose qui nous permettrait de gérer des événements de broadcast Android par exemple où un register doit être fait qu'une seule fois au début, et un unregister une seule fois qu'a la fin.

Des extensions ont donc été rajouté à Observable pour gérer ces cas là.

```kotlin
fun <T> Observable<T>.doOnFirstSubscribe(
    action: (() -> Unit)
): Observable<T>

fun <T> Observable<T>.doOnLastDispose(
    action: (() -> Unit)
): Observable<T>

fun <T> Observable<T>.doOnFirstSubscribeAndLastDispose(
    onFirstSubscribe: (() -> Unit),
    onLastDispose: (() -> Unit)
): Observable<T>
```

#### Exemple avec un Observable de type BehaviorSubject :

```kotlin
class MyClass {
    private val cacheData = BehaviorSubject.create<NetworkData>()
    
    private val observable = cacheData.doOnFirstSubscribeAndLastDispose(
        { ... /*Faire ce que que vous voulez au premier abonnement*/ },
        { ... /*Faire ce que que vous voulez au dernier désabonnement*/ }
    )
    
    fun find(): Observable<NetworkData> = observable
}
```

La différence principale est lorsque vous voudrez renvoyer ce subject dans l'une de vos méthodes en observable, il faudra utiliser l'attribut `observable` et non pas `cacheData`.


# Network
Le repository NetworkRepository permet de manière **réactive** d'écouter les changements d'état du réseau (peu importe wifi ou 4g).

Il ne trigger que s'il y a un changement d'état ou lors du premier abonnement. 
Les n abonnés suivants reçoivent le dernier état connu de la connexion lors de leurs abonnements.

## Intégration
L'interface NetworkRepository déclare une méthode find() qui retourne un Observable contenant un objet de type NetworkData. Pour l'utiliser dans votre projet, vous devez implémenter cette interface ou utiliser une implémentation existante.

Par exemple, si vous avez besoin d'utiliser NetworkRepository dans votre repository métier, voici comment vous pouvez l'intégrer :

 ```kotlin
class MyRepository(private val networkRepository: NetworkRepository) {

    fun getNetworkData(): Observable<NetworkData> {
        return networkRepository.find()
    }
}}
}

```
Vous pouvez ensuite appeler la méthode getNetworkData() de MyRepository pour obtenir un Observable qui émettra des objets NetworkData.

Il est important de noter que l'objet NetworkData peut être l'un des trois types suivants : WifiNetworkData, CellularNetworkData ou NoNetworkData. Pour récupérer un type spécifique, vous pouvez utiliser un cast sur l'objet NetworkData.

Exemple :

```kotlin
private val network = findNetwork() // usecase pour obtenir les informations de réseau

val wifiStatus: LiveData<WifiNetworkData> = network
    .filter { it is WifiNetworkData }
    .map { it as WifiNetworkData }
    .toLiveData()
```
Dans cet exemple, nous castons le résultat en un Observable de type WifiNetworkData. Vous pouvez utiliser la même approche avec les deux autres types de données.
#### La data class

##### NetworkData: 
Cette interface déclare trois propriétés : connected (connecté ou déconnecté), ip (adresse IP), airPlaneMode (Mode avion activé ou non).


##### CellularNetworkData: 
Cette data class implémente l'interface NetworkData et stocke l'état de la connexion cellulaire (connecté ou déconnecté), l'adresse IP et airPlaneMode (Mode avion activé ou non).

##### NoNetworkData: 
Cette data class implémente l'interface NetworkData et stocke l'état de la connexion (déconnecté), l'adresse IP (null) et airPlaneMode (Mode avion activé ou non).

##### NetworkSignalStrength: 
Cette enum représente la force du signal réseau et contient trois valeurs: POOR, GOOD et EXCELLENT. Cette enum fournit une fonction statique fromLevel() qui permet de récupérer la NetworkSignalStrength en fonction d'un niveau de signal.

##### NetworkType: 
Cette enum contient deux valeurs : WIFI et CELLULAR.

##### WifiNetworkData: 
Cette data class implémente l'interface WirelessNetworkData et stocke des informations sur le réseau Wifi, telles que l'ID du réseau, la qualité du signal, l'état de connexion, l'adresse IP, l'adresse MAC et airPlaneMode (Mode avion activé ou non).. Cette data class contient également un constructeur secondaire pour faciliter la création d'une instance à partir de certaines propriétés.

##### WirelessNetworkData: 
Cette interface étend NetworkData et déclare une propriété qualitySignal qui représente la qualité du signal réseau.

