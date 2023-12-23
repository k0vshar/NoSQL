package data.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutDto(
    val _id: String,
    val type: String,
    val date: Long,
    val clientId: String,
)