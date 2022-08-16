package com.halcyoninae.cloudspin.lib.gradient

import com.halcyoninae.cloudspin.lib.Blur
import java.awt.image.BufferedImage
import com.halcyoninae.cloudspin.enums.SpeedStyle
import java.awt.image.Kernel
import java.awt.image.ConvolveOp
import com.halcyoninae.cloudspin.lib.blurhash.base_83
import com.halcyoninae.cloudspin.lib.blurhash.BlurHashChild
import com.halcyoninae.cloudspin.helpers.math
import java.lang.IllegalArgumentException
import com.halcyoninae.cloudspin.CloudSpin
import java.awt.*
import javax.swing.Icon
import javax.swing.ImageIcon
import java.awt.image.BufferedImageOp
import java.awt.image.ColorConvertOp
import java.util.*

/**
 * A gradient generator meant to generate some interesting
 * gradients to be used within the program.
 *
 * @author Jack Meng
 * @since 3.3
 */
object GradientGenerator {
    /**
     * Create a gradient based on the colors given.
     * The accepted arguments are just some simple colors;
     * preferably those that are unique and not close to each other.
     *
     * @param style       the style to generate
     * @param requiredDim the dimension to generate the pattern
     * @param yi          the first color sequence
     * @param er          the second color sequence
     * @param lock        whether to make sure to lock or no lock if the random
     * numbers are the same
     * @return A bufferedimage of the color gradient
     */
    fun make(style: SpeedStyle, requiredDim: Dimension, yi: Color?, er: Color?, lock: Boolean): BufferedImage {
        val r = Random()
        val bi = BufferedImage(requiredDim.width, requiredDim.height, BufferedImage.TYPE_INT_ARGB)
        val g = bi.createGraphics()
        if (style == SpeedStyle.QUALITY) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        }
        val x = r.nextInt(requiredDim.width)
        val y = r.nextInt(requiredDim.height)
        // Ok might sound crazy, but there is a chance we might run into the same
        // number, maybe...
        var x2 = r.nextInt(requiredDim.width)
        if (lock) {
            stupid@ while (x2 == x) {
                x2 = r.nextInt(requiredDim.width)
                if (x2 != x) break@stupid
            }
        }
        var y2 = r.nextInt(requiredDim.height)
        if (lock) {
            stupid2@ while (y2 == y) {
                y2 = r.nextInt(requiredDim.height)
                if (y2 != y) break@stupid2
            }
        }
        val gp = GradientPaint(x.toFloat(), y.toFloat(), yi, x2.toFloat(), y2.toFloat(), er, true)
        g.paint = gp
        g.fillRect(0, 0, bi.width, bi.height)
        g.dispose()
        return bi
    }
}