package github.buriedincode.kraken

import java.time.Duration
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@TestInstance(Lifecycle.PER_CLASS)
class ExceptionsTest {
  @Nested
  inner class Authentication {
    @Test
    fun `Test throwing an AuthenticationException`() {
      val session = Metron(username = "Invalid", password = "Invalid", cache = null)
      assertThrows(AuthenticationException::class.java) { session.getIssue(id = 1088) }
    }
  }

  @Nested
  inner class Service {
    @Test
    fun `Test throwing a ServiceException for a 404`() {
      val username = System.getenv("METRON__USERNAME") ?: "IGNORED"
      val password = System.getenv("METRON__PASSWORD") ?: "IGNORED"
      val session = Metron(username = username, password = password, cache = null)
      assertThrows(ServiceException::class.java) {
        // val uri = session.encodeURI(endpoint = "/invalid")
        val uri = session.encodeURI(endpoint = "/issue/-1")
        session.getRequest(uri = uri)
      }
    }

    @Test
    fun `Test throwing a ServiceException for a timeout`() {
      val username = System.getenv("METRON__USERNAME") ?: "IGNORED"
      val password = System.getenv("METRON__PASSWORD") ?: "IGNORED"
      val session = Metron(username = username, password = password, cache = null, timeout = Duration.ofMillis(1))
      assertThrows(ServiceException::class.java) { session.getIssue(id = 1088) }
    }
  }
}
