package data.dao.impl.mongo

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import data.dao.TrainersDao
import data.utils.Collections
import domain.entity.users.Client
import domain.entity.users.Trainer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.Document

class MongoTrainersDao(private val db: MongoDatabase) : TrainersDao {

    override suspend fun getAll() =
        db.getCollection(Collections.TRAINERS)
            .find()
            .toList()
            .map { Json.decodeFromString<Trainer>(it.toJson()) }

    override suspend fun getById(id: String) =
        db.getCollection(Collections.TRAINERS)
            .find(Filters.eq("_id", id))
            .first()
            ?.let { Json { ignoreUnknownKeys = true }.decodeFromString<Trainer>(it.toJson()) }

    override suspend fun getClientsByTrainerId(trainerId: String) =
        db.getCollection(Collections.CLIENTS)
            .find(Filters.eq("trainerId", trainerId))
            .toList()
            .map { Json.decodeFromString<Client>(it.toJson()) }

    override suspend fun getByNameAndSurname(name: String, surname: String) =
        db.getCollection(Collections.TRAINERS)
            .find(Filters.and(
                Filters.eq("name", name),
                Filters.eq("surname", surname)
            ))
            .toList()
            .map { Json.decodeFromString<Trainer>(it.toJson()) }

    override suspend fun create(item: Trainer) =
        db.getCollection(Collections.TRAINERS)
            .insertOne(Document.parse(Json.encodeToString(item)))
            .wasAcknowledged()

    override suspend fun update(item: Trainer) =
        db.getCollection(Collections.TRAINERS)
            .replaceOne(
                Filters.eq("_id", item.id),
                Document.parse(Json.encodeToString(item))
            ).wasAcknowledged()

    override suspend fun delete(item: Trainer): Boolean {

        val deleteResult = db.getCollection(Collections.TRAINERS)
            .deleteOne(Filters.eq("_id", item.id))
            .wasAcknowledged()

        if (deleteResult) {
            return db.getCollection(Collections.CLIENTS)
                .updateMany(
                    Filters.eq("trainerId", item.id),
                    Updates.set("trainerId", null)
                ).wasAcknowledged()
        }

        return false
    }
}