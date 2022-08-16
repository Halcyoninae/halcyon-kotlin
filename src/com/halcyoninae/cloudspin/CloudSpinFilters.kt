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
package com.halcyoninae.cloudspin

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
import java.awt.color.ColorSpace
import javax.swing.Icon
import javax.swing.ImageIcon
import java.awt.image.BufferedImageOp
import java.awt.image.ColorConvertOp

/**
 * @author Jack Meng
 * @since 3.2
 */
interface CloudSpinFilters {
    companion object {
        val filters = arrayOf<BufferedImageOp>(
            ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null)
        )
        val BLUR_KERNEL = floatArrayOf(
            1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
            1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
            1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f
        )
        const val AFF_GREY = 0
    }
}