package io.github.shijianjs.kmphutool.core.net

import io.github.shijianjs.kmphutool.core.map.BiMap

/**
 * Maps IPv4 subnet-mask bit counts to dotted-decimal masks and back.
 *
 * Port of `cn.hutool.core.net.MaskBit`.
 *
 * @hutool cn.hutool.core.net.MaskBit
 */
object MaskBit {
    private val MASK_BIT_MAP: BiMap<Int, String> = BiMap<Int, String>().apply {
        put(1, "128.0.0.0")
        put(2, "192.0.0.0")
        put(3, "224.0.0.0")
        put(4, "240.0.0.0")
        put(5, "248.0.0.0")
        put(6, "252.0.0.0")
        put(7, "254.0.0.0")
        put(8, "255.0.0.0")
        put(9, "255.128.0.0")
        put(10, "255.192.0.0")
        put(11, "255.224.0.0")
        put(12, "255.240.0.0")
        put(13, "255.248.0.0")
        put(14, "255.252.0.0")
        put(15, "255.254.0.0")
        put(16, "255.255.0.0")
        put(17, "255.255.128.0")
        put(18, "255.255.192.0")
        put(19, "255.255.224.0")
        put(20, "255.255.240.0")
        put(21, "255.255.248.0")
        put(22, "255.255.252.0")
        put(23, "255.255.254.0")
        put(24, "255.255.255.0")
        put(25, "255.255.255.128")
        put(26, "255.255.255.192")
        put(27, "255.255.255.224")
        put(28, "255.255.255.240")
        put(29, "255.255.255.248")
        put(30, "255.255.255.252")
        put(31, "255.255.255.254")
        put(32, "255.255.255.255")
    }

    /** The dotted mask for [maskBit], or `null` outside 1..32. */
    fun get(maskBit: Int): String? = MASK_BIT_MAP[maskBit]

    /** The bit count for a dotted [mask], or `null` when the mask is not a valid contiguous mask. */
    fun getMaskBit(mask: String): Int? = MASK_BIT_MAP.getKey(mask)
}
