import data.dao.impl.mongo.*
import data.dao.impl.mysql.MySqlTrainersDaoImpl
import data.utils.MongoDbProvider
import domain.entity.users.Client
import domain.entity.users.Request
import domain.entity.users.RequestStatus
import domain.entity.users.Trainer
import domain.entity.workout.CompletedSet
import domain.entity.workout.Exercise
import domain.entity.workout.Time
import domain.entity.workout.Workout
import org.bson.types.ObjectId
import java.util.*
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

const val COUNT = 10000

suspend fun main() {

    val db = MongoDbProvider.provideDb("workout_diary")
    val mongoTrainersDao = MongoTrainersDao(db)
    val mySqlTrainersDao = MySqlTrainersDaoImpl()

//    println("Generating...")
//    val trainers = mutableListOf<Trainer>()
//    for (i in 1..COUNT) {
//        var trainer = Trainer(
//            id = ObjectId(Date().time.toInt() + (Math.random() * 16).toInt(), i).toHexString(),
//            name = "name$i",
//            surname = "surname$i",
//            login = "login$i",
//            password = "password$i",
//            phoneNumber = "phone$i",
//            email = "email$i"
//        )
//        while (trainers.any { ObjectId(it.id).date.time == ObjectId(trainer.id).date.time }) {
//            trainer = Trainer(
//                id = ObjectId(Date().time.toInt() + (Math.random() * 16).toInt(), i).toHexString(),
//                name = "name$i",
//                surname = "surname$i",
//                login = "login$i",
//                password = "password$i",
//                phoneNumber = "phone$i",
//                email = "email$i"
//            )
//        }
//        trainers.add(trainer)
//    }
//
//    println("Inserting...")
//    val insertTime = measureTimeMillis {
//        for (i in 0 until COUNT) {
//            mongoTrainersDao.create(trainers[i])
//        }
//    }
//
//    println(insertTime.toDouble() / 1000)

    val trainers: List<Trainer>
    val gettingInfoTime = measureTimeMillis {
        trainers = mySqlTrainersDao.getByNameAndSurname("name9666", "surname9666")
    }
    println(gettingInfoTime.toDouble() / 1000)
    println(trainers)
}