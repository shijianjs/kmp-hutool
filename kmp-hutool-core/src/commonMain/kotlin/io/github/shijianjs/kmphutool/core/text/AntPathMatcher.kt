package io.github.shijianjs.kmphutool.core.text

/**
 * Ant-style path matcher: `?` matches one character, `*` zero or more characters inside a path
 * segment, `**` zero or more path segments, and `{name:regex}` captures a named variable.
 *
 * Port of `cn.hutool.core.text.AntPathMatcher`. Upstream compiles every pattern into its own
 * matcher object and caches tokenised patterns in a `ConcurrentHashMap`; common code has neither a
 * concurrent map nor the class of per-pattern helpers, so this port translates the whole Ant
 * pattern into one regular expression and applies it to the full path.
 *
 * @hutool cn.hutool.core.text.AntPathMatcher
 */
class AntPathMatcher(pathSeparator: String = DEFAULT_PATH_SEPARATOR) {
    var pathSeparator: String = pathSeparator
        private set
    private var caseSensitive: Boolean = true
    private var trimTokens: Boolean = false

    init {
        this.pathSeparator = pathSeparator
    }

    /** Changes the path separator and returns `this`. */
    fun setPathSeparator(pathSeparator: String?): AntPathMatcher {
        this.pathSeparator = pathSeparator ?: DEFAULT_PATH_SEPARATOR
        return this
    }

    /** Whether matching is case sensitive (default `true`). */
    fun setCaseSensitive(caseSensitive: Boolean): AntPathMatcher {
        this.caseSensitive = caseSensitive
        return this
    }

    /** Whether path segments are trimmed before matching (default `false`). */
    fun setTrimTokens(trimTokens: Boolean): AntPathMatcher {
        this.trimTokens = trimTokens
        return this
    }

    /** Caching only affects performance upstream; this port has no cache, so the flag is ignored. */
    fun setCachePatterns(cachePatterns: Boolean): AntPathMatcher = this

    /** Whether [path] contains `*`, `?` or a `{...}` variable. */
    fun isPattern(path: String?): Boolean {
        if (path == null) return false
        var inVariable = false
        for (c in path) {
            if (c == '*' || c == '?') return true
            if (c == '{') inVariable = true
            if (c == '}' && inVariable) return true
        }
        return false
    }

    /** Whether [path] matches [pattern] completely. */
    fun match(pattern: String, path: String): Boolean = doMatch(pattern, path, fullMatch = true, variables = null)

    /** Whether the start of [path] matches [pattern]. */
    fun matchStart(pattern: String, path: String): Boolean = doMatch(pattern, path, fullMatch = false, variables = null)

    /** Extracts `{name:regex}` values from [path]; returns an empty map when nothing is captured. */
    fun extractUriTemplateVariables(pattern: String, path: String): Map<String, String> {
        val variables = LinkedHashMap<String, String>()
        if (!doMatch(pattern, path, fullMatch = true, variables = variables)) return emptyMap()
        return variables
    }

    /** Shared implementation for [match], [matchStart] and [extractUriTemplateVariables]. */
    fun doMatch(
        pattern: String,
        path: String,
        fullMatch: Boolean,
        variables: MutableMap<String, String>?,
    ): Boolean {
        if (path.startsWith(pathSeparator) != pattern.startsWith(pathSeparator)) return false
        val compiled = compile(pattern, fullMatch)
        val input = if (trimTokens) path.split(pathSeparator).joinToString(pathSeparator) { it.trim() } else path
        val match = compiled.first.find(input) ?: return false
        if (variables != null) {
            compiled.second.forEachIndexed { index, name ->
                val value = match.groups[index + 1]?.value
                if (value != null) variables[name] = value
            }
        }
        return true
    }

    /** Compiles the Ant pattern into a regex plus the ordered capture names. */
    private fun compile(pattern: String, fullMatch: Boolean): Pair<Regex, List<String>> {
        val names = ArrayList<String>()
        val regex = StringBuilder("^")
        var i = 0
        while (i < pattern.length) {
            val c = pattern[i]
            when {
                c == '*' -> {
                    if (i + 1 < pattern.length && pattern[i + 1] == '*') {
                        i += 2
                        // `**/` also matches zero segments, so `/a/**/b` covers `/a/b`.
                        if (i < pattern.length && pattern.startsWith(pathSeparator, i)) {
                            i += pathSeparator.length
                            regex.append("(?:.*/)?")
                        } else {
                            regex.append(".*")
                        }
                    } else {
                        regex.append("[^").append(Regex.escape(pathSeparator)).append("]*")
                        i++
                    }
                }
                c == '?' -> {
                    regex.append("[^").append(Regex.escape(pathSeparator)).append("]")
                    i++
                }
                c == '{' -> {
                    val end = pattern.indexOf('}', i)
                    if (end < 0) {
                        regex.append(Regex.escape("{"))
                        i++
                    } else {
                        val body = pattern.substring(i + 1, end)
                        val colon = body.indexOf(':')
                        if (colon < 0) {
                            // No regex: a plain variable matches one non-separator run.
                            names.add(body)
                            regex.append("([^").append(Regex.escape(pathSeparator)).append("]+)")
                        } else {
                            names.add(body.substring(0, colon))
                            regex.append('(').append(body.substring(colon + 1)).append(')')
                        }
                        i = end + 1
                    }
                }
                else -> {
                    regex.append(Regex.escape(c.toString()))
                    i++
                }
            }
        }
        if (fullMatch) regex.append("$")
        val options = if (caseSensitive) emptySet() else setOf(RegexOption.IGNORE_CASE)
        return Regex(regex.toString(), options) to names
    }

    companion object {
        /** Default path separator. */
        const val DEFAULT_PATH_SEPARATOR: String = "/"
    }
}

