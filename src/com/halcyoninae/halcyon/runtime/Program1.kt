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

import com.halcyoninae.halcyon.cacher.MoosicCache
import com.halcyoninae.halcyon.connections.properties.ProgramResourceManager
import com.halcyoninae.halcyon.debug.Debugger
import com.halcyoninae.halcyon.filesystem.PhysicalFolder
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Provides a concurrent logging system for the program
 * instead of using Debugger, which is meant for debugging during
 * initial development.
 *
 * @author Jack Meng
 * @since 3.1
 */
object Program {
    @JvmField
    var cacher = MoosicCache()
    private var executorService: ExecutorService? = null

    /**
     * Different from Debugger's log method, this method is meant for
     * printing out messages to the console, however can only
     * print out a string.
     *
     *
     * Asynchronous println t the console.
     *
     * @param e A string
     */
    private fun println(e: String) {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool { r: Runnable? ->
                val t = Thread(r)
                t.isDaemon = true
                t
            }
            executorService.submit(
                Runnable {
                    while (true) {
                        try {
                            wait()
                            System.err.println(e)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                })
        }
    }

    /**
     * Writes a dump file to the bin folder.
     *
     *
     * This is not a serialization byte stream!!!
     *
     *
     * This method is typically used for debugging and coverage
     * telemetry stuffs (idk :/).
     *
     * @param content The Objects to dump or content.
     */
    fun createDump(vararg content: Any) {
        val sb = StringBuilder()
        for (o in content) {
            sb.append(o.toString())
        }
        val s = sb.toString()
        val f = File(
            ProgramResourceManager.PROGRAM_RESOURCE_FOLDER + "/bin/dump_" + System.currentTimeMillis() + "_.halcyon"
        )
        try {
            val fw = FileWriter(f)
            fw.write(s)
            fw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun forceSaveUserConf() {
        cacher.forceSave()
    }

    /**
     * @return File[] Returns all of the liked tracks.
     */
    @JvmStatic
    fun fetchLikedTracks(): Array<File?> {
        return if (cacher.likedTracks.size == 0) {
            arrayOfNulls(0)
        } else {
            val list: MutableSet<File?> = HashSet()
            for (s in cacher.likedTracks) {
                val f = File(s)
                if (f.isFile && f.exists()) {
                    list.add(f)
                }
            }
            list.toTypedArray()
        }
    }

    /**
     * @return FolderInfo[] Returns all concurrently stored playlists in the running
     * instance.
     */
    fun fetchSavedPlayLists(): Array<PhysicalFolder?> {
        return if (cacher.savedPlaylists.size == 0 || cacher.savedPlaylists == null) {
            Debugger.warn("SPL_Condition: " + 0)
            arrayOfNulls(0)
        } else {
            val list: MutableList<PhysicalFolder?> = ArrayList()
            for (s in cacher.savedPlaylists) {
                val f = File(s)
                if (f.isDirectory && f.exists()) {
                    list.add(PhysicalFolder(f.absolutePath))
                }
            }
            list.toTypedArray()
        }
    }

    /**
     * This main function runs the daemon for system logging
     *
     *
     * This is provided under an executor service that runs
     * async to the main thread in order to log everything made by the
     * the program.
     *
     *
     * This async mechanism is called natively.
     *
     * @param args Initial items to log
     */
    @JvmStatic
    fun main(args: Array<String>) {
        for (arg in args) {
            println(arg)
        }
    }
}