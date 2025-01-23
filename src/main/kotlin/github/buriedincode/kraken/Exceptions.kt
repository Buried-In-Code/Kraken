package github.buriedincode.kraken

/**
 * A generic exception representing any error in Kraken or the Metron service.
 *
 * This exception serves as the base class for all service-related exceptions within the Kraken library.
 *
 * @param message An optional message describing the exception.
 * @param cause An optional cause that triggered this exception.
 */
open class ServiceException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

/**
 * An exception indicating an authentication failure with the Metron API.
 *
 * @param message An optional message describing the exception.
 * @param cause An optional cause that triggered this exception.
 */
class AuthenticationException(message: String? = null, cause: Throwable? = null) : ServiceException(message, cause)

/**
 * An exception indicating that the Metron API rate limit has been exceeded.
 *
 * @param message An optional message describing the exception.
 * @param cause An optional cause that triggered this exception.
 */
class RateLimitException(message: String? = null, cause: Throwable? = null) : ServiceException(message, cause)
