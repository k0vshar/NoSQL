package data.converters

import data.dao.impl.mongo.MongoTrainersDao
import data.dao.impl.mysql.MySqlTrainersDaoImpl

class MongoMySqlConverter(
    private val mongoDao: MongoTrainersDao,
    private val mySqlTrainersDaoImpl: MySqlTrainersDaoImpl
) {

    suspend fun migrateToMySql() {
        val trainers = mongoDao.getAll()
        for (trainer in trainers) {
            mySqlTrainersDaoImpl.create(trainer)
        }
    }

    suspend fun migrateToMongo() {
        val trainers = mySqlTrainersDaoImpl.getAll()
        for (trainer in trainers) {
            mongoDao.create(trainer)
        }
    }
}