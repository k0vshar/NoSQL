package data.dao

import domain.entity.workout.Exercise

interface ExercisesDao : BaseDao<Exercise> {

    suspend fun getByWorkoutId(workoutId: String): List<Exercise>
}