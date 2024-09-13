package github.buriedincode.kalibak

import java.nio.file.Path
import java.sql.Date
import java.sql.DriverManager
import java.time.LocalDate

data class SQLiteCache(val path: Path, val expiry: Int? = null) {
    private val databaseUrl: String = "jdbc:sqlite:$path"

    init {
        this.createTable()
        this.cleanup()
    }

    private fun createTable() {
        val query = "CREATE TABLE IF NOT EXISTS queries (url, response, query_date);"
        DriverManager.getConnection(this.databaseUrl).use {
            it.createStatement().use {
                it.execute(query)
            }
        }
    }

    fun select(url: String): String? {
        val query = if (this.expiry == null) {
            "SELECT * FROM queries WHERE url = ?;"
        } else {
            "SELECT * FROM queries WHERE url = ? and query_date > ?;"
        }
        DriverManager.getConnection(this.databaseUrl).use {
            it.prepareStatement(query).use {
                it.setString(1, url)
                if (this.expiry != null) {
                    it.setDate(2, Date.valueOf(LocalDate.now().minusDays(this.expiry.toLong())))
                }
                it.executeQuery().use {
                    return it.getString("response")
                }
            }
        }
    }

    fun insert(url: String, response: String) {
        if (this.select(url = url) != null) {
            return
        }
        val query = "INSERT INTO queries (url, response, query_date) VALUES (?, ?, ?);"
        DriverManager.getConnection(this.databaseUrl).use {
            it.prepareStatement(query).use {
                it.setString(1, url)
                it.setString(2, response)
                it.setDate(3, Date.valueOf(LocalDate.now()))
                it.executeUpdate()
            }
        }
    }

    fun delete(url: String) {
        val query = "DELETE FROM queries WHERE url = ?;"
        DriverManager.getConnection(this.databaseUrl).use {
            it.prepareStatement(query).use {
                it.setString(1, url)
                it.executeUpdate()
            }
        }
    }

    fun cleanup() {
        if (this.expiry == null) {
            return
        }
        val query = "DELETE FROM queries WHERE query_date < ?;"
        val expiryDate = LocalDate.now().minusDays(this.expiry.toLong())
        DriverManager.getConnection(this.databaseUrl).use {
            it.prepareStatement(query).use {
                it.setDate(1, Date.valueOf(expiryDate))
                it.executeUpdate()
            }
        }
    }
}
