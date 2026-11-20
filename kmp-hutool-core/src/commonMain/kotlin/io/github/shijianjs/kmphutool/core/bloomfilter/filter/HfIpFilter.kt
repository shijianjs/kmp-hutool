package io.github.shijianjs.kmphutool.core.bloomfilter.filter

import io.github.shijianjs.kmphutool.core.util.HashUtil

/**
 * Bloom filter backed by `HashUtil.hfIpHash`.
 *
 * Port of `cn.hutool.bloomfilter.filter.HfIpFilter`.
 *
 * @hutool cn.hutool.bloomfilter.filter.HfIpFilter
 */
class HfIpFilter(maxValue: Long, machineNum: Int = AbstractFilter.DEFAULT_MACHINE_NUM) :
    FuncFilter(maxValue, machineNum, { HashUtil.hfIpHash(it).toLong() })
