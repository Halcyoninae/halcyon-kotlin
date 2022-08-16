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
package com.halcyoninae.tailwind

import com.halcyoninae.cosmos.dialog.ErrorWindow
import com.halcyoninae.tailwind.simple.FileFormat
import com.halcyoninae.tailwind.vorbis.VorbisIn
import de.jarnbjo.ogg.LogicalOggStream
import de.jarnbjo.ogg.OnDemandUrlStream
import de.jarnbjo.vorbis.VorbisStream
import java.io.File
import java.net.URL
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

/**
 * @author Jack Meng
 * @since 3.1
 */
object TailwindHelper {
    /**
     * @param locale
     * @return AudioInputStream
     */
    @JvmStatic
    fun getAudioIS(locale: URL): AudioInputStream? {
        try {
            var ais: AudioInputStream
            val target = FileFormat.getFormatByName(locale.toExternalForm())
            if (target == FileFormat.MP3) {
                ais = AudioSystem.getAudioInputStream(locale)
                val base = ais.format
                val decode = AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    base.sampleRate,
                    16,
                    base.channels,
                    base.channels * 2,
                    base.sampleRate,
                    false
                )
                ais = AudioSystem.getAudioInputStream(decode, ais)
                return ais
            } else if (target == FileFormat.WAV) {
                ais = AudioSystem.getAudioInputStream(locale)
                return ais
            } else if (target == FileFormat.AIFF || target == FileFormat.AIFC) {
                ais = AudioSystem.getAudioInputStream(locale)
                return ais
            } else if (target == FileFormat.OGG) {
                val stream = OnDemandUrlStream(locale).logicalStreams
                    .iterator()
                    .next() as LogicalOggStream
                if (stream.format != LogicalOggStream.FORMAT_VORBIS) {
                    ErrorWindow("Failed to read this Vorbis (OGG) file...").run()
                    return null
                }
                val v = VorbisIn(VorbisStream(stream))
                ais = AudioInputStream(v, v.format, -1L)
                return ais
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * This writes an AudioInfo's properties to a file.
     *
     *
     * This process writes to a folder and writes the following:
     * A file with all the attributes
     * A file with the image artwork
     *
     * @param parentDir The folder to write to.
     * @param info      The AudioInfo object
     * @return The File as a directory that has been written to.
     * @since 3.3
     */
    fun writeAudioInfoConfig(parentDir: File?, info: AudioInfo?): File? {
        return null
    }
}