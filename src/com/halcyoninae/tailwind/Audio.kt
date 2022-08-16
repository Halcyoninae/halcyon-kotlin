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

import java.io.File
import java.net.URL

/**
 * An interface that provides implementation for
 * Audio creation to the BigContainer Player classes.
 *
 *
 * This interface template does not provide every single
 * method that a player might need; thus, the programmer
 * must implement their own styles and methods to be used along.
 *
 *
 * An example of this implementation is TailwindPlayer
 *
 * @author Jack Meng
 * @see com.halcyoninae.tailwind.TailwindPlayer
 *
 * @since 3.1
 */
interface Audio {
    /**
     * Opens a File for reading
     *
     * @param url The File locale as a [java.io.File]
     */
    fun open(url: File?)

    /**
     * Opens a URL resource linker for reading
     *
     * @param url A URL object to a resource for the audio stream.
     */
    fun open(url: URL?)

    /**
     * Close the stream buffer
     */
    fun close()

    /**
     * Start reading the loaded stream into buffers
     */
    fun play()

    /**
     * Sets the gain of the stream
     *
     * @param percent A percent value
     */
    fun setGain(percent: Float)

    /**
     * Sets the balance of the stream
     *
     * @param balance The balance value as a float
     */
    fun setBalance(balance: Float)

    /**
     * @param mute (true || false) if the stream should be on mute
     */
    fun setMute(mute: Boolean)

    /**
     * Resume from where the stream left off after a pause.
     */
    fun resume()

    /**
     * Similar to [.setPosition]
     * but will provide additional safety
     * checks to make sure the time being seeked to
     * is not out of reach. (EOF)
     *
     * @param millis
     */
    fun seekTo(millis: Long)

    /**
     * @param millis Moves the buffer reader to the millis frame
     */
    fun setPosition(millis: Long)

    /**
     * Pause the current stream at the current spot.
     */
    fun pause()

    /**
     * Stops the stream
     */
    fun stop()
}