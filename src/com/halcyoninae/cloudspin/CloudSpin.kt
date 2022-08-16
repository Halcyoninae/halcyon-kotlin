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
package com.halcyoninae.cloudspin

import com.halcyoninae.cloudspin.lib.Blur
import java.awt.image.BufferedImage
import com.halcyoninae.cloudspin.enums.SpeedStyle
import java.awt.image.Kernel
import java.awt.image.ConvolveOp
import com.halcyoninae.cloudspin.lib.blurhash.base_83
import com.halcyoninae.cloudspin.lib.blurhash.BlurHashChild
import com.halcyoninae.cloudspin.helpers.math
import java.util.Arrays
import java.lang.IllegalArgumentException
import com.halcyoninae.cloudspin.CloudSpin
import java.awt.*
import javax.swing.Icon
import javax.swing.ImageIcon
import java.awt.image.BufferedImageOp
import java.awt.image.ColorConvertOp

/**
 * This class is used for general graphical manipulation.
 *
 *
 * For example taking an image and blurring it.
 *
 *
 * This package is intended to replace the overburdened
 * [com.halcyoninae.halcyon.utils.DeImage]
 * which has been here since 2.0 and features the old burden class style of
 * having a util
 * class that handles a lot of functions.
 *
 * @author Jack Meng
 * @since 3.2
 */
object CloudSpin {
    /**
     * Macro Methods
     *
     * @param img A bufferedImage
     * @return An int array representing the individual pixels of the image.
     */
    fun forPixels(img: BufferedImage): IntArray {
        return img.getRGB(0, 0, img.width, img.height, null, 0, img.width)
    }

    /**
     * This method will apply a toner on the original image.
     *
     * @param img  The original image.
     * @param tone The tone of the color to shift to.
     */
    fun colorTone(img: BufferedImage, tone: Color) {
        for (x in 0 until img.width) {
            for (y in 0 until img.height) {
                img.setRGB(x, y, tone.rgb)
            }
        }
    }

    /**
     * @param img
     * @param mask
     * @param method
     * @return BufferedImage
     */
    fun applyMask(img: BufferedImage?, mask: BufferedImage, method: Int): BufferedImage? {
        var maskedImage: BufferedImage? = null
        if (img != null) {
            val width = mask.width
            val height = mask.height
            maskedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            val mg = maskedImage.createGraphics()
            val x = (width - img.width) / 2
            val y = (height - img.height) / 2
            mg.drawImage(img, x, y, null)
            mg.composite = AlphaComposite.getInstance(method)
            mg.drawImage(mask, 0, 0, null)
            mg.dispose()
        }
        return maskedImage
    }

    /**
     * @param src
     * @param startOpacity
     * @param endOpacity
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @return BufferedImage
     */
    fun createGradient(
        src: BufferedImage, startOpacity: Int, endOpacity: Int, startX: Int,
        startY: Int, endX: Int, endY: Int
    ): BufferedImage? {
        val alphamask = BufferedImage(src.width, src.height, BufferedImage.TYPE_INT_ARGB)
        val g2d = alphamask.createGraphics()
        val lgp = LinearGradientPaint(
            Point(startX, startY),
            Point(endX, endY),
            floatArrayOf(0.0f, 1.0f),
            arrayOf(Color(0, 0, 0, startOpacity), Color(0, 0, 0, endOpacity))
        )
        g2d.paint = lgp
        g2d.fillRect(0, 0, alphamask.width, alphamask.height)
        g2d.dispose()
        return applyMask(src, alphamask, AlphaComposite.DST_IN)
    }

    /**
     * A lite util method to convert an Icon to an Image
     * preferable for use as a BufferedImage.
     *
     * @param icon An Icon to convert.
     * @return An Image after the conversion.
     */
    fun iconToImage(icon: Icon): Image {
        return if (icon is ImageIcon) {
            icon.image
        } else {
            val w = icon.iconWidth
            val h = icon.iconHeight
            val image = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
            val g = image.createGraphics()
            icon.paintIcon(null, g, 0, 0)
            g.dispose()
            image
        }
    }

    /**
     * @param target
     * @param w
     * @return BufferedImage
     */
    fun grabCrop(target: BufferedImage, w: Rectangle): BufferedImage {
        var width = 0
        var height = 0
        if (w.getWidth() > target.width && w.getHeight() > target.height) {
            return target
        } else {
            // since the decimals are just stripped it will be fine.
            width = w.getWidth().toInt()
            height = w.getHeight().toInt()
        }
        val cropped = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = cropped.createGraphics()
        // crop the image from (x1, y1) to (x2, y2)
        g.drawImage(
            target,
            0,
            0,
            width,
            height,
            w.getX().toInt(),
            w.getY().toInt(),
            (w.getX() + w.getWidth()).toInt(),
            (w.getY() + w.getHeight()).toInt(),
            null
        )
        g.dispose()
        return cropped
    }
}