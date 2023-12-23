package data.utils

import org.apache.commons.dbcp.BasicDataSource
import java.sql.Connection
import java.util.*

object DbConnector {

    private val properties =  Properties()
    private val datasource = BasicDataSource()

    private var user: String
    private var password: String
    private var url: String

    init {
        properties.load(javaClass.classLoader.getResourceAsStream("application.properties"))
        properties.run {
            user = getProperty("db.mysql.username")
            password = getProperty("db.mysql.password")
            url = getProperty("db.mysql.url")
        }
        datasource.run {
            url = this@DbConnector.url
            username = this@DbConnector.user
            password = this@DbConnector.password

            minIdle = 5
            maxIdle = 10

            maxWait = 10000
        }
    }

    fun getConnection(): Connection? = datasource.connection
}