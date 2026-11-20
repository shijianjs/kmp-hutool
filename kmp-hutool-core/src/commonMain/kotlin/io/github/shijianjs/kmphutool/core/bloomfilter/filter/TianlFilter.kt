package io.github.shijianjs.kmphutool.core.bloomfilter.filter

import io.github.shijianjs.kmphutool.core.util.HashUtil

/**
 * Bloom filter backed by `HashUtil.tianlHash`.
 *
 * Port of `cn.hutool.bloomfilter.filter.TianlFilter`.
 *
 * @hutool cn.hutool.bloomfilter.filter.TianlFilter
 */
class TianlFilter(maxValue: Long, machineNum: Int = AbstractFilter.DEFAULT_MACHINE_NUM) :
    FuncFilter(maxValue, machineNum, { HashUtil.tianlHash(it).toLong() })
