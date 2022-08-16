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
package com.halcyoninae.cloudspin.lib.boxblur

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
 * @author Jack Meng
 * @since 3.2
 */
class BoxBlur : Blur {
    /**
     * @param image
     * @param _x
     * @param _y
     * @param otherParams
     * @return BufferedImage
     */
    override fun blur(image: BufferedImage, _x: Int, _y: Int, vararg otherParams: Any): BufferedImage {
        var mod = BufferedImage(image.width, image.height, image.type)
        if (otherParams[0] == SpeedStyle.GENERAL) {
            for (row in 0 until image.height) {
                for (col in 0 until image.width) {
                    val x = col - _x
                    val y = row - _y
                    var count = 0
                    var r = 0
                    var g = 0
                    var b = 0
                    for (i in 0 until _x * 2 + 1) {
                        for (j in 0 until _y * 2 + 1) {
                            if (x + i < 0 || x + i >= image.width || y + j < 0 || y + j >= image.height) {
                                continue
                            }
                            val c = Color(image.getRGB(x + i, y + j))
                            r += c.red
                            g += c.green
                            b += c.blue
                            count++
                        }
                    }
                    val c = Color(r / count, g / count, b / count)
                    mod.setRGB(col, row, c.rgb)
                }
            }
        } else if (otherParams[0] == SpeedStyle.SPEED) {
            val radius = otherParams[1] as Int
            val weight = (radius * radius / radius).toFloat()
            val kernel = FloatArray(radius * radius)
            for (i in 0 until radius * radius) {
                kernel[i] = weight
            }
            val k = Kernel(radius, radius, kernel)
            // fill the edges with blurring as well
            val r = RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED)
            r.add(RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR))
            val op = ConvolveOp(k, ConvolveOp.EDGE_NO_OP, r)
            op.createCompatibleDestRaster(image.raster)
            mod = op.filter(image, mod)
        }
        return mod
    }
}