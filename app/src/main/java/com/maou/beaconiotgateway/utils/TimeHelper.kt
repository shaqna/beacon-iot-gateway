package com.maou.beaconiotgateway.utils

import android.annotation.SuppressLint
import android.os.SystemClock
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.pow

object TimeHelper {

//    private const val measuredPower = -69
//    private const val n = 2
//
//    fun Int.toBeaconDistanceInMeters(): Double {
//        return calculateDistanceForRSSI(this)
//    }
//
//    private fun calculateDistanceForRSSI(rssi: Int): Double {
//        val powValue = ((measuredPower * -1 - (rssi) * -1) / (10 * n)).toDouble()
//        return 10.toDouble().pow(powValue)
//    }

    private fun timestampMillis(timestampNanos: Long): Long {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime() + timestampNanos / 1_000_000
    }

    @SuppressLint("SimpleDateFormat")
    fun timestampToDate(timestampNanos: Long): String {
        val date = Date(timestampMillis(timestampNanos))
        return SimpleDateFormat("HH:mm:ss").format(date)
    }
}