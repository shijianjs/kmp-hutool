package io.github.shijianjs.kmphutool.demo.page.hashes

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.kmphutool.core.crypto.SignUtil
import io.github.shijianjs.kmphutool.core.crypto.digest.SM3
import io.github.shijianjs.kmphutool.core.crypto.digest.otp.HOTP
import io.github.shijianjs.kmphutool.core.json.JSONObject
import io.github.shijianjs.kmphutool.core.lang.RegexPool
import io.github.shijianjs.kmphutool.core.lang.hash.CityHash
import io.github.shijianjs.kmphutool.core.bloomfilter.BitMapBloomFilter
import io.github.shijianjs.kmphutool.demo.page.ResultPage
import io.github.shijianjs.kmphutool.demo.page.ResultRow
import io.github.shijianjs.kmphutool.demo.page.ResultSection
import io.github.shijianjs.kmphutool.demo.page.ResultTexts
import io.github.shijianjs.kmphutool.demo.page.resultTexts

/** One filter, so the three bloom rows describe the same object. */
private val filter: BitMapBloomFilter = BitMapBloomFilter(5)

private val firstAdded: Boolean = filter.add("kmp-hutool")

private val containsValue: Boolean = filter.contains("kmp-hutool")

private val addedAgain: Boolean = filter.add("kmp-hutool")

/** The calls of the first family: digests, one-time passwords and request signing. */
private val digestRows: List<ResultRow> = listOf(
    ResultRow("SM3().digestHex(\"abc\")", SM3().digestHex("abc").take(16) + "…"),
    ResultRow(
        "HOTP(key).generate(1)",
        HOTP(key = "12345678901234567890".encodeToByteArray()).generate(1).toString(),
    ),
    ResultRow("SignUtil.signParamsMd5({b=2, a=1}, \"key\")", SignUtil.signParamsMd5(mapOf("b" to 2, "a" to 1), "key").take(16) + "…"),
    ResultRow("Regex.matches(RegexPool.MOBILE, \"13812345678\")", Regex(RegexPool.MOBILE).matches("13812345678").toString()),
)

/** The calls of the second family: a bloom filter, added to twice. */
private val bloomRows: List<ResultRow> = listOf(
    ResultRow("BitMapBloomFilter(5).add(\"kmp-hutool\") #1", firstAdded.toString()),
    ResultRow("BitMapBloomFilter.contains(\"kmp-hutool\")", containsValue.toString()),
    ResultRow("BitMapBloomFilter.add(\"kmp-hutool\") #2", addedAgain.toString()),
)

/** The calls of the third family: JSON and a 64-bit hash. */
private val structuredRows: List<ResultRow> = listOf(
    ResultRow(
        "JSONObject().put(…).toJSONString()",
        JSONObject().put("name", "kmp-hutool").put("portable", true).toJSONString(),
    ),
    ResultRow("CityHash.hash64(\"kmp-hutool\")", CityHash.hash64("kmp-hutool".encodeToByteArray()).toString()),
)

private val english: ResultTexts = ResultTexts(
    title = "Hashes and signing",
    summary = "SM3, HOTP, request signing and a bloom filter — the cryptographic surface of the port, all " +
        "computed in the browser.",
    sections = listOf(
        ResultSection(
            title = "Digests, one-time passwords, signatures",
            hint = "Long values are cut to sixteen characters and an ellipsis, so the column stays readable.",
            rows = digestRows,
        ),
        ResultSection(
            title = "Bloom filter",
            hint = "add answers whether the bit was already set: the first call says false, the second " +
                "true, and contains agrees in between.",
            rows = bloomRows,
        ),
        ResultSection(
            title = "JSON and a 64-bit hash",
            hint = "CityHash.hash64 takes bytes, not a string — the encoding is the caller's decision.",
            rows = structuredRows,
        ),
    ),
)

private val chinese: ResultTexts = ResultTexts(
    title = "摘要与签名",
    summary = "SM3、HOTP、请求签名与布隆过滤器 —— 这个移植版本的加密面，全在浏览器里算。",
    sections = listOf(
        ResultSection(
            title = "摘要、一次性密码、签名",
            hint = "长值截到十六个字符再加省略号，避免把结果列撑开。",
            rows = digestRows,
        ),
        ResultSection(
            title = "布隆过滤器",
            hint = "add 返回的是「这一位之前是否已置上」：第一次是 false，第二次是 true，" +
                "中间的 contains 与之一致。",
            rows = bloomRows,
        ),
        ResultSection(
            title = "JSON 与 64 位哈希",
            hint = "CityHash.hash64 收的是字节数组而不是字符串 —— 怎么编码由调用方决定。",
            rows = structuredRows,
        ),
    ),
)

private val hashesTexts = resultTexts(english, chinese)

/** Every string this page shows, and the calls behind them. */
internal class HashesPage : ResultPage() {
    init {
        render(hashesTexts)
    }

    companion object : CustomElementReferenceAdapter<HashesPage>(HashesPage::class.js, "hutool-hashes-page")
}

