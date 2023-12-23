package data.utils

import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

object DBService {

    fun closeConnection(connection: Connection?) {
        try {
            connection?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeStatement(statement: Statement?) {
        try {
            statement?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeResultSet(resultSet: ResultSet?) {
        try {
            resultSet?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}