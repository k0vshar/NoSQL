package data.dao.impl.mysql

import data.dao.TrainersDao
import data.utils.DBService
import data.utils.DbConnector
import data.utils.TrainersTable
import data.utils.toUser
import domain.entity.users.Client
import domain.entity.users.Trainer
import org.bson.types.ObjectId
import java.sql.*

class MySqlTrainersDaoImpl : TrainersDao {

    private var dbConnector: DbConnector = DbConnector

    override suspend fun getAll(): List<Trainer> {

        val users = mutableListOf<Trainer>()

        var connection: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null

        try {

            connection = dbConnector.getConnection()
            statement = connection?.createStatement()
            resultSet = statement?.executeQuery(TrainersTable.GET_ALL)

            while (resultSet?.next() == true) {
                users.add(resultSet.toUser())
            }

        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            DBService.closeResultSet(resultSet)
            DBService.closeStatement(statement)
            DBService.closeConnection(connection)
        }

        return users
    }

    override suspend fun getById(id: String): Trainer? {

        var user: Trainer? = null

        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null

        try {

            connection = dbConnector.getConnection()

            statement = connection?.prepareStatement(TrainersTable.GET_BY_ID)
            statement?.setInt(1, ObjectId(id).date.time.toInt())

            resultSet = statement?.executeQuery()

            if (resultSet?.next() == true) {
                user = resultSet.toUser()
            }

        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            DBService.closeResultSet(resultSet)
            DBService.closeStatement(statement)
            DBService.closeConnection(connection)
        }

        return user
    }

    override suspend fun getClientsByTrainerId(trainerId: String): List<Client> {
        TODO("Not yet implemented")
    }

    override suspend fun getByNameAndSurname(name: String, surname: String): List<Trainer> {

        var users = mutableListOf<Trainer>()

        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null

        try {

            connection = dbConnector.getConnection()

            statement = connection?.prepareStatement(TrainersTable.GET_BY_NAME_AND_SURNAME)
            statement?.setString(1, name)
            statement?.setString(2, surname)

            resultSet = statement?.executeQuery()

            while (resultSet?.next() == true) {
                users.add(resultSet.toUser())
            }

        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            DBService.closeResultSet(resultSet)
            DBService.closeStatement(statement)
            DBService.closeConnection(connection)
        }

        return users
    }

    override suspend fun create(item: Trainer): Boolean {

        if (item.password == null) {
            return false
        }

        var connection: Connection? = null
        var statement: PreparedStatement? = null

        try {

            connection = dbConnector.getConnection()
            statement = connection?.prepareStatement(TrainersTable.SAVE)

            statement?.apply {
                setString(1, item.name)
                setString(2, item.surname)
                setString(3, item.login)
                setString(4, item.password)
                setString(5, item.phoneNumber)
                setString(6, item.email)
                setInt(7, ObjectId(item.id).date.time.toInt())
            }

            return statement?.executeUpdate() == 1

        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            DBService.closeStatement(statement)
            DBService.closeConnection(connection)
        }

        return false
    }

    override suspend fun update(item: Trainer): Boolean {

        if (item.password == null) {
            return false
        }

        var connection: Connection? = null
        var statement: PreparedStatement? = null

        try {

            connection = dbConnector.getConnection()
            statement = connection?.prepareStatement(TrainersTable.UPDATE)

            statement?.apply {
                setString(1, item.name)
                setString(2, item.surname)
                setString(3, item.login)
                setString(4, item.password)
                setString(5, item.phoneNumber)
                setString(6, item.email)
                setInt(7, ObjectId(item.id).date.time.toInt())
            }

            return statement?.executeUpdate() == 1

        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            DBService.closeStatement(statement)
            DBService.closeConnection(connection)
        }

        return false
    }

    override suspend fun delete(item: Trainer): Boolean {

        var connection: Connection? = null
        var statement: PreparedStatement? = null

        try {

            connection = dbConnector.getConnection()
            statement = connection?.prepareStatement(TrainersTable.DELETE)
            statement?.setInt(1, ObjectId(item.id).date.time.toInt())

            return statement?.executeUpdate() == 1

        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            DBService.closeStatement(statement)
            DBService.closeConnection(connection)
        }

        return false
    }
}