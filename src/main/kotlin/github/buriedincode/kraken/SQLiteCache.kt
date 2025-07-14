package github.buriedincode.kraken

import java.nio.file.Path
import java.sql.Date
import java.sql.DriverManager
import java.time.LocalDate

/**
 * A simple SQLite-based caching mechanism for storing and retrieving HTTP query results.
 *
 * The `SQLiteCache` class provides methods to persist query results, retrieve them later, and automatically clean up
 * expired entries based on a configurable expiry period.
 *
 * @property path The file path to the SQLite database file.
 * @property expiry The number of days before cached entries expire. If `null`, entries will not expire.
 * @constructor Initializes the SQLite cache, creating the necessary table and performing cleanup for expired entries.
 */
data class SQLiteCache(val path: Path, val expiry: Int? = null) {
  private val databaseUrl: String = "jdbc:sqlite:$path"

  init {
    this.createTable()
    this.cleanup()
  }

  /** Creates the `queries` table in the SQLite database if it does not already exist. */
  private fun createTable() {
    val query = "CREATE TABLE IF NOT EXISTS queries (url, response, query_date);"
    DriverManager.getConnection(this.databaseUrl).use { it.createStatement().use { it.execute(query) } }
  }

  /**
   * Selects a cached response for a given URL.
   *
   * If an expiry is set, only entries that have not expired will be retrieved.
   *
   * @param url The URL whose cached response is to be retrieved.
   * @return The cached response as a string, or `null` if no valid entry exists.
   */
  fun select(url: String): String? {
    val query =
      if (this.expiry == null) {
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

  /**
   * Inserts a new cached response for a given URL.
   *
   * If an entry for the URL already exists, the method does nothing.
   *
   * @param url The URL whose response is to be cached.
   * @param response The response to cache as a string.
   */
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

  /**
   * Deletes a cached response for a given URL.
   *
   * @param url The URL whose cached response is to be deleted.
   */
  fun delete(url: String) {
    val query = "DELETE FROM queries WHERE url = ?;"
    DriverManager.getConnection(this.databaseUrl).use {
      it.prepareStatement(query).use {
        it.setString(1, url)
        it.executeUpdate()
      }
    }
  }

  /**
   * Cleans up expired entries in the cache.
   *
   * If an expiry is set, this method removes all entries with a `query_date` older than the expiry period.
   */
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
