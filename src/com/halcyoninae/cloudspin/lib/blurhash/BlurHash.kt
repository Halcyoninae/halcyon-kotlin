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
 * The Main BlurHash Extern Class that provides
 * a High Level Method to directly interface with the
 * Child Low Level Class
 *
 * @author Jack Meng
 * @see com.halcyoninae.cloudspin.lib.Blur
 *
 * @since 1.0
 */
class BlurHash : Blur {
    /**
     * @param image
     * @param _x
     * @param _y
     * @param otherParams
     * @return BufferedImage
     */
    override fun blur(image: BufferedImage, _x: Int, _y: Int, vararg otherParams: Any): BufferedImage {
        val pixels = image.getRGB(0, 0, image.width, image.height, null, 0, image.width)
        val hash = BlurHashChild.enc(pixels, image.width, image.height, _x, _y)
        var p = 1.2
        if (otherParams != null) {
            if (otherParams[0] is Double) {
                if ((otherParams[0] as Double).toDouble() > 0) {
                    p = (otherParams[0] as Double).toDouble()
                }
            }
        }
        val dec = BlurHashChild.dec(hash, image.width, image.height, p)
        val res = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB)
        res.setRGB(0, 0, image.width, image.height, dec, 0, image.width)
        return res
    }
}