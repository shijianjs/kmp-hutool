package io.github.shijianjs.kmphutool.core.bloomfilter.filter

import io.github.shijianjs.kmphutool.core.util.HashUtil

/**
 * Default Bloom filter using Hutool's Java-style string hash.
 *
 * Port of `cn.hutool.bloomfilter.filter.DefaultFilter`.
 *
 * @hutool cn.hutool.bloomfilter.filter.DefaultFilter
 */
class DefaultFilter(maxValue: Long, machineNumber: Int = AbstractFilter.DEFAULT_MACHINE_NUM) :
    FuncFilter(maxValue, machineNumber, { HashUtil.javaDefaultHash(it).toLong() })
