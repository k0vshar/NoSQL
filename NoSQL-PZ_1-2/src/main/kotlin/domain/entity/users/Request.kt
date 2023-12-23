package domain.entity.users

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Request(
        @SerialName("_id")
        val id: String,
        val trainerId: String,
        val clientId: String,
        val status: RequestStatus = RequestStatus.UNCHECKED,
)