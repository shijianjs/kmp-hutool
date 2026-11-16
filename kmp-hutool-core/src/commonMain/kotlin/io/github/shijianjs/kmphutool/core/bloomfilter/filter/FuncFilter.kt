package io.github.shijianjs.kmphutool.core.bloomfilter.filter

/**
 * A filter driven by a pluggable hash function.
 *
 * Port of `cn.hutool.bloomfilter.filter.FuncFilter`; `java.util.function.Function` becomes a
 * Kotlin function type.
 *
 * @hutool cn.hutool.bloomfilter.filter.FuncFilter
 */
open class FuncFilter(
    maxValue: Long,
    machineNum: Int,
    private val hashFunc: (String) -> Long,
) : AbstractFilter(maxValue, machineNum) {
    constructor(maxValue: Long, hashFunc: (String) -> Long) :
        this(maxValue, DEFAULT_MACHINE_NUM, hashFunc)

    override fun hash(str: String): Long = hashFunc(str) % size
}
