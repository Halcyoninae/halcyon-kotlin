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
package com.halcyoninae.halcyon.runtime

/**
 * Represents file encoding BOMs
 *
 * @author Jack Meng
 * @since 3.2
 */
interface TextBOM {
    companion object {
        val UTF8_BOM = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()) // UTF 8 Byte Order Mark
        val UTF16_LE_BOM = byteArrayOf(0xFF.toByte(), 0xFE.toByte()) // UTF 16 Little Endian Byte Order Mark
        val UTF16_BE_BOM = byteArrayOf(0xFE.toByte(), 0xFF.toByte()) // UTF 16 Big Endian Byte Order Mark
    }
}