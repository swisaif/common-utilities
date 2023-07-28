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
