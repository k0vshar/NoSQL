package data.dao.impl.mongo

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.*
import data.dao.CompletedSetsDao
import data.utils.Collections
import domain.entity.workout.CompletedSet
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.conversions.Bson

class MongoCompletedSetsDao(private val db: MongoDatabase) : CompletedSetsDao {

    override suspend fun getByExerciseId(exerciseId: String): List<CompletedSet> {

        val filter = Filters.eq("${Collections.WORKOUTS}.${Collections.EXERCISES}._id", exerciseId)

        return db.getCollection(Collections.CLIENTS)
            .aggregate(listOf(
                Aggregates.match(filter),
                Aggregates.unwind("$${Collections.WORKOUTS}"),
                Aggregates.unwind("$${Collections.WORKOUTS}.${Collections.EXERCISES}"),
                Aggregates.match(filter)
            ))
            .first()
            ?.get(Collections.WORKOUTS, Document::class.java)
            ?.get(Collections.EXERCISES, Document::class.java)
            ?.getList(Collections.COMPLETED_SETS, Document::class.java)
            ?.let { documents ->
                documents.map { Json { ignoreUnknownKeys = true }.decodeFromString(it.toJson()) }
            }
            ?: listOf()
    }

    override suspend fun create(item: CompletedSet): Boolean {

        val filter = Filters.eq("${Collections.WORKOUTS}.${Collections.EXERCISES}._id", item.exerciseId)
        val indices = getIndicesForCreate(filter)

        return if (indices != null) {
            db.getCollection(Collections.CLIENTS)
                .updateOne(
                    filter,
                    Updates.push(
                        "${Collections.WORKOUTS}.${indices[W_IDX]}.${Collections.EXERCISES}.${indices[E_IDX]}.${Collections.COMPLETED_SETS}",
                        Document.parse(Json.encodeToString(item))
                    )
                ).wasAcknowledged()
        } else {
            false
        }
    }

    private fun getIndicesForCreate(filter: Bson) =
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

    override suspend fun update(item: CompletedSet): Boolean {

        val filter = Filters.eq(
            "${Collections.WORKOUTS}.${Collections.EXERCISES}.${Collections.COMPLETED_SETS}._id",
            item.id
        )
        val indices = getIndicesForUpdate(filter)

        return if (indices != null) {
            val completedSet = "${Collections.WORKOUTS}.${indices[W_IDX]}.${Collections.EXERCISES}.${indices[E_IDX]}.${Collections.COMPLETED_SETS}.${indices[CS_IDX]}"
            db.getCollection(Collections.CLIENTS)
                .updateOne(
                    filter,
                    Updates.combine(
                        Updates.set("$completedSet.setNumber", item.setNumber),
                        Updates.set("$completedSet.repetitions", item.repetitions),
                        Updates.set("$completedSet.weight", item.weight),
                        Updates.set("$completedSet.time", Document.parse(Json.encodeToString(item.time))),
                        Updates.set("$completedSet.notes", item.notes),
                        Updates.set("$completedSet.exerciseId", item.exerciseId),
                    )
                ).wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun delete(item: CompletedSet): Boolean {

        val filter = Filters.eq(
            "${Collections.WORKOUTS}.${Collections.EXERCISES}.${Collections.COMPLETED_SETS}._id",
            item.id
        )
        val indices = getIndicesForUpdate(filter)

        return if (indices != null) {
            db.getCollection(Collections.CLIENTS)
                .updateOne(
                    filter,
                    Updates.pull(
                        "${Collections.WORKOUTS}.${indices[W_IDX]}.${Collections.EXERCISES}.${indices[E_IDX]}.${Collections.COMPLETED_SETS}",
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
                Aggregates.unwind(
                    "$${Collections.WORKOUTS}.${Collections.EXERCISES}.${Collections.COMPLETED_SETS}",
                    UnwindOptions().includeArrayIndex(CS_IDX)
                ),
                Aggregates.match(filter),
                Aggregates.project(Projections.include(W_IDX, E_IDX, CS_IDX))
            ))
            .first()
            ?.toMap()

    companion object {
        private const val W_IDX = "workout_index"
        private const val E_IDX = "exercise_index"
        private const val CS_IDX = "completed_set_index"
    }
}