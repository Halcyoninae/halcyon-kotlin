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
package com.halcyoninae.halcyon.connections.resource

import com.halcyoninae.halcyon.debug.Debugger.log
import java.awt.image.BufferedImage
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import javax.imageio.ImageIO
import javax.swing.ImageIcon

/**
 * Retrieves resources from the binary resource folder.
 *
 *
 * This resource folder is not located externally and is packed in during
 * compilation to the generated binary.
 *
 *
 * This class should not be confused with [com.halcyoninae.halcyon.connections.properties.ExternalResource]
 * as that class handles external resources, while this class handles internal resources.
 *
 * @author Jack Meng
 * @since 2.1
 */
class ResourceDistributor {
    /**
     * Gets an ImageIcon from the resource folder.
     *
     * @param path The path to the image
     * @return ImageIcon The image icon
     */
    fun getFromAsImageIcon(path: String): ImageIcon {
        return try {
            ImageIcon(
                Objects.requireNonNull(javaClass.getResource(path))
            )
        } catch (e: NullPointerException) {
            log(
                """
                Resource Distributor [IMAGE] was unable to fetch the expected path: $path
                Resorted to raw fetching...
                """.trimIndent()
            )
            ImageIcon(path)
        }
    }

    /**
     * Gets a standard file from the resource folder.
     *
     * @param path The path to the file
     * @return File The file
     */
    fun getFromAsFile(path: String): File {
        return try {
            File(
                Objects.requireNonNull(javaClass.getResource(path)).file
            )
        } catch (e: NullPointerException) {
            log(
                """
                Resource Distributor [FILE] was unable to fetch the expected path: $path
                Resorted to raw fetching...
                """.trimIndent()
            )
            File(path)
        }
    }

    /**
     * Similar to other methods but returns as [java.net.URL]
     *
     * @param path
     * @return
     */
    fun getFromAsURL(path: String): URL? {
        try {
            return Objects.requireNonNull(javaClass.getResource(path))
        } catch (e: NullPointerException) {
            log(
                """
                    Resource Distributor [URL] was unable to fetch the expected path: $path
                    Resorted to raw fetching...
                    """.trimIndent()
            )
            try {
                return URL(path)
            } catch (e1: MalformedURLException) {
                e1.printStackTrace()
            }
        }
        return null
    }

    /**
     * Similar to other methods but returns as [java.io.InputStream]
     *
     * @param path
     * @return
     */
    fun getFromAsStream(path: String): InputStream? {
        try {
            return Objects.requireNonNull(javaClass.getResourceAsStream(path))
        } catch (e: NullPointerException) {
            log(
                """
                    Resource Distributor [STREAM] was unable to fetch the expected path: $path
                    Resorted to raw fetching...
                    """.trimIndent()
            )
            try {
                return FileInputStream(path)
            } catch (e1: FileNotFoundException) {
                e1.printStackTrace()
            }
        }
        return null
    }

    /**
     * Retrieve a resource as a BufferedImage
     *
     * @param path The path to the suspected resource
     * @return The BufferedImage or null if not found
     */
    fun getFromAsBufferedImage(path: String): BufferedImage? {
        try {
            return ImageIO.read(File(path))
        } catch (e: IOException) {
            log(
                """
                    Resource Distributor [BUFFERED IMAGE] was unable to fetch the expected path: $path
                    Resorted to raw fetching...
                    """.trimIndent()
            )
        }
        return null
    }
}