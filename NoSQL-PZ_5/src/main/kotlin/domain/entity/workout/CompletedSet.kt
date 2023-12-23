package domain.entity.workout

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompletedSet(
        @SerialName("_id")
        val id: String,
        val setNumber: Byte,
        val repetitions: Short,
        val weight: Short?,
        val time: Time?,
        val notes: String? = null,
        val exerciseId: String,
)