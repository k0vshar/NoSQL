package data.dao.impl.mongo

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import data.dao.ClientsDao
import data.utils.Collections
import domain.entity.users.Client
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.Document

class MongoClientsDao(private val db: MongoDatabase) : ClientsDao {

    override suspend fun getById(clientId: String) =
        db.getCollection(Collections.CLIENTS)
            .find(Filters.eq("_id", clientId))
            .first()
            ?.let<Document, Client> {
                Json { ignoreUnknownKeys = true }.decodeFromString(it.toJson())
            }

    override suspend fun create(item: Client) =
        db.getCollection(Collections.CLIENTS)
            .insertOne(Document.parse(Json.encodeToString(item)))
            .wasAcknowledged()

    override suspend fun update(item: Client) =
        db.getCollection(Collections.CLIENTS)
            .updateOne(
                Filters.eq("_id", item.id),
                Updates.combine(
                    Updates.set("name", item.name),
                    Updates.set("surname", item.surname),
                    Updates.set("login", item.login),
                    Updates.set("password", item.password),
                    Updates.set("phoneNumber", item.phoneNumber),
                    Updates.set("email", item.email),
                    Updates.set("trainerId", item.trainerId),
                )
        ).wasAcknowledged()

    override suspend fun delete(item: Client) =
        db.getCollection(Collections.CLIENTS)
            .deleteOne(Filters.eq("_id", item.id))
            .wasAcknowledged()
}