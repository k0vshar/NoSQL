import data.converters.MongoMySqlConverter
import data.dao.impl.mongo.*
import data.dao.impl.mysql.MySqlTrainersDaoImpl
import data.utils.MongoDbProvider
import domain.entity.users.Trainer
import org.bson.types.ObjectId
import java.util.*

const val COUNT = 5

suspend fun main() {

    val db = MongoDbProvider.provideDb("workout_diary")
    val mongoTrainersDao = MongoTrainersDao(db)
    val mySqlTrainersDao = MySqlTrainersDaoImpl()
    val converter = MongoMySqlConverter(mongoTrainersDao, mySqlTrainersDao)

    val trainers = mutableListOf<Trainer>()
    for (i in 1..COUNT) {
        var trainer = Trainer(
            id = ObjectId(Date().time.toInt() + (Math.random() * 16).toInt(), i).toHexString(),
            name = "name$i",
            surname = "surname$i",
            login = "login$i",
            password = "password$i",
            phoneNumber = "phone$i",
            email = "email$i"
        )
        while (trainers.any { ObjectId(it.id).date.time == ObjectId(trainer.id).date.time }) {
            trainer = Trainer(
                id = ObjectId(Date().time.toInt() + (Math.random() * 16).toInt(), i).toHexString(),
                name = "name$i",
                surname = "surname$i",
                login = "login$i",
                password = "password$i",
                phoneNumber = "phone$i",
                email = "email$i"
            )
        }
        trainers.add(trainer)
    }

//    Migrating from mango to mysql
//    for (trainer in trainers) {
//        mongoTrainersDao.create(trainer)
//    }
//    converter.migrateToMySql()

//    Migrating from mysql to mongo
//    for (trainer in trainers) {
//        mySqlTrainersDao.create(trainer)
//    }
//    converter.migrateToMongo()
}