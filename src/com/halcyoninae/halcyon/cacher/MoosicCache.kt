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
package com.halcyoninae.halcyon.cacher

import com.halcyoninae.cosmos.dialog.ErrorWindow
import com.halcyoninae.halcyon.connections.properties.ExternalResource
import com.halcyoninae.halcyon.connections.properties.ProgramResourceManager
import com.halcyoninae.halcyon.constant.Global
import com.halcyoninae.halcyon.debug.Debugger.info
import com.halcyoninae.halcyon.debug.Debugger.warn
import java.io.File
import java.util.*
import java.util.function.Consumer
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException

/**
 * @author Jack Meng
 * @since 3.2
 */
class MoosicCache {
    // MoosicCache Config END
    var cacher: Cacher? = null
    private var excludedFiles: MutableList<String>? = null

    /**
 * @return A List of String representsin the tabs with the different names.
 */
    /**
     * @return A list of string
     */
    var strTabs: List<String>? = null
        private set
get() {
        return com.halcyoninae.halcyon.runtime.Program.cacher.savedPlaylists
    }

    /**
     * @return A Set of String
     */
    var likedTracks: Set<String>? = null
        private set
    private val lock: Any
    private val lock2: Any
    private val lock3: Any = Any()

    init {
        lock2 = lock3
        lock = lock2
        init()
    }

    fun init() {
        cacher = Cacher(File(MOOSIC_DEFAULT_LOCALE))
        /**
         * If the desired file for the configuration is not found, we will
         * target a default setting.
         *
         * Conditions:
         * 1. If the file doesn't exist
         * 2. If the size of the file is zero
         */
        if (!File(MOOSIC_DEFAULT_LOCALE).exists() || File(MOOSIC_DEFAULT_LOCALE).length() == 0L || !File(
                MOOSIC_DEFAULT_LOCALE
            ).isFile
        ) {
            warn("Incorrect user cache found! >,< Moosic resetting")
            val content: MutableMap<String, String?> = HashMap()
            content[NODE_USER_LIKED_TRACKS] = ""
            content[NODE_USER_SAVED_PLAYLISTS] = ""
            content[NODE_USER_EXCLUDED_TRACKS] = ""
            try {
                cacher!!.build(NODE_ROOT, content)
            } catch (e: TransformerException) {
                ExternalResource.dispatchLog(e)
                e.printStackTrace()
            } catch (e: ParserConfigurationException) {
                ExternalResource.dispatchLog(e)
                e.printStackTrace()
            }
            excludedFiles = ArrayList()
            savedPlaylists = ArrayList()
            likedTracks = HashSet()
        } else {
            /**
             * We try to look up the data from the configuration file if the previous
             * conditions
             * are not met, meaning:
             * 1. File exists
             * 2. File size is not zero.
             * However error handling and how data is processed is handled by the user.
             */
            info("Loading user cache...!! :D The moosic is on!")
            try {
                excludedFiles = if (cacher!!.getContent(NODE_USER_EXCLUDED_TRACKS)[0] != null) ArrayList(
                    Arrays.asList(
                        *cacher!!.getContent(NODE_USER_EXCLUDED_TRACKS)[0]!!.split("\n".toRegex()).toTypedArray()
                    )
                ) else ArrayList()
                savedPlaylists = if (cacher!!.getContent(NODE_USER_SAVED_PLAYLISTS)[0] != null) ArrayList(
                    Arrays.asList(
                        *cacher!!.getContent(NODE_USER_SAVED_PLAYLISTS)[0]!!.split("\n".toRegex()).toTypedArray()
                    )
                ) else ArrayList()
                likedTracks = if (cacher!!.getContent(NODE_USER_LIKED_TRACKS)[0] != null) HashSet(
                    Arrays.asList(
                        *cacher!!.getContent(NODE_USER_LIKED_TRACKS)[0]!!.split("\n".toRegex()).toTypedArray()
                    )
                ) else HashSet()
                info("EF: $excludedFiles", "SPL: " + savedPlaylists, "LT: $likedTracks")
            } catch (e: Exception) {
                ExternalResource.dispatchLog(e)
                e.printStackTrace()
                ErrorWindow(e.message).run()
            }
        }
    }

    /**
     * @param path
     * @return boolean
     */
    fun isExcluded(path: String): Boolean {
        return excludedFiles!!.contains(path)
    }

    fun pingLikedTracks() {
        synchronized(lock) { likedTracks = HashSet(Arrays.asList(*Global.ll.folder?.filesAsStr ?: )) }
    }

    fun pingSavedPlaylists() {
        synchronized(lock2) { savedPlaylists = ArrayList(HashSet(savedPlaylists)) }
    }

    /**
     * @param exclude
     */
    fun pingExcludedTracks(exclude: String) {
        if (!excludedFiles!!.contains(exclude)) {
            synchronized(lock3) { excludedFiles!!.add(exclude) }
        }
    }

    /**
     * @return A list of string
     */
    val excludedTracks: List<String>?
        get() = excludedFiles

    fun forceSave() {
        val content: MutableMap<String, String?> = HashMap()
        val sb1 = StringBuilder()
        excludedFiles!!.forEach(Consumer { x: String? -> sb1.append(x).append("\n") })
        content[NODE_USER_EXCLUDED_TRACKS] = sb1.toString()
        val sb2 = StringBuilder()
        savedPlaylists!!.forEach(Consumer { x: String? -> sb2.append(x).append("\n") })
        content[NODE_USER_SAVED_PLAYLISTS] = sb2.toString()
        val sb3 = StringBuilder()
        likedTracks!!.forEach(Consumer { x: String? -> sb3.append(x).append("\n") })
        content[NODE_USER_LIKED_TRACKS] = sb3.toString()
        info(
            "Force Saving " + this.javaClass.simpleName + " > ", "ET: \n$sb1",
            "SPL: \n$sb2", "LT: \n$sb3"
        )
        try {
            cacher!!.build(NODE_ROOT, content)
        } catch (e: TransformerException) {
            e.printStackTrace()
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        }
    }

    fun forceSaveQuiet() {
        val content: MutableMap<String, String?> = HashMap()
        val sb1 = StringBuilder()
        for (s in excludedFiles!!) {
            sb1.append(s).append("\n")
        }
        content[NODE_USER_EXCLUDED_TRACKS] = sb1.toString()
        val sb2 = StringBuilder()
        for (s in savedPlaylists!!) {
            sb2.append(s).append("\n")
        }
        content[NODE_USER_SAVED_PLAYLISTS] = sb2.toString()
        val sb3 = StringBuilder()
        for (s in likedTracks!!) {
            sb3.append(s).append("\n")
        }
        content[NODE_USER_LIKED_TRACKS] = sb3.toString()
        try {
            cacher!!.build(NODE_ROOT, content)
        } catch (e: TransformerException) {
            e.printStackTrace()
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        }
    }

    companion object {
        // MoosicCache Config START
        const val NODE_ROOT = "moosic"
        const val NODE_USER_LIKED_TRACKS = "likedTracks"
        const val NODE_USER_SAVED_PLAYLISTS = "savedPlaylists"
        const val NODE_USER_EXCLUDED_TRACKS = "excludedTracks"
        var MOOSIC_DEFAULT_LOCALE = (ProgramResourceManager.PROGRAM_RESOURCE_FOLDER
                + ProgramResourceManager.FILE_SLASH
                + ProgramResourceManager.RESOURCE_SUBFOLDERS[2] + ProgramResourceManager.FILE_SLASH + "moosic.halcyon")
    }
}