package io.github.shijianjs.kmphutool.core.net

import io.github.shijianjs.kmphutool.core.lang.Validator

/**
 * IPv4 helpers: range expansion, subnet masks, long conversion, private-network checks.
 *
 * Port of `cn.hutool.core.net.Ipv4Util`. The upstream implementation goes through `PatternPool` and
 * `Convert`; here octets are parsed directly and validated with [Validator.isIpv4], so no regex pool or
 * converter is needed. Ranges are produced with plain arithmetic instead of nested loops over all four
 * octets, which keeps the result identical while avoiding a four-level loop.
 *
 * @hutool cn.hutool.core.net.Ipv4Util
 */
object Ipv4Util {
    const val LOCAL_IP: String = "127.0.0.1"

    /** Separator for an `from-to` range. */
    const val IP_SPLIT_MARK: String = "-"

    /** Separator between address and mask. */
    const val IP_MASK_SPLIT_MARK: String = "/"

    /** Largest mask bit. */
    const val IP_MASK_MAX: Int = 32

    /** Formats `ip/mask` as `ip/<bit>`; throws if the mask is invalid. */
    fun formatIpBlock(ip: String, mask: String): String = "$ip$IP_MASK_SPLIT_MARK${getMaskBitByMask(mask)}"

    /** Expands `x.x.x.x-x.x.x.x` or `x.x.x.x/bit`; a bare address yields a one-element list. */
    fun list(ipRange: String, isAll: Boolean): List<String> = when {
        IP_SPLIT_MARK in ipRange -> {
            val range = ipRange.split(IP_SPLIT_MARK)
            list(range[0], range[1])
        }
        IP_MASK_SPLIT_MARK in ipRange -> {
            val param = ipRange.split(IP_MASK_SPLIT_MARK)
            list(param[0], param[1].toInt(), isAll)
        }
        else -> listOf(ipRange)
    }

    /** All (or only usable) addresses of `ip/bit`. */
    fun list(ip: String, maskBit: Int, isAll: Boolean): List<String> {
        if (maskBit == IP_MASK_MAX) return if (isAll) listOf(ip) else emptyList()
        val startIp = getBeginIpStr(ip, maskBit)
        val endIp = getEndIpStr(ip, maskBit)
        if (isAll) return list(startIp, endIp)
        val start = startIp.substring(0, startIp.lastIndexOf('.') + 1) +
            (startIp.substring(startIp.lastIndexOf('.') + 1).toInt() + 1)
        val end = endIp.substring(0, endIp.lastIndexOf('.') + 1) +
            (endIp.substring(endIp.lastIndexOf('.') + 1).toInt() - 1)
        return list(start, end)
    }

    /** Every address between [ipFrom] and [ipTo], inclusive. */
    fun list(ipFrom: String, ipTo: String): List<String> {
        val from = parse(ipFrom)
        val to = parse(ipTo)
        require(from <= to) { "to IP must be greater than from IP!" }
        val count = (to - from + 1).toInt()
        val result = ArrayList<String>(count)
        for (value in from..to) {
            result.add(longToIpv4(value))
        }
        return result
    }

    /** Converts an IPv4 long to dotted decimal. */
    fun longToIpv4(longIP: Long): String =
        "${longIP shr 24 and 0xFF}.${longIP shr 16 and 0xFF}.${longIP shr 8 and 0xFF}.${longIP and 0xFF}"

    /** Parses dotted decimal to a long, throwing for an invalid address. */
    fun ipv4ToLong(strIP: String): Long = parse(strIP)

    /** Parses dotted decimal, falling back to [defaultValue] when [strIP] is invalid. */
    fun ipv4ToLong(strIP: String, defaultValue: Long): Long =
        if (Validator.isIpv4(strIP)) parse(strIP) else defaultValue

    /** First address of `ip/bit`. */
    fun getBeginIpStr(ip: String, maskBit: Int): String = longToIpv4(getBeginIpLong(ip, maskBit))

