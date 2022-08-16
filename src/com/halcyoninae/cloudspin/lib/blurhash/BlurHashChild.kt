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
 * A low level implementation of the BlurHash
 * algorithm from here: https://blurha.sh/
 *
 *
 * Original ported from an early version of this
 * program.
 *
 * @author Jack Meng
 * @since 1.5
 */
object BlurHashChild {
    /**
     * Represents the colors;
     */
    var __ll = DoubleArray(256)

    init {
        for (i in __ll.indices) {
            val _m = i / 255.0
            __ll[i] =
                if (com.halcyoninae.cloudspin.lib.blurhash._m <= 0.04045) com.halcyoninae.cloudspin.lib.blurhash._m / 12.92 else Math.pow(
                    (com.halcyoninae.cloudspin.lib.blurhash._m + 0.055) / 1.055,
                    2.4
                )
        }
    }

    /**
     * Finds a max value in an array (2D)
     *
     * @param val The array
     * @return A max value
     */
    fun max(`val`: Array<DoubleArray>): Double {
        var max = 0.0
        for (i in `val`.indices) {
            for (j in `val`[i].indices) {
                if (`val`[i][j] > max) {
                    max = `val`[i][j]
                }
            }
        }
        return max
    }

    /**
     * Converts the given number to be within the linear range
     *
     * @param val The number to convert
     * @return The converted number
     */
    fun to_linear(`val`: Int): Double {
        return if (`val` < 0) __ll[0] else if (`val` >= 256) __ll[255] else __ll[`val`]
    }

    /**
     * Converts the given number to be within the sRGB range
     *
     * @param val The number to convert
     * @return The converted number
     */
    fun _as_linear(`val`: Double): Int {
        var _l = Arrays.binarySearch(__ll, `val`)
        if (_l < 0) {
            _l = _l.inv()
        }
        return if (_l < 0) 0 else if (_l >= 256) 255 else _l
    }

    /**
     * Encodes the given values into a BlurHash
     *
     * @param pixels     The pixels to encode
     * @param width      The width of the image
     * @param height     The height of the image
     * @param componentX The x-component of the center of the image
     * @param componentY The y-component of the center of the image
     * @return The encoded BlurHash as a String
     */
    fun enc(pixels: IntArray, width: Int, height: Int, componentX: Int, componentY: Int): String {
        val factors = Array(componentX * componentY) { DoubleArray(3) }
        for (j in 0 until componentY) {
            for (i in 0 until componentX) {
                val normalisation: Double = if (i == 0 && j == 0) 1 else 2.toDouble()
                var r = 0.0
                var g = 0.0
                var b = 0.0
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val basis = (normalisation
                                * Math.cos(Math.PI * i * x / width)
                                * Math.cos(Math.PI * j * y / height))
                        val pixel = pixels[y * width + x]
                        r += basis * to_linear(pixel shr 16 and 0xff)
                        g += basis * to_linear(pixel shr 8 and 0xff)
                        b += basis * to_linear(pixel and 0xff)
                    }
                }
                val scale = 1.0 / (width * height)
                val index = j * componentX + i
                factors[index][0] = r * scale
                factors[index][1] = g * scale
                factors[index][2] = b * scale
            }
        }
        val factorsLength = factors.size
        val hash = CharArray(4 + 2 * factorsLength)
        val sizeFlag = componentX + componentY * 9L - 10
        base_83.encode(sizeFlag, 1, hash, 0)
        val maximumValue: Double
        if (factorsLength > 1) {
            val actualMaximumValue = max(factors)
            val quantisedMaximumValue = Math
                .floor(Math.max(0.0, Math.min(82.0, Math.floor(actualMaximumValue * 166 - 0.5))))
            maximumValue = (quantisedMaximumValue + 1) / 166
            base_83.encode(Math.round(quantisedMaximumValue), 1, hash, 1)
        } else {
            maximumValue = 1.0
            base_83.encode(0, 1, hash, 1)
        }
        val dc = factors[0]
        base_83.encode(base_83.encodeDC(dc), 4, hash, 2)
        for (i in 1 until factorsLength) {
            base_83.encode(base_83.encodeAC(factors[i], maximumValue), 2, hash, 4 + 2 * i)
        }
        return String(hash)
    }

    /**
     * Decodes the given BlurHash into an array of pixels
     *
     * @param blurHash The BlurHash to decode (String)
     * @param width    The width of the image
     * @param height   The height of the image
     * @param punch    The punch value of the image; often regarded as the "sharpness" of the image
     * @return The decoded pixels
     */
    fun dec(blurHash: String?, width: Int, height: Int, punch: Double): IntArray {
        val blurHashLength = blurHash!!.length
        require(blurHashLength >= 6) { "BlurHash must be at least 6 characters long" }
        val sizeInfo = base_83.decode(blurHash.substring(0, 1))
        val sizeY = sizeInfo / 9 + 1
        val sizeX = sizeInfo % 9 + 1
        val quantMaxValue = base_83.decode(blurHash.substring(1, 2))
        val rmV = (quantMaxValue + 1) / 166.0 * punch
        val colors = Array(sizeX * sizeY) { DoubleArray(3) }
        base_83.decodeDC(blurHash.substring(2, 6), colors[0])
        for (i in 1 until sizeX * sizeY) {
            base_83.decodeAC(blurHash.substring(4 + i * 2, 6 + i * 2), rmV, colors[i])
        }
        val pixels = IntArray(width * height)
        var pos = 0
        for (j in 0 until height) {
            for (i in 0 until width) {
                var r = 0.0
                var g = 0.0
                var b = 0.0
                for (y in 0 until sizeY) {
                    for (x in 0 until sizeX) {
                        val basic = Math.cos(Math.PI * x * i / width) *
                                Math.cos(Math.PI * y * j / height)
                        val color = colors[x + y * sizeX]
                        r += color[0] * basic
                        g += color[1] * basic
                        b += color[2] * basic
                    }
                }
                pixels[pos++] = 255 shl 24 or (_as_linear(r) and 255 shl 16) or (
                        _as_linear(g) and 255 shl 8) or (_as_linear(b) and 255)
            }
        }
        return pixels
    }
}