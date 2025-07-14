package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.Metron
import github.buriedincode.kraken.SQLiteCache
import github.buriedincode.kraken.ServiceException
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.assertAll

@TestInstance(Lifecycle.PER_CLASS)
class TeamTest {
  private val session: Metron

  init {
    val username = System.getenv("METRON__USERNAME") ?: "IGNORED"
    val password = System.getenv("METRON__PASSWORD") ?: "IGNORED"
    val cache = SQLiteCache(path = Paths.get("cache.sqlite"), expiry = null)
    session = Metron(username = username, password = password, cache = cache)
  }

  @Nested
  inner class ListTeams {
    @Test
    fun `Test ListTeams with a valid search`() {
      val results = session.listTeams(params = mapOf("name" to "Rat Creatures"))
      assertEquals(1, results.size)
      assertAll({ assertEquals(1473, results[0].id) }, { assertEquals("Rat Creatures", results[0].name) })
    }

    @Test
    fun `Test ListTeams with an invalid search`() {
      val results = session.listTeams(params = mapOf("name" to "INVALID"))
      assertTrue(results.isEmpty())
    }
  }

  @Nested
  inner class GetTeam {
    @Test
    fun `Test GetTeam with a valid id`() {
      val result = session.getTeam(id = 1473)
      assertNotNull(result)
      assertAll(
        { assertEquals(62250, result.comicvineId) },
        { assertTrue(result.creators.isEmpty()) },
        { assertNull(result.grandComicsDatabaseId) },
        { assertEquals(1473, result.id) },
        {
          assertEquals(
            "https://static.metron.cloud/media/team/2024/03/07/f957fc534c0245abafbecb5e8bb4dafa.jpg",
            result.image,
          )
        },
        { assertEquals("Rat Creatures", result.name) },
        { assertEquals("https://metron.cloud/team/rat-creatures/", result.resourceUrl) },
        { assertTrue(result.universes.isEmpty()) },
      )
    }

    @Test
    fun `Test GetTeam with an invalid id`() {
      assertThrows(ServiceException::class.java) { session.getTeam(id = -1) }
    }
  }
}
