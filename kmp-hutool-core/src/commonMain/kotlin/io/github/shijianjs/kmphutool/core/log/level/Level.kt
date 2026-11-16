package io.github.shijianjs.kmphutool.core.log.level

/**
 * Log levels, ordered from least to most severe.
 *
 * Port of `cn.hutool.log.level.Level`.
 *
 * @hutool cn.hutool.log.level.Level
 */
enum class Level {
    ALL,
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL,
    OFF,
}

/**
 * TRACE-level logging contract.
 *
 * @hutool cn.hutool.log.level.TraceLog
 */
interface TraceLog {
    /** Whether trace logging is on. */
    fun isTraceEnabled(): Boolean

    /** Logs a throwable at trace level. */
    fun trace(t: Throwable)

    /** Logs a formatted message at trace level. */
    fun trace(format: String, vararg arguments: Any?)

    /** Logs a throwable and a formatted message at trace level. */
    fun trace(t: Throwable, format: String, vararg arguments: Any?)

    /** Logs at trace level, reporting [fqcn] as the caller's class name. */
    fun trace(fqcn: String, t: Throwable?, format: String, vararg arguments: Any?)
}

/**
 * DEBUG-level logging contract.
 *
 * @hutool cn.hutool.log.level.DebugLog
 */
interface DebugLog {
    /** Whether debug logging is on. */
    fun isDebugEnabled(): Boolean

    /** Logs a throwable at debug level. */
    fun debug(t: Throwable)

    /** Logs a formatted message at debug level. */
    fun debug(format: String, vararg arguments: Any?)

    /** Logs a throwable and a formatted message at debug level. */
    fun debug(t: Throwable, format: String, vararg arguments: Any?)

    /** Logs at debug level, reporting [fqcn] as the caller's class name. */
    fun debug(fqcn: String, t: Throwable?, format: String, vararg arguments: Any?)
}

/**
 * INFO-level logging contract.
 *
 * @hutool cn.hutool.log.level.InfoLog
 */
interface InfoLog {
    /** Whether info logging is on. */
    fun isInfoEnabled(): Boolean

    /** Logs a throwable at info level. */
    fun info(t: Throwable)

    /** Logs a formatted message at info level. */
    fun info(format: String, vararg arguments: Any?)

    /** Logs a throwable and a formatted message at info level. */
    fun info(t: Throwable, format: String, vararg arguments: Any?)

    /** Logs at info level, reporting [fqcn] as the caller's class name. */
    fun info(fqcn: String, t: Throwable?, format: String, vararg arguments: Any?)
}

/**
 * WARN-level logging contract.
 *
 * @hutool cn.hutool.log.level.WarnLog
 */
interface WarnLog {
    /** Whether warn logging is on. */
    fun isWarnEnabled(): Boolean

    /** Logs a throwable at warn level. */
    fun warn(t: Throwable)

    /** Logs a formatted message at warn level. */
    fun warn(format: String, vararg arguments: Any?)

    /** Logs a throwable and a formatted message at warn level. */
    fun warn(t: Throwable, format: String, vararg arguments: Any?)

    /** Logs at warn level, reporting [fqcn] as the caller's class name. */
    fun warn(fqcn: String, t: Throwable?, format: String, vararg arguments: Any?)
}

/**
 * ERROR-level logging contract.
 *
 * @hutool cn.hutool.log.level.ErrorLog
 */
interface ErrorLog {
    /** Whether error logging is on. */
    fun isErrorEnabled(): Boolean

    /** Logs a throwable at error level. */
    fun error(t: Throwable)

    /** Logs a formatted message at error level. */
    fun error(format: String, vararg arguments: Any?)

    /** Logs a throwable and a formatted message at error level. */
    fun error(t: Throwable, format: String, vararg arguments: Any?)

    /** Logs at error level, reporting [fqcn] as the caller's class name. */
    fun error(fqcn: String, t: Throwable?, format: String, vararg arguments: Any?)
}
