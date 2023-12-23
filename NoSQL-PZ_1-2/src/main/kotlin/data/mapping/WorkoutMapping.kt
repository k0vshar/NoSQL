package data.mapping

import data.dto.WorkoutDto
import domain.entity.workout.Workout
import java.util.*

fun WorkoutDto.toDomain() = Workout(
    id = this._id,
    date = Date(this.date),
    type = this.type,
    clientId = this.clientId,
)

fun Workout.toDto() = WorkoutDto(
    _id = this.id,
    date = this.date.time,
    type = this.type,
    clientId = this.clientId,
)