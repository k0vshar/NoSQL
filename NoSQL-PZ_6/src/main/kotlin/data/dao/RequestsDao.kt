package data.dao

import domain.entity.users.Request

interface RequestsDao : BaseDao<Request> {

    suspend fun getByClientId(clientId: String): List<Request>

    suspend fun getByTrainerId(trainerId: String): List<Request>
}