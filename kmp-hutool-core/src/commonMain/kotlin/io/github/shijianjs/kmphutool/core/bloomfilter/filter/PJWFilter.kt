package io.github.shijianjs.kmphutool.core.bloomfilter.filter

import io.github.shijianjs.kmphutool.core.util.HashUtil

/**
 * Bloom filter backed by `HashUtil.pjwHash`.
 *
 * Port of `cn.hutool.bloomfilter.filter.PJWFilter`.
 *
 * @hutool cn.hutool.bloomfilter.filter.PJWFilter
 */
class PJWFilter(maxValue: Long, machineNum: Int = AbstractFilter.DEFAULT_MACHINE_NUM) :
    FuncFilter(maxValue, machineNum, { HashUtil.pjwHash(it).toLong() })
