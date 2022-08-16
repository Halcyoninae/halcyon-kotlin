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
package com.halcyoninae.halcyon.connections.properties

import com.halcyoninae.halcyon.connections.properties.validators.*
import java.awt.image.BufferedImage

/**
 * A constant defined class that holds
 * values for any external resources, such as
 * the properties file for the program config.
 *
 * @author Jack Meng
 * @see com.halcyoninae.halcyon.connections.properties.ExternalResource
 *
 * @since 3.0
 */
object ProgramResourceManager {
    const val KEY_USER_DEFAULT_FOLDER = "user.default.folder"
    const val KEY_USE_MEDIA_TITLE_AS_INFOVIEW_HEADER = "audio.info.media_title_as_header"
    const val KEY_INFOVIEW_BACKDROP_USE_GREYSCALE = "audio.info.backdrop_use_grayscale"
    const val KEY_INFOVIEW_BACKDROP_USE_GRADIENT = "audio.info.backdrop_use_gradient"
    const val KEY_PROGRAM_FORCE_OPTIMIZATION = "user.force_optimization"
    const val KEY_INFOVIEW_BACKDROP_GRADIENT_STYLE = "audio.info.backdrop_gradient_style"
    const val KEY_PROGRAM_HIDPI_VALUE = "user.hidpi_value"
    const val KEY_USER_DSIABLE_CLI = "user.disable_cli"
    const val KEY_USER_USE_DISCORD_RPC = "user.use_discord_rpc"
    const val KEY_USER_CHAR_SET_WRITE_TABLE = "user.charset_write_table"
    const val KEY_AUDIO_DEFAULT_BUFFER_SIZE = "audio.buffer_size"
    const val KEY_MINI_PLAYER_DEFAULT_BG_ALPHA = "mini.player.default_bg_alpha"
    const val KEY_TIME_CONTROL_FAST_WARD_MS_INCREMENT = "time.control.forward_ms_increment"
    const val KEY_TIME_CONTROL_BACK_WARD_MS_INCREMENT = "time.control.back_ms_increment"
    const val KEY_USER_PROGRAM_COLOR_THEME = "user.program_color_theme"

    /**
     * if false, a loop will disable a shuffle if both are activated.
     * if true, a loop will be disabled if a shuffle is activated (AKA the shuffle
     * is the master).
     */
    const val KEY_AUDIO_LOOP_SLAVE = "mini.player.loop_slave"
    @kotlin.jvm.JvmField
    val propertiesList = arrayOf(
        Property(KEY_USER_DEFAULT_FOLDER, ".", DirectoryValidator()),
        Property(
            KEY_USE_MEDIA_TITLE_AS_INFOVIEW_HEADER,
            "true", BooleanValidator()
        ),
        Property(
            KEY_INFOVIEW_BACKDROP_USE_GREYSCALE,
            "true", BooleanValidator()
        ),
        Property(
            KEY_INFOVIEW_BACKDROP_USE_GRADIENT, "true",
            BooleanValidator()
        ),
        Property(KEY_PROGRAM_FORCE_OPTIMIZATION, "true", BooleanValidator()),
        Property(
            KEY_INFOVIEW_BACKDROP_GRADIENT_STYLE,
            "focused", StrictValidator(
                "top", "left", "right",
                "focused"
            )
        ),
        Property(KEY_PROGRAM_HIDPI_VALUE, "1.0", NumericRangeValidator(0.9, 1.1, 0.1)),
        Property(KEY_USER_DSIABLE_CLI, "true", BooleanValidator()),
        Property(KEY_USER_USE_DISCORD_RPC, "true", BooleanValidator()),
        Property(
            KEY_USER_CHAR_SET_WRITE_TABLE, "utf8", StrictValidator(
                "utf8", "utf16le",
                "utf16be"
            )
        ),
        Property(KEY_AUDIO_DEFAULT_BUFFER_SIZE, "auto", DefaultValidator()),
        Property(
            KEY_MINI_PLAYER_DEFAULT_BG_ALPHA, "0.6",
            NumericRangeValidator(0.0, 1.0, 0.1)
        ),
        Property(
            KEY_TIME_CONTROL_FAST_WARD_MS_INCREMENT, "5000",
            NumericRangeValidator(1000.0, 20000.0, 1.0)
        ),
        Property(
            KEY_TIME_CONTROL_BACK_WARD_MS_INCREMENT, "5000", NumericRangeValidator(
                1000.0, 20000.0, 1.0
            )
        ),
        Property(
            KEY_USER_PROGRAM_COLOR_THEME, "dark_green",
            StrictValidator(
                "dark_green", "dark_orange", "light_orange",
                "light_green"
            )
        )
    )
    const val FILE_SLASH = "/"
    const val PROGRAM_RESOURCE_FOLDER = "halcyon"
    const val PROGRAM_RESOURCE_FILE_PROPERTIES = "conf.halcyon"
    val RESOURCE_SUBFOLDERS = arrayOf<String?>("log", "bin", "user")
    const val DEFAULT_ARTWORK_FILE_NAME = "artwork_cache.png"

    /**
     * @return The Map of default properties
     */
    val programDefaultProperties: Map<String?, String?>
        get() {
            val properties: MutableMap<String?, String?> = HashMap()
            for (p in propertiesList) properties[p.propertyName] = p.defaultProperty
            return properties
        }

    /**
     * @return The map of the allowed properties
     */
    val allowedProperties: Map<String?, PropertyValidator?>
        get() {
            val properties: MutableMap<String?, PropertyValidator?> = HashMap()
            for (p in propertiesList) properties[p.propertyName] = p.pr
            return properties
        }

    /**
     * Writes a bufferedimage to the resource folder.
     *
     * @param img An image to write; a BufferedImage instance
     * @return The string representing the location of the image (ABSOLUTE PATH)
     */
    fun writeBufferedImageToBin(img: BufferedImage?): String? {
        return ExternalResource.writeBufferedImageCacheFile(
            img,
            RESOURCE_SUBFOLDERS[1],
            DEFAULT_ARTWORK_FILE_NAME
        )
    }
}