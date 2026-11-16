package io.github.shijianjs.kmphutool.core.util

import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Conversions between WGS-84, GCJ-02 ("Mars"), BD-09 and Web Mercator.
 *
 * Port of `cn.hutool.core.util.CoordinateUtil`. The nested `Coordinate` becomes a Kotlin data class, so
 * `equals`/`hashCode`/`toString` come from the compiler; the arithmetic is unchanged.
 *
 * @hutool cn.hutool.core.util.CoordinateUtil
 */
object CoordinateUtil {
    /** Intermediate constant between the GCJ-02 and BD-09 systems. */
    const val X_PI: Double = 3.1415926535897932384626433832795 * 3000.0 / 180.0

    const val PI: Double = 3.1415926535897932384626433832795

    /** Earth radius (Krasovsky 1940). */
    const val RADIUS: Double = 6378245.0

    /** Flattening correction. */
    const val CORRECTION_PARAM: Double = 0.00669342162296594323

    /** Whether the coordinate lies outside China, where GCJ-02 does not apply. */
    fun outOfChina(lng: Double, lat: Double): Boolean =
        lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271

    /** WGS-84 → GCJ-02. */
    fun wgs84ToGcj02(lng: Double, lat: Double): Coordinate = Coordinate(lng, lat).offset(offset(lng, lat, true))

    /** WGS-84 → BD-09. */
    fun wgs84ToBd09(lng: Double, lat: Double): Coordinate {
        val gcj02 = wgs84ToGcj02(lng, lat)
        return gcj02ToBd09(gcj02.lng, gcj02.lat)
    }

    /** GCJ-02 → WGS-84 (approximate inverse). */
    fun gcj02ToWgs84(lng: Double, lat: Double): Coordinate = Coordinate(lng, lat).offset(offset(lng, lat, false))

    /** GCJ-02 → BD-09. */
    fun gcj02ToBd09(lng: Double, lat: Double): Coordinate {
        val z = sqrt(lng * lng + lat * lat) + 0.00002 * sin(lat * X_PI)
        val theta = atan2(lat, lng) + 0.000003 * cos(lng * X_PI)
        return Coordinate(z * cos(theta) + 0.0065, z * sin(theta) + 0.006)
    }

    /** BD-09 → GCJ-02. */
    fun bd09ToGcj02(lng: Double, lat: Double): Coordinate {
        val x = lng - 0.0065
        val y = lat - 0.006
        val z = sqrt(x * x + y * y) - 0.00002 * sin(y * X_PI)
        val theta = atan2(y, x) - 0.000003 * cos(x * X_PI)
        return Coordinate(z * cos(theta), z * sin(theta))
    }

    /** BD-09 → WGS-84. */
    fun bd09toWgs84(lng: Double, lat: Double): Coordinate {
        val gcj02 = bd09ToGcj02(lng, lat)
        return gcj02ToWgs84(gcj02.lng, gcj02.lat)
    }

    /** WGS-84 → Web Mercator. */
    fun wgs84ToMercator(lng: Double, lat: Double): Coordinate {
        val x = lng * 20037508.342789244 / 180
        var y = ln(tan((90 + lat) * PI / 360)) / (PI / 180)
        y *= 20037508.342789244 / 180
        return Coordinate(x, y)
    }

    /** Web Mercator → WGS-84. */
    fun mercatorToWgs84(mercatorX: Double, mercatorY: Double): Coordinate {
        val x = mercatorX / 20037508.342789244 * 180
        var y = mercatorY / 20037508.342789244 * 180
        y = 180 / PI * (2 * atan(exp(y * PI / 180)) - PI / 2)
        return Coordinate(x, y)
    }

    private fun offset(lng: Double, lat: Double, isPlus: Boolean): Coordinate {
        var dlng = transLng(lng - 105.0, lat - 35.0)
        var dlat = transLat(lng - 105.0, lat - 35.0)
        var magic = sin(lat / 180.0 * PI)
        magic = 1 - CORRECTION_PARAM * magic * magic
        val sqrtMagic = sqrt(magic)
        dlng = dlng * 180.0 / (RADIUS / sqrtMagic * cos(lat / 180.0 * PI) * PI)
        dlat = dlat * 180.0 / (RADIUS * (1 - CORRECTION_PARAM) / (magic * sqrtMagic) * PI)
        if (!isPlus) {
            dlng = -dlng
            dlat = -dlat
        }
        return Coordinate(dlng, dlat)
    }

    private fun transLng(lng: Double, lat: Double): Double {
        var ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * sqrt(abs(lng))
        ret += (20.0 * sin(6.0 * lng * PI) + 20.0 * sin(2.0 * lng * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(lng * PI) + 40.0 * sin(lng / 3.0 * PI)) * 2.0 / 3.0
        ret += (150.0 * sin(lng / 12.0 * PI) + 300.0 * sin(lng / 30.0 * PI)) * 2.0 / 3.0
        return ret
    }

    private fun transLat(lng: Double, lat: Double): Double {
        var ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * sqrt(abs(lng))
        ret += (20.0 * sin(6.0 * lng * PI) + 20.0 * sin(2.0 * lng * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(lat * PI) + 40.0 * sin(lat / 3.0 * PI)) * 2.0 / 3.0
        ret += (160.0 * sin(lat / 12.0 * PI) + 320 * sin(lat * PI / 30.0)) * 2.0 / 3.0
        return ret
    }

    /** A longitude/latitude pair. */
    data class Coordinate(
        var lng: Double,
        var lat: Double,
    ) {
        /** Adds [offset] in place and returns this. */
        fun offset(offset: Coordinate): Coordinate {
            lng += offset.lng
            lat += offset.lat
            return this
        }
    }
}
