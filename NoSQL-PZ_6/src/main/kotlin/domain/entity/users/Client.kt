package domain.entity.users

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Client(
    @SerialName("_id")
    val id: String,
    val name: String,
    val surname: String,
    val login: String,
    val password: String?,
    val phoneNumber: String?,
    val email: String,
    val trainerId: String? = null
)