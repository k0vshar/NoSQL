package data.dao

import domain.entity.users.Client
import domain.entity.users.Trainer

interface TrainersDao : BaseDao<Trainer> {

    suspend fun getById(id: String): Trainer?

    suspend fun getClientsByTrainerId(trainerId: String): List<Client>
}