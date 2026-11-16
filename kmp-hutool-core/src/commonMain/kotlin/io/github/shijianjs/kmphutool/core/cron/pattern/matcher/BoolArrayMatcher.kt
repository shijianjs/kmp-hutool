package io.github.shijianjs.kmphutool.core.cron.pattern.matcher

/**
 * Boolean-array matcher for a numeric field.
 *
 * Port of `cn.hutool.cron.pattern.matcher.BoolArrayMatcher`.
 *
 * @hutool cn.hutool.cron.pattern.matcher.BoolArrayMatcher
 */
open class BoolArrayMatcher(intValueList: List<Int>) : PartMatcher {
    protected val bValues: BooleanArray
    private val minValue: Int
    private val maxValue: Int

    init {
        require(intValueList.isNotEmpty()) { "Values must be not empty!" }
        bValues = BooleanArray(intValueList.max() + 1)
        var min = Int.MAX_VALUE
        var max = 0
        for (value in intValueList) {
            if (value < min) min = value
            if (value > max) max = value
            bValues[value] = true
        }
        minValue = min
        maxValue = max
    }

    override fun match(value: Int): Boolean =
        value in minValue..maxValue && bValues[value]

    override fun nextAfter(value: Int): Int {
        if (value == maxValue) return value
        if (value > minValue && value < maxValue) {
            var current = value
            while (current <= maxValue) {
                if (current == maxValue || bValues[current]) return current
                current++
            }
        }
        return minValue
    }

    fun getMinValue(): Int = minValue

    fun getMaxValue(): Int = maxValue

    override fun toString(): String = "Matcher:${bValues.contentToString()}"
}
