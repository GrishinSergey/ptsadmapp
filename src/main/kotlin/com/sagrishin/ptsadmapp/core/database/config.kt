package com.sagrishin.ptsadmapp.core.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class PostgreSqlConfig {

    companion object {
        private const val DATABASE_NAME = "d8qnhnjrr13it7"
        private const val DATABASE_USER = "wacxgsgswztuey"
        private const val DATABASE_PASSWORD = "877b5636020f7e0653596d3efcca9b55914c401c1b8443022e9c296f93b654c0"

        private const val DATABASE_DRIVER = "org.postgresql.Driver"
        private const val DATABASE_TYPE = "postgresql"

        private const val DATABASE_HOST = "ec2-54-225-150-216.compute-1.amazonaws.com"
        private const val DATABASE_PORT = "5432"

        private const val CONNECTION_ENCODING = "characterEncoding=utf8"
        private const val USE_UNICODE = "useUnicode=true"

        private const val SSL_PARAM = "ssl=true"
        private const val SSL_FACTORY = "sslfactory=org.postgresql.ssl.NonValidatingFactory"
    }

    fun getDatabaseInstance(): Database {
        return getConnector().apply(this::checkTables)
    }

    private fun getConnector(): Database {
        return Database.connect(
                url = getDatabaseServerUrlConnection(),
                driver = DATABASE_DRIVER,
                user = DATABASE_USER,
                password = DATABASE_PASSWORD
        )
    }

    private fun checkTables(db: Database) {
        transaction(db) {
            with(SchemaUtils) {
                create(Patients)
                create(Appointments)
                create(Users)
            }
        }
    }

    private fun getDatabaseServerUrlConnection(): String {
        return "jdbc:$DATABASE_TYPE://$DATABASE_HOST:$DATABASE_PORT/$DATABASE_NAME?${getParams()}"
    }

    private fun getParams(): String {
        return "$CONNECTION_ENCODING&$USE_UNICODE&$SSL_PARAM&$SSL_FACTORY"
    }

}


class MySqlConfig {

    companion object {
        private const val DATABASE_NAME = "ptsadmapp"
        private const val DATABASE_USER = "root"
        private const val DATABASE_PASSWORD = "1111"

        private const val DATABASE_DRIVER = "com.mysql.jdbc.Driver"
        private const val DATABASE_TYPE = "mysql"

        private const val DATABASE_HOST = "localhost"
        private const val DATABASE_PORT = "3306"

        private const val CONNECTION_ENCODING = "characterEncoding=utf8"
        private const val USE_UNICODE = "useUnicode=true"
    }

    fun getDatabaseInstance(): Database {
        return getConnector().apply(this::checkTables)
    }

    private fun checkTables(db: Database) {
        transaction(db) {
            with(SchemaUtils) {
                create(Patients)
                create(Appointments)
                create(Users)
            }
        }
    }

    private fun getConnector(): Database {
        return Database.connect(
                url = getDatabaseServerUrlConnection(),
                driver = DATABASE_DRIVER,
                user = DATABASE_USER,
                password = DATABASE_PASSWORD
        )
    }

    private fun getDatabaseServerUrlConnection(): String {
        return "jdbc:$DATABASE_TYPE://$DATABASE_HOST:$DATABASE_PORT/$DATABASE_NAME?$CONNECTION_ENCODING&$USE_UNICODE"
    }

}
