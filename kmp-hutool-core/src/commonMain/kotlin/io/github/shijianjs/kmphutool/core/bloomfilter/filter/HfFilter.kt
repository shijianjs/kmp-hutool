package io.github.shijianjs.kmphutool.core.bloomfilter.filter

import io.github.shijianjs.kmphutool.core.util.HashUtil

/**
 * Bloom filter backed by `HashUtil.hfHash`.
 *
 * Port of `cn.hutool.bloomfilter.filter.HfFilter`.
 *
 * @hutool cn.hutool.bloomfilter.filter.HfFilter
 */
class HfFilter(maxValue: Long, machineNum: Int = AbstractFilter.DEFAULT_MACHINE_NUM) :
    FuncFilter(maxValue, machineNum, { HashUtil.hfHash(it).toLong() })