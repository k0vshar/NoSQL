package data.utils

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClients

object MongoDbProvider {

    private val client = MongoClients.create(ConnectionString("mongodb://127.0.0.1:27017"))

    fun provideDb(dbName: String) = client.getDatabase(dbName)
}