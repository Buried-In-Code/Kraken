package github.buriedincode.kraken

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.Level

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

internal const val VERSION = "0.2.3"
