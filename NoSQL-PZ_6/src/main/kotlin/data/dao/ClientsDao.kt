package data.dao

import domain.entity.users.Client

interface ClientsDao : BaseDao<Client> {

    suspend fun getById(clientId: String): Client?
}