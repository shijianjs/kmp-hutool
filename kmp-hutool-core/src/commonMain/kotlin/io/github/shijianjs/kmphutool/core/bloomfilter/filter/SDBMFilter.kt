package io.github.shijianjs.kmphutool.core.bloomfilter.filter

import io.github.shijianjs.kmphutool.core.util.HashUtil

/**
 * Bloom filter backed by `HashUtil.sdbmHash`.
 *
 * Port of `cn.hutool.bloomfilter.filter.SDBMFilter`.
 *
 * @hutool cn.hutool.bloomfilter.filter.SDBMFilter
 */
class SDBMFilter(maxValue: Long, machineNum: Int = AbstractFilter.DEFAULT_MACHINE_NUM) :
    FuncFilter(maxValue, machineNum, { HashUtil.sdbmHash(it).toLong() })