    /** First address of `ip/bit`, as a long. */
    fun getBeginIpLong(ip: String, maskBit: Int): Long = parse(ip) and parse(getMaskByMaskBit(maskBit))

    /** Last address of `ip/bit`. */
    fun getEndIpStr(ip: String, maskBit: Int): String = longToIpv4(getEndIpLong(ip, maskBit))

    /** Last address of `ip/bit`, as a long. */
    fun getEndIpLong(ip: String, maskBit: Int): Long =
        getBeginIpLong(ip, maskBit) + (0xffffffffL and parse(getMaskByMaskBit(maskBit)).inv())

    /** Converts a dotted mask to its bit count; throws when the mask is invalid. */
    fun getMaskBitByMask(mask: String): Int =
        MaskBit.getMaskBit(mask) ?: throw IllegalArgumentException("Invalid netmask $mask")

    /** Number of addresses for a mask bit, optionally excluding network and broadcast. */
    fun countByMaskBit(maskBit: Int, isAll: Boolean): Int {
        if (!isAll && (maskBit <= 0 || maskBit >= 32)) return 0
        // `1 shl 32` wraps in Kotlin; upstream's `(int) Math.pow(2, 32)` saturates at Int.MAX_VALUE.
        if (maskBit == 0) return Int.MAX_VALUE
        val count = 1 shl (32 - maskBit)
        return if (isAll) count else count - 2
    }

    /** Dotted mask for a mask bit. */
    fun getMaskByMaskBit(maskBit: Int): String = MaskBit.get(maskBit)!!

    /** Mask covering the range between [fromIp] and [toIp]. */
    fun getMaskByIpRange(fromIp: String, toIp: String): String {
        require(parse(fromIp) < parse(toIp)) { "to IP must be greater than from IP!" }
        val fromParts = fromIp.split('.')
        val toParts = toIp.split('.')
        return fromParts.indices.joinToString(".") { index ->
            (255 - toParts[index].toInt() + fromParts[index].toInt()).toString()
        }
    }

    /** Number of addresses in `fromIp..toIp`. */
    fun countByIpRange(fromIp: String, toIp: String): Int {
        val from = parse(fromIp)
        val to = parse(toIp)
        require(from <= to) { "to IP must be greater than from IP!" }
        return (to - from + 1).toInt()
    }

    /** Whether [mask] is a valid dotted subnet mask. */
    fun isMaskValid(mask: String): Boolean = MaskBit.getMaskBit(mask) != null

    /** Whether [maskBit] is a valid mask length. */
    fun isMaskBitValid(maskBit: Int): Boolean = MaskBit.get(maskBit) != null

    /** Whether the address belongs to a private or loopback network. */
    fun isInnerIP(ipAddress: String): Boolean {
        val value = parse(ipAddress)
        val aBegin = parse("10.0.0.0")
        val aEnd = parse("10.255.255.255")
        val bBegin = parse("172.16.0.0")
        val bEnd = parse("172.31.255.255")
        val cBegin = parse("192.168.0.0")
        val cEnd = parse("192.168.255.255")
        return value in aBegin..aEnd || value in bBegin..bEnd || value in cBegin..cEnd || LOCAL_IP == ipAddress
    }

    /** Matches an address against a wildcard such as `192.168.*.1`. */
    fun matches(wildcard: String, ipAddress: String): Boolean {
        if (!Validator.isIpv4(ipAddress)) return false
        val wildcardSegments = wildcard.split('.')
        val ipSegments = ipAddress.split('.')
        if (wildcardSegments.size != ipSegments.size) return false
        return wildcardSegments.indices.all { index ->
            wildcardSegments[index] == "*" || wildcardSegments[index] == ipSegments[index]
        }
    }

    private fun parse(ip: String): Long {
        require(Validator.isIpv4(ip)) { "Invalid IPv4 address!" }
        val parts = ip.split('.')
        return (parts[0].toLong() shl 24) or (parts[1].toLong() shl 16) or (parts[2].toLong() shl 8) or parts[3].toLong()
    }
}

