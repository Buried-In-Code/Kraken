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
class ImprintTest {
  private val session: Metron

  init {
    val username = System.getenv("METRON__USERNAME") ?: "IGNORED"
    val password = System.getenv("METRON__PASSWORD") ?: "IGNORED"
    val cache = SQLiteCache(path = Paths.get("cache.sqlite"), expiry = null)
    session = Metron(username = username, password = password, cache = cache)
  }

  @Nested
  inner class ListImprints {
    @Test
    fun `Test ListImprints with a valid search`() {
      val results = session.listImprints(params = mapOf("name" to "KaBOOM!"))
      assertEquals(1, results.size)
      assertAll({ assertEquals(12, results[0].id) }, { assertEquals("KaBOOM!", results[0].name) })
    }

    @Test
    fun `Test ListImprints with an invalid search`() {
      val results = session.listImprints(params = mapOf("name" to "INVALID"))
      assertTrue(results.isEmpty())
    }
  }

  @Nested
  inner class GetImprint {
    @Test
    fun `Test GetImprint with a valid id`() {
      val result = session.getImprint(id = 12)
      assertNotNull(result)
      assertAll(
        { assertNull(result.comicvineId) },
        { assertNull(result.founded) },
        { assertNull(result.grandComicsDatabaseId) },
        { assertEquals(12, result.id) },
        { assertEquals("https://static.metron.cloud/media/imprint/2024/08/13/kaboom.jpg", result.image) },
        { assertEquals("KaBOOM!", result.name) },
        {
          assertAll({ assertEquals(20, result.publisher.id) }, { assertEquals("Boom! Studios", result.publisher.name) })
        },
        { assertEquals("https://metron.cloud/imprint/kaboom/", result.resourceUrl) },
      )
    }

    @Test
    fun `Test GetImprint with an invalid id`() {
      assertThrows(ServiceException::class.java) { session.getImprint(id = -1) }
    }
  }
}
