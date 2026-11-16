package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.lang.hash.CityHash
import kotlin.test.Test
import kotlin.test.assertEquals

class CityHashTest {
    private data class Case(
        val length: Int,
        val hash32: Int,
        val hash64: Long,
        val low: Long,
        val high: Long,
    )

    @Test
    fun matchesUpstreamReferenceValues() {
        val cases = listOf(
            Case(0, -598290054, -7286425919675154353, 4463240938071824939, 4374473821787594281),
            Case(1, 1528084275, 4787810249829893994, 7271283877269870694, -1352893286927000788),
            Case(16, -887804018, 6146972963557212620, -7398453723187817107, 93850380810639305),
            Case(17, -305787175, 1288097421791121095, -3858948159726846437, 3499480191486518397),
            Case(32, 1135308812, 2092107961373731933, -4105617453964470465, -2062461404046331150),
            Case(33, -1994450126, -4695537285490860256, 8534580293597468164, -4849618578498899593),
            Case(64, 1320731534, -6681026661954574343, -8726830829212084121, -2081004720453634514),
            Case(65, 967439958, -993569984384047363, -2273885902644169754, -1287947446123447182),
            Case(128, 1504528275, 7069653650337432167, -1698002469026900083, 7334273075182302032),
            Case(200, 2002204502, -9059570217456873820, -3813124772219860073, 303481290049845585),
        )
        for (case in cases) {
            val data = ByteArray(case.length) { i -> ((i * 37 + case.length * 11) and 0xff).toByte() }
            assertEquals(case.hash32, CityHash.hash32(data), "hash32 length=${case.length}")
            assertEquals(case.hash64, CityHash.hash64(data), "hash64 length=${case.length}")
            assertEquals(case.low, CityHash.hash128(data).lowValue, "hash128 low length=${case.length}")
            assertEquals(case.high, CityHash.hash128(data).highValue, "hash128 high length=${case.length}")
        }
    }
}
