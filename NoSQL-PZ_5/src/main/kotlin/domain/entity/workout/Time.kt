package domain.entity.workout

import kotlinx.serialization.Serializable

@Serializable
data class Time(
        val minutes: Byte,
        val seconds: Byte,
)
