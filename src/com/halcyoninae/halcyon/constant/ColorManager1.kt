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
package com.halcyoninae.halcyon.constant

import com.halcyoninae.cosmos.theme.ThemeBundles
import com.halcyoninae.halcyon.utils.ColorTool.brightenColor
import com.halcyoninae.halcyon.utils.ColorTool.hexToRGBA
import com.halcyoninae.halcyon.utils.ColorTool.rgbTohex

/**
 * This interface holds constants for any color values that
 * may be used throughout the program for
 * GUI based colors.
 *
 * @author Jack Meng
 * @since 3.0
 */
object ColorManager {
    @kotlin.jvm.JvmField
    var programTheme = ThemeBundles.defaultTheme

    // stable const
    @kotlin.jvm.JvmField
    var ONE_DARK_BG = hexToRGBA("#21252B")
    @kotlin.jvm.JvmField
    var BORDER_THEME = hexToRGBA("#5F657D")
    @kotlin.jvm.JvmField
    var MAIN_FG_THEME = programTheme.foregroundColor
    @kotlin.jvm.JvmField
    var MAIN_FG_STR = rgbTohex(programTheme.foregroundColor)
    @kotlin.jvm.JvmField
    var MAIN_BG_THEME = programTheme.backgroundColor
    @kotlin.jvm.JvmField
    var MAIN_FG_FADED_THEME = brightenColor(MAIN_FG_THEME, 30)
    @kotlin.jvm.JvmStatic
    fun refreshColors() {
        MAIN_FG_THEME = programTheme.foregroundColor
        MAIN_FG_STR = rgbTohex(programTheme.foregroundColor)
        MAIN_BG_THEME = programTheme.backgroundColor
    }
}