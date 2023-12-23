package data.dao

interface BaseDao<T> {

    suspend fun create(item: T): Boolean

    suspend fun update(item: T): Boolean

    suspend fun delete(item: T): Boolean
}