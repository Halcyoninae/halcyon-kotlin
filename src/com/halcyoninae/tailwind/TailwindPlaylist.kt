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

import com.halcyoninae.halcyon.debug.Debugger
import com.halcyoninae.tailwind.TailwindEvent.TailwindStatus
import com.halcyoninae.tailwind.TailwindListener.StatusUpdateListener
import java.io.File

/**
 * A sort of wrapper for the default TailwindPlayer class.
 *
 * This media player is intended to be constantly fed a list
 * of media to play and can then keep track of what it had played.
 *
 * @author Jack Meng
 * @since 3.2
 * (Technically 3.1)
 */
class TailwindPlaylist : TailwindPlayer(), StatusUpdateListener {
    private val history: MutableList<File>?
    /**
     * @return boolean
     */
    /**
 * @return boolean
 */
    /**
 * @param b
 */
    /**
     * @param loop
     */
    var isLooping = false
get() {
        return audio.isLoop
    }
        public  set(b) {
        audio.isLoop = b
    }
    /**
     * @return boolean
     */
    /**
 * @return boolean
 */
    /**
 * @param b
 */
    /**
     * @param autoPlay
     */
    var isShuffling = false
get() {
        return audio.isAutoPlay
    }
        public  set(b) {
        audio.isAutoPlay = b
    }
    private var pointer = 0
    private var gain: Float

    /**
     * @return File
     */
    var currentTrack = File(".")
        private set

    init {
        history = ArrayList()
        addStatusUpdateListener(this)
        gain = Float.NaN
    }

    /**
     * @param f
     */
    fun playlistStart(f: File) {
        if (isPlaying) {
            stop()
        }
        if (currentTrack.absolutePath != f.absolutePath) {
            history!!.add(f)
            currentTrack = f
            open(f)
            if (gain != Float.NaN) {
                setGain(gain)
            }
            play()
        }
    }

    /**
     * @param f
     */
    fun rawPlay(f: File?) {
        open(f)
        if (gain != Float.NaN) {
            setGain(gain)
        }
        play()
    }

    fun backTrack() {
        var state = false
        if (history!!.size > 1 && pointer - 1 >= 0) {
            Debugger.warn(pointer)
            pointer -= 1
            Debugger.warn(pointer)
            if (isOpen) {
                close()
            }
            open(history[pointer])
            play()
            state = true
        }
        Debugger.good(
            """Backtrack marked ($state)...
Pointer Information: $pointer | ${history.size}"""
        )
    }

    fun forwardTrack() {
        var state = false
        if (history!!.size > 1 && pointer >= 0 && pointer < history.size - 1) {
            Debugger.warn(pointer)
            pointer += 1
            Debugger.warn(pointer)
            if (isOpen) {
                close()
            }
            open(history[pointer])
            play()
            state = true
        }
        Debugger.good(
            """Forwardtrack marked ($state)...
Pointer Information: $pointer | ${history.size}"""
        )
    }

    override fun setGain(gain: Float) {
        super.setGain(gain)
        this.gain = gain
    }

    /**
     * @return return the playlist history
     */
    fun getHistory(): List<File>? {
        return history
    }

    /**
     * @return int
     */
    fun getpointer(): Int {
        return pointer
    }

    /**
     * @param i
     * @return File
     */
    fun getFromHistory(i: Int): File {
        return history!![if (i > history.size) history.size else i]
    }

    /**
     * @param status
     */
    override fun statusUpdate(status: TailwindStatus) {
        if (isLoop && status == TailwindStatus.END) {
            rawPlay(currentTrack)
        }
    }

    override fun equals(arg: Any?): Boolean {
        return arg.hashCode() == super.hashCode()
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + if (history == null) history.hashCode() else 0
        return result
    }
}