/*
 *  Copyright: (C) 2022 MP4J Jack Meng
 * CloudSpin a graphics library for image manipulation is licensed under the following
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
package com.halcyoninae.cloudspin.lib.blurhash

import com.halcyoninae.cloudspin.lib.Blur
import java.awt.image.BufferedImage
import com.halcyoninae.cloudspin.enums.SpeedStyle
import java.awt.Color
import java.awt.image.Kernel
import java.awt.RenderingHints
import java.awt.image.ConvolveOp
import com.halcyoninae.cloudspin.lib.blurhash.base_83
import com.halcyoninae.cloudspin.lib.blurhash.BlurHashChild
import com.halcyoninae.cloudspin.helpers.math
import java.util.Arrays
import java.lang.IllegalArgumentException
import java.awt.Graphics2D
import java.awt.GradientPaint
import java.awt.AlphaComposite
import java.awt.LinearGradientPaint
import com.halcyoninae.cloudspin.CloudSpin
import javax.swing.Icon
import javax.swing.ImageIcon
import java.awt.image.BufferedImageOp
import java.awt.image.ColorConvertOp

/**
 * Base 83 Helper Class
 *
 * @author Jack Meng
 * @since 1.0
 */
object base_83 {
    /**
     * A Char Table to look up.
     */
    val TABLE = charArrayOf(
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        'A',
        'B',
        'C',
        'D',
        'E',
        'F',
        'G',
        'H',
        'I',
        'J',
        'K',
        'L',
        'M',
        'N',
        'O',
        'P',
        'Q',
        'R',
        'S',
        'T',
        'U',
        'V',
        'W',
        'X',
        'Y',
        'Z',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j',
        'k',
        'l',
        'm',
        'n',
        'o',
        'p',
        'q',
        'r',
        's',
        't',
        'u',
        'v',
        'w',
        'x',
        'y',
        'z',
        '#',
        '$',
        '%',
        '*',
        '+',
        ',',
        '-',
        '.',
        ':',
        ';',
        '=',
        '?',
        '@',
        '[',
        ']',
        '^',
        '_',
        '{',
        '|',
        '}',
        '~'
    )

    /**
     * Encodes with Base 83.
     *
     * @param val    The value to encode
     * @param length The length of the value
     * @param buff   The buffer to write to (contains values)
     * @param offset The offset to start writing at
     * @return The encoded value as a string
     */
    fun encode(`val`: Long, length: Int, buff: CharArray, offset: Int): String {
        var _i = 1
        for (i in 1..length) {
            val curr = `val`.toInt() / _i % 83
            buff[offset + length - i] = TABLE[curr]
            _i *= 83
        }
        return String(buff)
    }

    /**
     * @param val
     * @return long
     */
    fun encodeDC(`val`: DoubleArray): Long {
        return ((BlurHashChild._as_linear(`val`[0]).toLong() shl 16) + (BlurHashChild._as_linear(`val`[1])
            .toLong() shl 8)
                + BlurHashChild._as_linear(`val`[2]))
    }

    /**
     * @param val
     * @param m
     * @return long
     */
    fun encodeAC(`val`: DoubleArray, m: Double): Long {
        return Math
            .round(
                Math.floor(
                    Math.max(
                        0.0, Math.min(
                            18.0, Math.floor(
                                math.signpow(
                                    `val`[0] / m, 0.5
                                ) * 9 + 9.5
                            )
                        )
                    )
                ) * 19 * 19 + Math.floor(
                    Math.max(
                        0.0, Math.min(
                            18.0, Math.floor(
                                math.signpow(
                                    `val`[1] / m, 0.5
                                ) * 9 + 9.5
                            )
                        )
                    )
                ) * 19 + Math.floor(
                    Math.max(
                        0.0, Math.min(
                            18.0, Math.floor(
                                math.signpow(
                                    `val`[2] / m, 0.5
                                ) * 9 + 9.5
                            )
                        )
                    )
                )
            )
    }

    /**
     * Decodes from Base 83
     *
     * @param str An Encoded String
     * @return The decoded string from base 83
     */
    fun decode(str: String): Int {
        var temp = 0
        for (c in str.toCharArray()) {
            val i = find(c)
            temp = temp * 83 + i
        }
        return temp
    }

    /**
     * @param str
     * @param rMv
     * @param color
     */
    fun decodeAC(str: String, rMv: Double, color: DoubleArray) {
        val aV = decode(str)
        val qR = aV / (19 * 19)
        val qG = aV / 19 % 19
        val qB = aV % 19
        color[0] = math.signpow((qR - 9.0) / 9.0, 2.0) * rMv
        color[1] = math.signpow((qG - 9.0) / 9.0, 2.0) * rMv
        color[2] = math.signpow((qB - 9.0) / 9.0, 2.0) * rMv
    }

    /**
     * @param str
     * @param colors
     */
    fun decodeDC(str: String, colors: DoubleArray) {
        val dV = decode(str)
        colors[0] = BlurHashChild.to_linear(dV shr 16)
        colors[1] = BlurHashChild.to_linear(dV shr 8 and 0xFF)
        colors[2] = BlurHashChild.to_linear(dV and 255)
    }

    /**
     * @param c
     * @return int
     */
    fun find(c: Char): Int {
        for (i in TABLE.indices) {
            if (TABLE[i] == c) {
                return i
            }
        }
        return -1
    }
}