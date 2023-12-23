package data.dao.impl.mongo

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.*
import data.dao.ExercisesDao
import data.utils.Collections
import domain.entity.workout.Exercise
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.conversions.Bson

class MongoExercisesDao(private val db: MongoDatabase) : ExercisesDao {

    override suspend fun getByWorkoutId(workoutId: String): List<Exercise> {

        val filter = Filters.eq("${Collections.WORKOUTS}._id", workoutId)

         return db.getCollection(Collections.CLIENTS)
            .aggregate(listOf(
                Aggregates.match(filter),
                Aggregates.unwind("$${Collections.WORKOUTS}"),
                Aggregates.match(filter)
            ))
            .first()
            ?.get(Collections.WORKOUTS, Document::class.java)
            ?.getList(Collections.EXERCISES, Document::class.java)
            ?.let { documents ->
                documents.map { Json { ignoreUnknownKeys = true }.decodeFromString(it.toJson()) }
            }
            ?: listOf()
    }

    override suspend fun create(item: Exercise): Boolean {

        val filter = Filters.eq("${Collections.WORKOUTS}._id", item.workoutId)
        val indices = getIndexForCreate(filter)

        return if (indices != null) {
            db.getCollection(Collections.CLIENTS)
                .updateOne(
                    filter,
                    Updates.push(
                        "${Collections.WORKOUTS}.${indices[W_IDX]}.${Collections.EXERCISES}",
                        Document.parse(Json.encodeToString(item))
                    )
                ).wasAcknowledged()
        } else {
            false
        }
    }

    private fun getIndexForCreate(filter: Bson) =
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

    override suspend fun update(item: Exercise): Boolean {

        val filter = Filters.eq("${Collections.WORKOUTS}.${Collections.EXERCISES}._id", item.id)
        val indices = getIndicesForUpdate(filter)

        return if (indices != null) {
            val exercise = "${Collections.WORKOUTS}.${indices[W_IDX]}.${Collections.EXERCISES}.${indices[E_IDX]}"
            db.getCollection(Collections.CLIENTS).updateOne(
                filter,
                Updates.combine(
                    Updates.set("$exercise.name", item.name),
                    Updates.set("$exercise.sets", item.sets),
                    Updates.set("$exercise.repetitionsFrom", item.repetitionsFrom),
                    Updates.set("$exercise.repetitionsTo", item.repetitionsTo),
                    Updates.set("$exercise.weightFrom", item.weightFrom),
                    Updates.set("$exercise.weightTo", item.weightTo),
                    Updates.set("$exercise.time", Document.parse(Json.encodeToString(item.time))),
                    Updates.set("$exercise.notes", item.notes),
                    Updates.set("$exercise.workoutId", item.workoutId),
                )
            ).wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun delete(item: Exercise): Boolean {

        val filter = Filters.eq("${Collections.WORKOUTS}.${Collections.EXERCISES}._id", item.id)
        val indices = getIndicesForUpdate(filter)

        return if (indices != null) {
            db.getCollection(Collections.CLIENTS)
                .updateOne(
                    filter,
                    Updates.pull(
                        "${Collections.WORKOUTS}.${indices[W_IDX]}.${Collections.EXERCISES}",
                        Document.parse(Json.encodeToString(item))
                    )
                ).wasAcknowledged()
        } else {
            false
        }
    }

    private fun getIndicesForUpdate(filter: Bson) =
        db.getCollection(Collections.CLIENTS)
            .aggregate(listOf(
                Aggregates.match(filter),
                Aggregates.unwind(
                    "$${Collections.WORKOUTS}",
                    UnwindOptions().includeArrayIndex(W_IDX)
                ),
                Aggregates.unwind(
                    "$${Collections.WORKOUTS}.${Collections.EXERCISES}",
                    UnwindOptions().includeArrayIndex(E_IDX)
                ),
                Aggregates.match(filter),
                Aggregates.project(Projections.include(W_IDX, E_IDX))
            ))
            .first()
            ?.toMap()

    companion object {
        private const val W_IDX = "workout_index"
        private const val E_IDX = "exercise_index"
    }
}