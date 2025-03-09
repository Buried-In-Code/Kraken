package github.buriedincode.kraken

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.Level

/**
 * Logs a message at a specified logging level using the [KLogger].
 *
 * This utility function provides a consistent way to log messages across different levels (e.g., TRACE, DEBUG, INFO, WARN, ERROR) by delegating to the corresponding logging method of the [KLogger].
 *
 * @receiver [KLogger] The logger instance to log the message.
 * @param level The logging level at which the message should be logged.
 * @param message A lambda function that produces the log message.
 */
internal fun KLogger.log(level: Level, message: () -> Any?) {
    when (level) {
        Level.TRACE -> this.trace(message)
        Level.DEBUG -> this.debug(message)
        Level.INFO -> this.info(message)
        Level.WARN -> this.warn(message)
        Level.ERROR -> this.error(message)
        else -> return
    }
}

/**
 * The version of the Kraken library.
 */
internal const val VERSION = "0.3.1"
