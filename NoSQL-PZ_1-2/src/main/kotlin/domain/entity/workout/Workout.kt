package domain.entity.workout

import java.util.Date

data class Workout(
    val id: String,
    val type: String,
    val date: Date,
    val clientId: String,
)