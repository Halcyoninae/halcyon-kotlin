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
package com.halcyoninae.tailwind.wrapper

import com.halcyoninae.cosmos.dialog.LoadingDialog
import com.halcyoninae.halcyon.constant.Global
import com.halcyoninae.halcyon.debug.Debugger
import com.halcyoninae.halcyon.utils.TimeParser
import com.halcyoninae.tailwind.AudioInfo
import com.halcyoninae.tailwind.TailwindPlayer
import com.halcyoninae.tailwind.TailwindPlaylist
import java.io.File
import javax.sound.sampled.Control
import javax.sound.sampled.FloatControl
import javax.swing.SwingUtilities

/**
 * This simplification is due to some of the methods not being to be needed and
 * to
 * have much more control over the playback library and to make it a global
 * scope player instead of having to reinit everything on something new.
 *
 *
 * 这种简化是由于不需要某些方法，并且至
 * 对播放库有更多的控制权并使其成为全局
 * 范围播放器，而不必在新事物上重新启动所有内容。
 *
 * @author Jack Meng
 * @since 3.0
 */
class Player @JvmOverloads constructor(f: File = Global.rd.getFromAsFile(PlayerManager.BLANK_MP3_FILE)) {
    private var audio: TailwindPlaylist? = null

    /**
     * @return String
     */
    var currentFile = ""
        private set
    private var lastGain = 0.0f

    /**
     * Constructs a player with a file location
     *
     *
     * Note: This constructor does assert for the file path leading to the file to
     * be having access to the file
     * or the file existing at all.
     *
     * @param file The absolute file path leading to the audio track
     */
    constructor(file: String?) : this(File(file)) {}
    /**
     * Constructs a player with a file object
     *
     * @param f The file object
     */
    /**
     * Constructs a player with a blank mp3 file
     */
    init {
        try {
            audio = TailwindPlaylist()
            currentFile = f.absolutePath
        } catch (e: Exception) {
            Debugger.log(e)
        }
    }

    /**
     * Starts playing the audio
     */
    fun play() {
        val f = File(currentFile)
        try {
            if (AudioInfo(f, false).getTag(AudioInfo.KEY_MEDIA_DURATION) == null) {
                val ld = LoadingDialog(
                    "<html><p>No duration metadata found<br>Seeking...</p></html>",
                    true
                )
                SwingUtilities.invokeLater { ld.run() }
                Debugger.warn(
                    "No proper duration metadata found for this audio file...\nLagging to find the frame length."
                )
            }
        } catch (e: Exception) {
            // IGNORE
        }
        audio!!.playlistStart(f)
    }

    /**
     * @param percent
     */
    fun setVolume(percent: Float) {
        audio!!.setGain(percent)
        lastGain = (audio!!.controls[TailwindPlayer.MASTER_GAIN_STR] as FloatControl?)!!.value
    }

    fun requestNextTrack() {
        audio!!.forwardTrack()
    }

    fun requestPreviousTrack() {
        audio!!.backTrack()
    }

    /**
     * Resets the audio to a different track.
     *
     *
     * This method will create the new track in a threaded manner in order
     * prevent any other processes from being blocked.
     *
     * @param f The new file location (absolute path)
     */
    fun setFile(f: String) {
        currentFile = f
    }

    /**
     * @return TailwindPlayer
     */
    val stream: TailwindPlayer?
        get() = audio

    fun absolutePlay() {
        audio!!.play()
    }

    /**
     * @return String
     */
    val stringedTime: String
        get() = (TimeParser.fromSeconds(audio!!.position.toInt() * 1000) + " / "
                + TimeParser.fromSeconds(audio!!.length.toInt() * 1000))

    /**
     * @param key
     * @return Control
     */
    fun getControl(key: String?): Control? {
        return audio!!.controls[key]
    }

    /**
     * @param zeroToHundred
     * @return float
     */
    fun convertVolume(zeroToHundred: Float): Float {
        return try {
            val control = audio?.controls?.get("Master Gain") as FloatControl?
            val range = control!!.maximum - control.minimum
            zeroToHundred / 100.0f * range + control.minimum
        } catch (e: NullPointerException) {
           0F
        }
    }

    /**
     * @return String
     */
    override fun toString(): String {
        return """
             isOpen: ${audio!!.isOpen}
             isPlaying${audio!!.isPlaying}
             isPaused${audio!!.isPaused}
             """.trimIndent()
    }
}