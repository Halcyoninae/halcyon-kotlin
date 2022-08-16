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
package com.halcyoninae.tailwind.vorbis

import de.jarnbjo.ogg.EndOfOggStreamException
import de.jarnbjo.vorbis.VorbisStream
import java.io.IOException
import java.io.InputStream
import javax.sound.sampled.AudioFormat

/**
 * Default Vorbis OGG input stream wrapper
 *
 * @author Jack Meng
 * @since 3.3
 */
class VorbisIn(private val stream: VorbisStream) : InputStream() {
    @Throws(IOException::class)
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        try {
            return stream.readPcm(buffer, offset, length)
        } catch (e: EndOfOggStreamException) {
            // IGNORED
        }
        return -1
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray): Int {
        return this.read(buffer, 0, buffer.size)
    }

    @Throws(IOException::class)
    override fun read(): Int {
        return 0
    }

    /**
     * @return AudioFormat Return this ogg stream's format
     */
    val format: AudioFormat
        get() = AudioFormat(
            stream.identificationHeader.sampleRate.toFloat(), 16,
            stream.identificationHeader.channels, true, true
        )
}