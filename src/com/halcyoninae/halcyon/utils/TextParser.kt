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

import com.halcyoninae.halcyon.connections.properties.ExternalResource
import com.halcyoninae.halcyon.connections.properties.ProgramResourceManager
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * A utility class for text manipulation
 *
 * @author Jack Meng
 * @since 3.0
 */
object TextParser {
    /**
     * Returns a string that has been stripped based on the desired length.
     *
     *
     * For example:
     *
     *
     * strip("helloworld", 2) --> "he..."
     *
     * @param str         The string to strip
     * @param validLength The valid length (from 1)
     * @return A string that has been stripped based on the desired length
     */
    @JvmStatic
    fun strip(str: String?, validLength: Int): String {
        return if (str != null) if (str.length > validLength) str.substring(0, validLength) + "..." else str else ""
    }

    /**
     * @param str
     * @return String
     */
    fun clipText(str: String): String {
        return str.substring(0, str.length - 1)
    }

    /**
     * @param str
     * @return boolean
     */
    fun isInteger(str: String): Boolean {
        try {
            str.toInt()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    /**
     * @return String
     */
    val propertyTextEncodingName: String
        get() = if (ExternalResource.pm[ProgramResourceManager.KEY_USER_CHAR_SET_WRITE_TABLE] == "utf8") "UTF-8" else if (ExternalResource.pm[ProgramResourceManager.KEY_USER_CHAR_SET_WRITE_TABLE] == "utf16le") "UTF-16LE" else "UTF-16BE"

    /**
     * @param str
     * @return String
     */
    @JvmStatic
    fun parseAsPure(str: String): String {
        return String(
            if (ExternalResource.pm[ProgramResourceManager.KEY_USER_CHAR_SET_WRITE_TABLE] == "utf16") str.toByteArray(
                StandardCharsets.UTF_16
            ) else if (ExternalResource.pm[ProgramResourceManager.KEY_USER_CHAR_SET_WRITE_TABLE] == "utf8") str.toByteArray(
                StandardCharsets.UTF_8
            ) else if (ExternalResource.pm[ProgramResourceManager.KEY_USER_CHAR_SET_WRITE_TABLE] == "utf16le") str.toByteArray(
                StandardCharsets.UTF_16LE
            ) else str.toByteArray(StandardCharsets.UTF_16BE)
        )
    }

    /**
     * @return Charset
     */
    val charset: Charset
        get() = if (propertyTextEncodingName == "UTF-8") StandardCharsets.UTF_8 else if (propertyTextEncodingName == "UTF-16LE") StandardCharsets.UTF_16LE else StandardCharsets.UTF_16BE
}