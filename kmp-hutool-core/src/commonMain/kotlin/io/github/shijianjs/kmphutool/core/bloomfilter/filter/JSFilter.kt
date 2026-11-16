package io.github.shijianjs.kmphutool.core.bloomfilter.filter

import io.github.shijianjs.kmphutool.core.util.HashUtil

/**
 * Bloom filter backed by `HashUtil.jsHash`.
 *
 * Port of `cn.hutool.bloomfilter.filter.JSFilter`.
 *
 * @hutool cn.hutool.bloomfilter.filter.JSFilter
 */
class JSFilter(maxValue: Long, machineNum: Int = AbstractFilter.DEFAULT_MACHINE_NUM) :
    FuncFilter(maxValue, machineNum, { HashUtil.jsHash(it).toLong() })