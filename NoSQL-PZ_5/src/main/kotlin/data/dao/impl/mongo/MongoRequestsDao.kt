package data.dao.impl.mongo

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import data.dao.RequestsDao
import data.utils.Collections
import domain.entity.users.Request
import domain.entity.users.RequestStatus
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.Document

class MongoRequestsDao(private val db: MongoDatabase) : RequestsDao {

    override suspend fun getByClientId(clientId: String) =
        db.getCollection(Collections.REQUESTS)
            .find(Filters.eq("clientId", clientId))
            .toList()
            .map { Json { ignoreUnknownKeys = true }.decodeFromString<Request>(it.toJson()) }

    override suspend fun getByTrainerId(trainerId: String) =
        db.getCollection(Collections.REQUESTS)
            .find(Filters.eq("trainerId", trainerId))
            .toList()
            .map { Json { ignoreUnknownKeys = true }.decodeFromString<Request>(it.toJson()) }

    override suspend fun create(item: Request) =
        db.getCollection(Collections.REQUESTS)
            .insertOne(Document.parse(Json.encodeToString(item.copy(status = RequestStatus.UNCHECKED))))
            .wasAcknowledged()

    override suspend fun update(item: Request): Boolean {

        val updateResult = db.getCollection(Collections.REQUESTS)
            .replaceOne(
                Filters.eq("_id", item.id),
                Document.parse(Json.encodeToString(item))
            ).wasAcknowledged()

        if (updateResult) {
            if (item.status == RequestStatus.CONFIRMED) {
                return db.getCollection(Collections.CLIENTS)
                    .updateOne(
                        Filters.eq("_id", item.clientId),
                        Updates.set("trainerId", item.trainerId)
                    ).wasAcknowledged()

            } else {
                return updateResult
            }
        }

        return false
    }

    override suspend fun delete(item: Request) =
        db.getCollection(Collections.REQUESTS)
            .deleteOne(Filters.eq("_id", item.id))
            .wasAcknowledged()
}