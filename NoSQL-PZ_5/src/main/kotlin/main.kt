import data.dao.impl.mongo.*
import data.utils.MongoDbProvider
import domain.entity.users.Trainer
import org.bson.types.ObjectId
import java.util.*

const val COUNT = 10_000

suspend fun main() {

    val db = MongoDbProvider.provideDb("workout_diary")
    println(db.writeConcern)
    val mongoTrainersDao = MongoTrainersDao(db)

    println("Generating...")
    val trainers = mutableListOf<Trainer>()
    for (i in 1..COUNT) {
        val trainer = Trainer(
            id = ObjectId(Date().time.toInt() + (Math.random() * 16).toInt(), i).toHexString(),
            name = "name$i",
            surname = "surname$i",
            login = "login$i",
            password = "password$i",
            phoneNumber = "phone$i",
            email = "email$i"
        )
        trainers.add(trainer)
    }

    println("Creating...")
    for (trainer in trainers) {
        var isSuccessful = true
        for (i in 1..3) {
            try {
                mongoTrainersDao.create(trainer)
                break
            } catch (e: Exception) {
                println(e.message)
                if (i == 3) {
                    isSuccessful = false
                    break
                }
                Thread.sleep(1000)
            }
        }
        if (!isSuccessful) {
            println("An error has been occurred while writing...")
            break
        }
    }

    val allTrainers = mongoTrainersDao.getAll()
    println("Total documents: ${allTrainers.size}")
}