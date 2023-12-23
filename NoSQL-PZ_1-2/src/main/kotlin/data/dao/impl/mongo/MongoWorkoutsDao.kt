package data.dao.impl.mongo

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.*
import data.dao.WorkoutsDao
import data.dto.WorkoutDto
import data.mapping.toDomain
import data.mapping.toDto
import data.utils.Collections
import domain.entity.workout.Workout
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.conversions.Bson

class MongoWorkoutsDao(private val db: MongoDatabase) : WorkoutsDao {

    override suspend fun getByClientId(clientId: String) =
        db.getCollection(Collections.CLIENTS)
            .find(Filters.eq("_id", clientId))
            .projection(Projections.include(Collections.WORKOUTS))
            .first()
            ?.getList(Collections.WORKOUTS, Document::class.java)
            ?.let { documents ->
                documents.map { Json { ignoreUnknownKeys = true }.decodeFromString<WorkoutDto>(it.toJson()) }
            }?.map { it.toDomain() }
            ?: listOf()

    override suspend fun create(item: Workout) =
        db.getCollection(Collections.CLIENTS)
            .updateOne(
                Filters.eq("_id", item.clientId),
                Updates.push(
                    Collections.WORKOUTS,
                    Document.parse(Json.encodeToString(item.toDto()))
                )
            ).wasAcknowledged()

    override suspend fun update(item: Workout): Boolean {

        val filter = Filters.eq("${Collections.WORKOUTS}._id", item.id)
        val index = getWorkoutIndex(filter)

        val dto = item.toDto()
        return if (index != null) {
            val workout = "${Collections.WORKOUTS}.${index[W_IDX]}"
            db.getCollection(Collections.CLIENTS)
                .updateOne(
                    filter,
                    Updates.combine(
                        Updates.set("$workout.type", dto.type),
                        Updates.set("$workout.date", dto.date),
                        Updates.set("$workout.clientId", dto.clientId),
                    )
                )
                .wasAcknowledged()
        } else {
            false
        }
    }

    private fun getWorkoutIndex(filter: Bson) =
        db.getCollection(Collections.CLIENTS)
            .aggregate(listOf(
                Aggregates.match(filter),
                Aggregates.unwind(
                    "$${Collections.WORKOUTS}",
                    UnwindOptions().includeArrayIndex(W_IDX)
                ),
                Aggregates.match(filter),
                Aggregates.project(Projections.include(W_IDX))
            ))
            .first()
            ?.toMap()

    override suspend fun delete(item: Workout) =
        db.getCollection(Collections.CLIENTS)
            .updateOne(
                Filters.eq("${Collections.WORKOUTS}._id", item.id),
                Updates.pull(
                    Collections.WORKOUTS,
                    Document.parse(Json.encodeToString(item.toDto()))
                )
            ).wasAcknowledged()

    companion object {
        private const val W_IDX = "workout_index"
    }
}