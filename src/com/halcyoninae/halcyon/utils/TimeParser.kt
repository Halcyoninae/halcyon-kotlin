/*
 *  Copyright: (C) 2022 MP4J Jack Meng
 * Halcyon MP4J is music-playing software.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */
package com.halcyoninae.halcyon.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * A utility class that provides methods for parsing time.
 *
 * @author Jack Meng
 * @since 3.0
 */
object TimeParser {
    /**
     * Returns a string representation of the time in the format of `yyyy-MM-dd HH:mm:ss`
     * This time is taken from [java.lang.System.currentTimeMillis].
     *
     * @return A string representation of the time in the format of `yyyy-MM-dd HH:mm:ss`
     */
    @JvmStatic
    val logCurrentTime: String
        get() {
            val date = Date()
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return df.format(date)
        }

    /**
     * Converts the seconds into `HH:MM:SS` format.
     *
     * @param seconds The seconds to be converted
     * @return A string representation of the time in the format of `HH:MM:SS`
     */
    @JvmStatic
    fun fromSeconds(seconds: Int): String {
        val hour = seconds / 3600
        val minute = seconds % 3600 / 60
        val second = seconds % 60
        return String.format("%02d:%02d:%02d", hour, minute, second)
    }
}