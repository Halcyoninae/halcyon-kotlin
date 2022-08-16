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

import java.awt.Color
import java.util.*

/**
 * A Class to manipulate Color utility
 *
 * @author Jack Meng
 * @since 2.0
 */
object ColorTool {
    /**
     * Given a hex, it will return a [java.awt.Color] Object
     * representing the color.
     *
     * @param hex The hex to convert
     * @return Color The color object
     */
    @JvmStatic
    fun hexToRGBA(hex: String): Color {
        var hex = hex
        if (!hex.startsWith("#")) {
            hex = "#$hex"
        }
        return Color(
            Integer.valueOf(hex.substring(1, 3), 16),
            Integer.valueOf(hex.substring(3, 5), 16),
            Integer.valueOf(hex.substring(5, 7), 16)
        )
    }

    /**
     * Convert a color object to a string hex representation.
     *
     * @param color The color to convert
     * @return String The hex representation of the color
     */
    @JvmStatic
    fun rgbTohex(color: Color): String {
        return String.format("#%02x%02x%02x", color.red, color.green, color.blue)
    }

    @JvmStatic
    fun brightenColor(c: Color, percent: Int): Color {
        val r = c.red + (255 - c.red) * percent / 100
        val g = c.green + (255 - c.green) * percent / 100
        val b = c.blue + (255 - c.blue) * percent / 100
        val a = c.alpha - c.alpha * 30 / 100
        return Color(r, g, b, a)
    }

    @JvmStatic
    val nullColor: Color
        get() = Color(0, 0, 0, 0)

    /**
     * Returns a random color
     *
     * @return A color object (int8)
     */
    fun rndColor(): Color {
        val r = Random()
        return Color(r.nextInt(255), r.nextInt(255), r.nextInt(255))
    }
}