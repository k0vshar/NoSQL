import data.dao.impl.mongo.*
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

suspend fun main() {

    val db = MongoDbProvider.provideDb("workout_diary")
    val clientsDao = MongoClientsDao(db)
    val trainersDao = MongoTrainersDao(db)
    val requestsDao = MongoRequestsDao(db)
    val workoutsDao = MongoWorkoutsDao(db)
    val exercisesDao = MongoExercisesDao(db)
    val completedSetsDao = MongoCompletedSetsDao(db)

    val trainer = Trainer(
        id = ObjectId().toHexString(),
        name = "James",
        surname = "Hetfield",
        login = "login1",
        password = "12345678",
        phoneNumber = "0996661313",
        email = "email1"
    )
    trainersDao.run {
        create(trainer)
        println(getById(trainer.id))
        update(trainer.copy(email = "james_666"))
        println(getById(trainer.id))
        println(getClientsByTrainerId(trainer.id))
    }

    val client = Client(
        id = ObjectId().toHexString(),
        name = "Kirk",
        surname = "Hammet",
        login = "kirk",
        password = "12345678",
        phoneNumber = "0996661313",
        email = "email2"
    )
    clientsDao.run {
        create(client)
        println(getById(client.id))
        update(client.copy(name = "Dave", surname = "Mustaine"))
        println(getById(client.id))
    }

    val request = Request(
        id = ObjectId().toHexString(),
        trainerId = trainer.id,
        clientId = client.id
    )
    requestsDao.run {
        create(request)
        println(getByClientId(client.id))
        println(getByTrainerId(trainer.id))
        update(request.copy(status = RequestStatus.CONFIRMED))
        println(getByClientId(client.id))
        println(getByTrainerId(trainer.id))
        delete(request)
    }
    println(trainersDao.getClientsByTrainerId(trainer.id))

    val workout1 = Workout(
        id = ObjectId().toHexString(),
        type = "workout1",
        date = Date(),
        clientId = client.id
    )
    val workout2 = Workout(
        id = ObjectId().toHexString(),
        type = "workout2",
        date = Date(),
        clientId = client.id
    )
    workoutsDao.run {
        create(workout1)
        create(workout2)
        println(getByClientId(client.id))
        update(workout2.copy(type = "new workout 2"))
        println(getByClientId(client.id))
        delete(workout1)
        println(getByClientId(client.id))
    }

    val exercise1 = Exercise(
        id = ObjectId().toHexString(),
        name = "exercise1",
        weightFrom = 10,
        weightTo = 15,
        repetitionsFrom = 14,
        repetitionsTo = 16,
        sets = 3,
        time = Time(0, 45),
        workoutId = workout2.id
    )
    val exercise2 = Exercise(
        id = ObjectId().toHexString(),
        name = "exercise2",
        weightFrom = 10,
        weightTo = 15,
        repetitionsFrom = 14,
        repetitionsTo = 16,
        sets = 3,
        time = Time(0, 45),
        workoutId = workout2.id
    )
    exercisesDao.run {
        create(exercise1)
        create(exercise2)
        println(getByWorkoutId(workout2.id))
        update(exercise1.copy(name = "new exercise 1"))
        println(getByWorkoutId(workout2.id))
        delete(exercise2)
        println(getByWorkoutId(workout2.id))
    }

    val completedSet1 = CompletedSet(
        id = ObjectId().toHexString(),
        setNumber = 1,
        repetitions = 16,
        weight = 16,
        time = Time(0, 42),
        exerciseId = exercise1.id
    )
    val completedSet2 = CompletedSet(
        id = ObjectId().toHexString(),
        setNumber = 1,
        repetitions = 16,
        weight = 16,
        time = Time(0, 42),
        exerciseId = exercise1.id
    )
    completedSetsDao.run {
        create(completedSet1)
        create(completedSet2)
        println(getByExerciseId(exercise1.id))
        update(completedSet2.copy(time = Time(0, 52)))
        println(getByExerciseId(exercise1.id))
        delete(completedSet1)
        println(getByExerciseId(exercise1.id))
    }

    trainersDao.delete(trainer)
    clientsDao.delete(client)

    println(requestsDao.getByClientId(client.id))
    println(clientsDao.getById(client.id))
    println(trainersDao.getById(trainer.id))
    println(workoutsDao.getByClientId(client.id))
}