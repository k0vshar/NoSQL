package data.dao

import domain.entity.users.Client
import domain.entity.users.Trainer

interface TrainersDao : BaseDao<Trainer> {

    suspend fun getAll(): List<Trainer>

    suspend fun getById(id: String): Trainer?

    suspend fun getClientsByTrainerId(trainerId: String): List<Client>

    suspend fun getByNameAndSurname(name: String, surname: String): List<Trainer>

    suspend fun getByName(name: String): List<Trainer>

    suspend fun getBySurname(surname: String): List<Trainer>

    suspend fun getMostPopularName(): String

    suspend fun getMostPopularNameAndSurname(): String
}