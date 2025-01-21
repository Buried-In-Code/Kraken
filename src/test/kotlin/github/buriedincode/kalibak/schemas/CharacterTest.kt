package github.buriedincode.kalibak.schemas

import github.buriedincode.kalibak.Metron
import github.buriedincode.kalibak.SQLiteCache
import github.buriedincode.kalibak.ServiceException
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
import java.nio.file.Paths

@TestInstance(Lifecycle.PER_CLASS)
class CharacterTest {
    private val session: Metron

    init {
        val username = System.getenv("METRON__USERNAME") ?: "IGNORED"
        val password = System.getenv("METRON__PASSWORD") ?: "IGNORED"
        val cache = SQLiteCache(path = Paths.get("cache.sqlite"), expiry = null)
        session = Metron(username = username, password = password, cache = cache)
    }

    @Nested
    inner class ListCharacters {
        @Test
        fun `Test ListCharacters with a valid search`() {
            val results = session.listCharacters(params = mapOf("name" to "Smiley Bone"))
            assertEquals(1, results.size)
            assertAll(
                { assertEquals(1234, results[0].id) },
                { assertEquals("Smiley Bone", results[0].name) },
            )
        }

        @Test
        fun `Test ListCharacters with an invalid search`() {
            val results = session.listCharacters(params = mapOf("name" to "INVALID"))
            assertTrue(results.isEmpty())
        }
    }

    @Nested
    inner class GetCharacter {
        @Test
        fun `Test GetCharacter with a valid id`() {
            val result = session.getCharacter(id = 1234)
            assertNotNull(result)
            assertAll(
                { assertTrue(result.alias.isEmpty()) },
                { assertEquals(23092, result.comicvineId) },
                {
                    assertAll(
                        { assertEquals(573, result.creators[0].id) },
                        { assertEquals("Jeff Smith", result.creators[0].name) },
                    )
                },
                { assertNull(result.grandComicsDatabaseId) },
                { assertEquals(1234, result.id) },
                { assertEquals("https://static.metron.cloud/media/character/2019/01/21/Smiley-Bone.jpg", result.image) },
                { assertEquals("Smiley Bone", result.name) },
                { assertEquals("https://metron.cloud/character/smiley-bone/", result.resourceUrl) },
                { assertTrue(result.teams.isEmpty()) },
                { assertTrue(result.universes.isEmpty()) },
            )
        }

        @Test
        fun `Test GetCharacter with an invalid id`() {
            assertThrows(ServiceException::class.java) {
                session.getCharacter(id = -1)
            }
        }

        @Test
        fun `Test GetCharacter with a null alias`() {
            val result = session.getCharacter(id = 25657)
            assertNotNull(result)
            assertAll(
                { assertTrue(result.alias.isEmpty()) },
            )
        }

        @Test
        fun `Test GetCharacter with an alias`() {
            val result = session.getCharacter(id = 648)
            assertNotNull(result)
            assertAll(
                { assertEquals("Spy-D", result.alias[0]) },
            )
        }
    }
}
