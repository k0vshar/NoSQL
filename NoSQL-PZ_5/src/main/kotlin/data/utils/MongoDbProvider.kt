package data.utils

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.WriteConcern
import com.mongodb.client.MongoClients

object MongoDbProvider {

    private val client = MongoClients.create(MongoClientSettings.builder()
        .writeConcern(WriteConcern.MAJORITY)
        .applyConnectionString(ConnectionString(
            "mongodb://127.0.0.1:27001,127.0.0.1:27002,127.0.0.1:27003"
        ))
        .build()
    )

    fun provideDb(dbName: String) = client.getDatabase(dbName)
}