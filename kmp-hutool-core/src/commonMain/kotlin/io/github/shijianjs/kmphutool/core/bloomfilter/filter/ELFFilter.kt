package io.github.shijianjs.kmphutool.core.bloomfilter.filter

import io.github.shijianjs.kmphutool.core.util.HashUtil

/**
 * Bloom filter backed by `HashUtil.elfHash`.
 *
 * Port of `cn.hutool.bloomfilter.filter.ELFFilter`.
 *
 * @hutool cn.hutool.bloomfilter.filter.ELFFilter
 */
class ELFFilter(maxValue: Long, machineNum: Int = AbstractFilter.DEFAULT_MACHINE_NUM) :
    FuncFilter(maxValue, machineNum, { HashUtil.elfHash(it).toLong() })
