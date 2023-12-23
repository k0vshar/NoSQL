import data.dao.impl.mongo.*
import data.utils.MongoDbProvider
import domain.entity.NAMES
import domain.entity.SURNAMES
import domain.entity.users.Trainer
import org.bson.types.ObjectId
import java.util.*
import kotlin.system.measureTimeMillis

const val COUNT = 100_000

suspend fun main() {

    val db = MongoDbProvider.provideDb("workout_diary")
    val mongoTrainersDao = MongoTrainersDao(db)

    println("Generating...")
    for (i in 1..COUNT) {
        val trainer = Trainer(
            id = ObjectId(Date().time.toInt() + (Math.random() * 16).toInt(), i).toHexString(),
            name = NAMES.random(),
            surname = SURNAMES.random(),
            login = "login$i",
            password = "password$i",
            phoneNumber = "phone$i",
            email = "email$i"
        )
        mongoTrainersDao.create(trainer)
    }

    var trainers: List<Trainer>
    var name: String

    var time: Long = measureTimeMillis {
        trainers = mongoTrainersDao.getByName("Анна")
    }
    println("Searching by name using aggregations: time = ${time.toDouble() / 1000}s, result: ${trainers.size} items")

    time = measureTimeMillis {
        trainers = mongoTrainersDao.getAll().filter { it.name == "Анна" }
    }
    println("Searching by name programmatically: time = ${time.toDouble() / 1000}s, result: ${trainers.size} items")

    time = measureTimeMillis {
        trainers = mongoTrainersDao.getBySurname("Федоров")
    }
    println("Searching by surname using aggregations: time = ${time.toDouble() / 1000}s, result: ${trainers.size} items")

    time = measureTimeMillis {
        trainers = mongoTrainersDao.getAll().filter { it.surname == "Федоров" }
    }
    println("Searching by surname programmatically: time = ${time.toDouble() / 1000}s, result: ${trainers.size} items")

    time = measureTimeMillis {
        name = mongoTrainersDao.getMostPopularName()
    }
    println("Getting the most popular name using aggregations: time = ${time.toDouble() / 1000}s, result: $name")

    time = measureTimeMillis {
        name = mongoTrainersDao.getAll()
            .groupBy { it.name }
            .maxByOrNull { it.value.size }
            ?.value
            ?.first()
            ?.name
            ?: ""
    }
    println("Getting the most popular name programmatically: time = ${time.toDouble() / 1000}s, result: $name")

    time = measureTimeMillis {
        name = mongoTrainersDao.getMostPopularNameAndSurname()
    }
    println("Getting the most popular name and surname using aggregations: time = ${time.toDouble() / 1000}s, result: $name")

    time = measureTimeMillis {
        name = mongoTrainersDao.getAll()
            .groupBy { "${it.name} ${it.surname}" }
            .maxByOrNull { it.value.count() }
            ?.value
            ?.first()
            ?.let { "${it.name} ${it.surname}" }
            ?: ""
    }
    println("Getting the most popular name and surname programmatically: time = ${time.toDouble() / 1000}s, result: $name")
}