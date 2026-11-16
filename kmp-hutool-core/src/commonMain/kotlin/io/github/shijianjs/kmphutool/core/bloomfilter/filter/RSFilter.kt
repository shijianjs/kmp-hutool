package io.github.shijianjs.kmphutool.core.bloomfilter.filter

import io.github.shijianjs.kmphutool.core.util.HashUtil

/**
 * Bloom filter backed by `HashUtil.rsHash`.
 *
 * Port of `cn.hutool.bloomfilter.filter.RSFilter`.
 *
 * @hutool cn.hutool.bloomfilter.filter.RSFilter
 */
class RSFilter(maxValue: Long, machineNum: Int = AbstractFilter.DEFAULT_MACHINE_NUM) :
    FuncFilter(maxValue, machineNum, { HashUtil.rsHash(it).toLong() })