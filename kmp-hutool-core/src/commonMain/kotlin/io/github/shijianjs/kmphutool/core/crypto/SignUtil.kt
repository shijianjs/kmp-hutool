package io.github.shijianjs.kmphutool.core.crypto

import io.github.shijianjs.kmphutool.core.crypto.digest.DigestAlgorithm
import io.github.shijianjs.kmphutool.core.crypto.digest.DigestSupport
import io.github.shijianjs.kmphutool.core.util.HexUtil

/**
 * Parameter signing helpers.
 *
 * Port of the digest part of `cn.hutool.crypto.SignUtil`: parameters are sorted by key string, joined
 * with the requested separators and hashed. MD5, SHA-1 and SHA-256 are built in; the asymmetric
 * `Sign` methods stay out because `Sign` needs a JCE provider.
 *
 * @hutool cn.hutool.crypto.SignUtil
 */
object SignUtil {
    fun signParamsMd5(params: Map<*, *>, vararg otherParams: String): String =
        signParams(DigestAlgorithm.MD5, params, *otherParams)

    fun signParamsSha1(params: Map<*, *>, vararg otherParams: String): String =
        signParams(DigestAlgorithm.SHA1, params, *otherParams)

    fun signParamsSha256(params: Map<*, *>, vararg otherParams: String): String =
        signParams(DigestAlgorithm.SHA256, params, *otherParams)

    fun signParams(
        digestAlgorithm: DigestAlgorithm,
        params: Map<*, *>,
        vararg otherParams: String,
    ): String = signParams(digestAlgorithm, params, "", "", true, *otherParams)

    fun signParams(
        digestAlgorithm: DigestAlgorithm,
        params: Map<*, *>,
        separator: String,
        keyValueSeparator: String,
        isIgnoreNull: Boolean,
        vararg otherParams: String,
    ): String {
        val joined = sortJoin(params, separator, keyValueSeparator, isIgnoreNull, otherParams)
        val digest = when (digestAlgorithm) {
            DigestAlgorithm.MD5 -> DigestSupport.md5(joined.encodeToByteArray())
            DigestAlgorithm.SHA1 -> DigestSupport.sha1(joined.encodeToByteArray())
            DigestAlgorithm.SHA256 -> DigestSupport.sha256(joined.encodeToByteArray())
            else -> throw CryptoException("${digestAlgorithm.value} is not available in common code")
        }
        return HexUtil.encodeHexStr(digest)
    }

    private fun sortJoin(
        params: Map<*, *>,
        separator: String,
        keyValueSeparator: String,
        isIgnoreNull: Boolean,
        otherParams: Array<out String>,
    ): String {
        val builder = StringBuilder()
        val entries = params.entries.sortedBy { it.key?.toString() ?: "" }
        var first = true
        for (entry in entries) {
            if (isIgnoreNull && (entry.key == null || entry.value == null)) continue
            if (!first) builder.append(separator)
            first = false
            builder.append(entry.key.toString()).append(keyValueSeparator).append(entry.value.toString())
        }
        for (param in otherParams) builder.append(param)
        return builder.toString()
    }
}
