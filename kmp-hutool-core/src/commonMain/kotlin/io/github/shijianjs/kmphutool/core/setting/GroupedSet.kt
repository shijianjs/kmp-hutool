package io.github.shijianjs.kmphutool.core.setting

/**
 * An INI-like `[group]` + value-lines structure, where a group holds a set of strings.
 *
 * Port of `cn.hutool.setting.GroupedSet`. Loading is text-based here: a common source set has no
 * `URL`/`File`/`InputStream`, so pass the file content to [load].
 *
 * @hutool cn.hutool.setting.GroupedSet
 */
class GroupedSet {
    private val groups = LinkedHashMap<String, LinkedHashSet<String>>()

    /** Loads `[group]` sections; blank lines and `#` comments are ignored. */
    fun load(content: CharSequence): Boolean {
        groups.clear()
        var current: LinkedHashSet<String>? = null
        for (rawLine in content.lineSequence()) {
            var line = rawLine.trim()
            if (line.isEmpty() || line.startsWith("#")) continue
            if (line.startsWith("\\#")) line = line.substring(1)
            if (line.startsWith("[") && line.endsWith("]")) {
                val group = line.substring(1, line.length - 1).trim()
                current = groups.getOrPut(group) { LinkedHashSet() }
                continue
            }
            if (current == null) {
                current = groups.getOrPut("") { LinkedHashSet() }
            }
            current.add(line)
        }
        return true
    }

    fun getGroups(): Set<String> = groups.keys

    fun getValues(group: String?): Set<String>? = groups[group.orEmpty()]

    /** `true` when [group] contains [value]; extra values must all be present as well. */
    fun contains(group: String?, value: String, vararg otherValues: String): Boolean {
        val set = getValues(group) ?: return false
        if (set.isEmpty()) return false
        if (otherValues.isEmpty()) return set.contains(value)
        return set.containsAll(otherValues.toList() + value)
    }

    /** `true` when [group] contains every value in [values]. */
    fun contains(group: String?, values: Collection<String>): Boolean {
        val set = getValues(group) ?: return false
        return values.isNotEmpty() && set.isNotEmpty() && set.containsAll(values)
    }
}
