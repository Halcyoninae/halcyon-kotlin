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

import java.awt.*
import java.awt.geom.RoundRectangle2D
import java.awt.image.BaseMultiResolutionImage
import java.awt.image.BufferedImage
import java.awt.image.ConvolveOp
import java.awt.image.Kernel
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.ImageIcon

/**
 * This is a class that modifies images that are fed to it.
 * It is primarily used to handle resources that are in image form.
 *
 *
 * This is a general utility class and is licensed under GPL-3.0.
 *
 * @author Jack Meng
 * @since 2.0
 */
object DeImage {
    /**
     * Turns an Image read raw from a stream to be enwrapped by a BufferedImage
     * object.
     *
     * @param image An Image from a stream.
     * @return BufferedImage A modified image that has been converted and held in a
     * BufferedImage object.
     */
    fun imagetoBI(image: Image): BufferedImage {
        val bi = BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB)
        val big = bi.createGraphics()
        big.drawImage(image, 0, 0, null)
        big.dispose()
        return bi
    }

    /**
     * Combines a mask between a source image and a mask image.
     *
     * @param sourceImage The image to be masked.
     * @param maskImage   The image to be used as a mask.
     * @param method      The method to be used to combine the images.
     * @return BufferedImage The combined image.
     */
    fun applyMask(sourceImage: BufferedImage?, maskImage: BufferedImage, method: Int): BufferedImage? {
        var maskedImage: BufferedImage? = null
        if (sourceImage != null) {
            val width = maskImage.width
            val height = maskImage.height
            maskedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            val mg = maskedImage.createGraphics()
            val x = (width - sourceImage.width) / 2
            val y = (height - sourceImage.height) / 2
            mg.drawImage(sourceImage, x, y, null)
            mg.composite = AlphaComposite.getInstance(method)
            mg.drawImage(maskImage, 0, 0, null)
            mg.dispose()
        }
        return maskedImage
    }

    /**
     * Writes a BufferedImage to a file.
     *
     * @param r    The BufferedImage to be written.
     * @param path The path to the file to be written.
     */
    @JvmStatic
    fun write(r: BufferedImage, path: String?) {
        try {
            ImageIO.write(imagetoBI(r), "png", File(path))
        } catch (e: IOException) {
            // print the exception in red text
            System.err.println("\u001B[31m" + e.message + "\u001B[0m")
        }
    }

    /**
     * Resizes a BufferedImage by preserving the aspect ratio.
     *
     * @param img  The BufferedImage to be resized.
     * @param newW The new width of the image.
     * @param newH The new height of the image.
     * @return BufferedImage The resized image.
     */
    @JvmStatic
    fun resizeNoDistort(img: BufferedImage, newW: Int, newH: Int): BufferedImage {
        val w = img.width
        val h = img.height
        val dimg: BufferedImage
        dimg = if (w > h) {
            img.getSubimage(w / 2 - h / 2, 0, h, h)
        } else {
            img.getSubimage(0, h / 2 - w / 2, w, w)
        }
        return resize(dimg, newW, newH)
    }

    /**
     * Scales up an image to it's center
     *
     * @param img         The image to be scaled up.
     * @param scaleFactor The scale factor.
     * @return BufferedImage The scaled up image.
     */
    fun zoomToCenter(img: BufferedImage, scaleFactor: Double): BufferedImage {
        val w = img.width
        val h = img.height
        val newW = (w * scaleFactor).toInt()
        val newH = (h * scaleFactor).toInt()
        val dimg: BufferedImage
        dimg = if (w > h) {
            img.getSubimage(w / 2 - h / 2, 0, h, h)
        } else {
            img.getSubimage(0, h / 2 - w / 2, w, w)
        }
        return resize(dimg, newW, newH)
    }

    /**
     * Blurs an image using a Gaussian blur.
     *
     * @param srcIMG The image to be blurred.
     * @return BufferedImage The blurred image.
     */
    fun blurImage(srcIMG: BufferedImage): BufferedImage {
        val cm = srcIMG.colorModel
        val src = BufferedImage(
            cm, srcIMG.copyData(srcIMG.raster.createCompatibleWritableRaster()),
            cm.isAlphaPremultiplied, null
        ).getSubimage(0, 0, srcIMG.width, srcIMG.height)
        val matrix = FloatArray(400)
        for (i in matrix.indices) {
            matrix[i] = 1f / matrix.size
        }
        ConvolveOp(Kernel(20, 20, matrix), ConvolveOp.EDGE_NO_OP, null).filter(src, srcIMG)
        return srcIMG
    }

    /**
     * Generates a rounded image with borders
     *
     * @param r         The image to be rounded
     * @param arc       The arc radius of the rounded corners
     * @param someColor The Color of the border
     * @return BufferedImage The rounded image
     */
    @JvmStatic
    fun createRoundedBorder(r: BufferedImage, arc: Int, someColor: Color?): BufferedImage {
        val w = r.width
        val h = r.height
        val out = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val g2 = out.createGraphics()
        g2.composite = AlphaComposite.Src
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = someColor
        g2.fill(RoundRectangle2D.Float(0F, 0F, w.toFloat(), h.toFloat(), arc.toFloat(), arc.toFloat()))
        g2.composite = AlphaComposite.SrcAtop
        g2.drawImage(r, 0, 0, null)
        g2.dispose()
        return out
    }

    /**
     * Converts an ImageIcon to a BufferedImage
     *
     * @param icon The ImageIcon to be converted
     * @return BufferedImage The converted BufferedImage
     */
    @JvmStatic
    fun imageIconToBI(icon: ImageIcon): BufferedImage {
        return imagetoBI(icon.image)
    }

    /**
     * Resizes a BufferedImage
     *
     * @param img  The BufferedImage to be resized
     * @param newW The new width
     * @param newH The new height
     * @return BufferedImage The resized BufferedImage
     */
    @JvmStatic
    fun resize(img: BufferedImage, newW: Int, newH: Int): BufferedImage {
        val tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH)
        val dimg = BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB)
        val g2d = dimg.createGraphics()
        g2d.drawImage(tmp, 0, 0, null)
        g2d.dispose()
        return dimg
    }

    /**
     * Makes a gradient from top to bottom.
     *
     * @param img          The source image
     * @param startOpacity The begin opacity of the gradient.
     * @param endOpacity   The end opacity of the gradient.
     * @return BufferedImage The gradient image.
     */
    @JvmStatic
    fun createGradientVertical(img: BufferedImage, startOpacity: Int, endOpacity: Int): BufferedImage? {
        val alphamask = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB)
        val g2d = alphamask.createGraphics()
        val lgp = LinearGradientPaint(
            Point(0, 0),
            Point(0, alphamask.height),
            floatArrayOf(0f, 1f),
            arrayOf(Color(0, 0, 0, startOpacity), Color(0, 0, 0, endOpacity))
        )
        g2d.paint = lgp
        g2d.fillRect(0, 0, alphamask.width, alphamask.height)
        g2d.dispose()
        return applyMask(img, alphamask, AlphaComposite.DST_IN)
    }

    /**
     * Creates a gradient from a certain side.
     *
     * @param img                   The source image
     * @param startOpacity          The begin opacity of the gradient.
     * @param endOpacity            The end opacity of the gradient.
     * @param opacityStartDirection The direction of the gradient.
     * @return BufferedImage The gradient image.
     */
    @JvmStatic
    fun createGradient(
        img: BufferedImage, startOpacity: Int, endOpacity: Int,
        opacityStartDirection: Directional
    ): BufferedImage? {
        if (opacityStartDirection == Directional.TOP) {
            return createGradientVertical(img, startOpacity, endOpacity)
        } else if (opacityStartDirection == Directional.BOTTOM) {
            val alphamask = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB)
            val g2d = alphamask.createGraphics()
            val lgp = LinearGradientPaint(
                Point(0, alphamask.height),
                Point(0, 0), floatArrayOf(0f, 1f), arrayOf(Color(0, 0, 0, startOpacity), Color(0, 0, 0, endOpacity))
            )
            g2d.paint = lgp
            g2d.fillRect(0, 0, alphamask.width, alphamask.height)
            g2d.dispose()
            return applyMask(img, alphamask, AlphaComposite.DST_IN)
        } else if (opacityStartDirection == Directional.RIGHT) {
            val alphamask = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB)
            val g2d = alphamask.createGraphics()
            val lgp = LinearGradientPaint(
                Point(0, alphamask.height / 2),
                Point(alphamask.width, alphamask.height / 2),
                floatArrayOf(0f, 1f),
                arrayOf(Color(0, 0, 0, startOpacity), Color(0, 0, 0, endOpacity))
            )
            g2d.paint = lgp
            g2d.fillRect(0, 0, alphamask.width, alphamask.height)
            g2d.dispose()
            return applyMask(img, alphamask, AlphaComposite.DST_IN)
        } else if (opacityStartDirection == Directional.LEFT) {
            val alphamask = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_ARGB)
            val g2d = alphamask.createGraphics()
            val lgp = LinearGradientPaint(
                Point(alphamask.width, alphamask.height / 2),
                Point(0, alphamask.height / 2),
                floatArrayOf(0f, 1f),
                arrayOf(Color(0, 0, 0, startOpacity), Color(0, 0, 0, endOpacity))
            )
            g2d.paint = lgp
            g2d.fillRect(0, 0, alphamask.width, alphamask.height)
            g2d.dispose()
            return applyMask(img, alphamask, AlphaComposite.DST_IN)
        }
        return img
    }

    /**
     * @param hRadius
     * @param vRadius
     * @return ConvolveOp
     */
    fun blurFilter(hRadius: Int, vRadius: Int): ConvolveOp {
        val width = hRadius * 2 + 1
        val height = vRadius * 2 + 1
        val weight = 1.0f / (width * height)
        val data = FloatArray(width * height)
        for (i in data.indices) {
            data[i] = weight
        }
        val k = Kernel(width, height, data)
        return ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null)
    }

    /**
     * @param image  An ImageIcon from a stream.
     * @param width  The width to scale down to
     * @param height The height to scale down to
     * @return ImageIcon A modified image that has been scaled to width and height.
     */
    @JvmStatic
    fun resizeImage(image: ImageIcon, width: Int, height: Int): ImageIcon {
        val mri = BaseMultiResolutionImage(image.image)
        val newImg = mri.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        return ImageIcon(newImg)
    }

    /**
     * Holds enum constants for the different aspects of the image.
     */
    enum class Directional {
        TOP, LEFT, RIGHT, BOTTOM
    }
}