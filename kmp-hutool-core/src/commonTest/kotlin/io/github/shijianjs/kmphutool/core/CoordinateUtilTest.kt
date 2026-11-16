package io.github.shijianjs.kmphutool.core

import io.github.shijianjs.kmphutool.core.util.CoordinateUtil
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoordinateUtilTest {
    @Test
    fun shiftsIntoMarsCoordinates() {
        assertFalse(CoordinateUtil.outOfChina(116.397, 39.909))
        assertTrue(CoordinateUtil.outOfChina(0.0, 0.0))
        val gcj = CoordinateUtil.wgs84ToGcj02(116.397, 39.909)
        assertTrue(gcj.lng in 116.39..116.41)
        assertTrue(gcj.lat in 39.90..39.93)
        val back = CoordinateUtil.gcj02ToWgs84(gcj.lng, gcj.lat)
        assertTrue(abs(back.lng - 116.397) < 0.01)
        assertTrue(abs(back.lat - 39.909) < 0.01)
    }

    @Test
    fun convertsBd09AndMercator() {
        val bd = CoordinateUtil.gcj02ToBd09(116.404, 39.915)
        val back = CoordinateUtil.bd09ToGcj02(bd.lng, bd.lat)
        assertTrue(abs(back.lng - 116.404) < 0.01)
        assertTrue(abs(back.lat - 39.915) < 0.01)

        val mercator = CoordinateUtil.wgs84ToMercator(116.397, 39.909)
        val wgs = CoordinateUtil.mercatorToWgs84(mercator.lng, mercator.lat)
        assertTrue(abs(wgs.lng - 116.397) < 0.0001)
        assertTrue(abs(wgs.lat - 39.909) < 0.0001)
    }
}
