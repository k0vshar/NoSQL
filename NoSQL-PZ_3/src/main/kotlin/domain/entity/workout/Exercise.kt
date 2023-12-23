package domain.entity.workout

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Exercise(
    @SerialName("_id")
    val id: String,
    val name: String,
    val sets: Byte,
    val repetitionsFrom: Short,
    val repetitionsTo: Short,
    val weightFrom: Short?,
    val weightTo: Short?,
    val time: Time?,
    val notes: String? = null,
    val workoutId: String,
)