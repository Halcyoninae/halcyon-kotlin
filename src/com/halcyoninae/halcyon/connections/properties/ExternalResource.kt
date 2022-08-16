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
package com.halcyoninae.halcyon.connections.properties

import com.halcyoninae.halcyon.DefaultManager
import com.halcyoninae.halcyon.debug.Debugger.log
import com.halcyoninae.halcyon.debug.Debugger.warn
import com.halcyoninae.halcyon.utils.DeImage.write
import java.awt.image.BufferedImage
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

/**
 * ResourceFolder is a general class that holds information about
 * the external resource folder.
 *
 *
 * The resource folder is named under the name "halcyon-mp4j".
 *
 * @author Jack Meng
 * @since 2.1
 */
object ExternalResource {
    /**
     * Represents the global instance of the PropertiesManager for the
     * user-modifiable "halcyon.properties" file
     *
     *
     * The program has one global instance to reduce overhead.
     */
    @kotlin.jvm.JvmField
    val pm = PropertiesManager(
        ProgramResourceManager.getProgramDefaultProperties(),
        ProgramResourceManager.getAllowedProperties(),
        ProgramResourceManager.PROGRAM_RESOURCE_FOLDER + ProgramResourceManager.FILE_SLASH
                + ProgramResourceManager.PROGRAM_RESOURCE_FILE_PROPERTIES
    )

    /**
     * An internal method used to retrieve a random string of letters
     * based on the parameterized length.
     *
     * @param length The length of the random string
     * @return String The random string
     */
    fun getRandomString(length: Int): String {
        val sb = StringBuilder()
        for (i in 0 until length) {
            sb.append((Math.random() * 26 + 'a'.code.toDouble()).toInt().toChar())
        }
        return sb.toString()
    }

    /**
     * Based on provided folders
     * it checks if these subfolders exists from within the main resource folder.
     *
     *
     * If a folder does not exist, it will be created under the main resource
     * folder.
     *
     *
     * 3.0:
     * Changed to have a parameter being taken
     *
     * @param folderName The folder to be checked
     */
    fun checkResourceFolder(folderName: String?) {
        val folder = File(folderName)
        if (!folder.isDirectory || !folder.exists()) {
            if (folder.mkdir()) {
                log("LOG > Resource folder created.")
            } else {
                log("LOG > Resource folder creation failed.")
            }
        }
    }

    /**
     * This is a standard method to write a log file to the resource's log folder.
     *
     *
     * This can be used for anything, however is not a place for things that are
     * cached to be written to.
     *
     *
     * It will only write String based files to the files.
     *
     * @param folderName The name of the folder to write to
     * @param f          The file to write
     */
    fun writeLog(folderName: String, f: String?) {
        if (!File(ProgramResourceManager.PROGRAM_RESOURCE_FOLDER + ProgramResourceManager.FILE_SLASH + folderName)
                .isDirectory || !File(ProgramResourceManager.PROGRAM_RESOURCE_FOLDER + ProgramResourceManager.FILE_SLASH + folderName).exists()
        ) {
            File(ProgramResourceManager.PROGRAM_RESOURCE_FOLDER + ProgramResourceManager.FILE_SLASH + folderName).mkdir()
        }
        val logFile = File(
            ProgramResourceManager.PROGRAM_RESOURCE_FOLDER + ProgramResourceManager.FILE_SLASH + folderName
                    + ProgramResourceManager.FILE_SLASH
                    + System.currentTimeMillis() + "_log.halcylog"
        )
        try {
            logFile.createNewFile()
            val writer = FileWriter(logFile)
            writer.write(f)
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Writes a buffered-image to the resource folder.
     *
     *
     * Planned:
     * - Make it so that it will write to the "cache" folder
     * of the main resource folder.
     *
     * @param bi The buffered image to write
     * @return String the absolute path of the image
     */
    fun writeBufferedImageCacheFile(bi: BufferedImage?, cacheFolder: String?, fileName: String): String {
        write(
            bi!!, ProgramResourceManager.PROGRAM_RESOURCE_FOLDER + "/" + cacheFolder
                    + "/" + fileName
        )
        return File(
            ProgramResourceManager.PROGRAM_RESOURCE_FOLDER + "/" + cacheFolder
                    + "/" + fileName
        ).absolutePath
    }

    /**
     * Creates a folder in the standard external resource folder.
     *
     * @param name The name of the folder (can be a subdirectory)
     * @return File The folder that was created
     */
    fun createFolder(name: String): Boolean {
        val folder = File(ProgramResourceManager.PROGRAM_RESOURCE_FOLDER + ProgramResourceManager.FILE_SLASH + name)
        return if (!folder.exists() || !folder.isDirectory) {
            folder.mkdir()
        } else false
    }

    /**
     * Creates a cache file in the standard usr folder that is
     * for this program.
     *
     * @param fileName The file to be created, just file name.
     * @param content  The contents of the file as a varargs of Objects
     * @return (true | | false) depending on if the creation process was a
     * success or not.
     */
    fun cacheFile(fileName: String, content: Array<String>): Boolean {
        val f = File(
            ProgramResourceManager.PROGRAM_RESOURCE_FOLDER + ProgramResourceManager.FILE_SLASH
                    + ProgramResourceManager.RESOURCE_SUBFOLDERS[2] + ProgramResourceManager.FILE_SLASH + fileName
        )
        try {
            val bw = BufferedWriter(OutputStreamWriter(FileOutputStream(f)))
            for (str in content) {
                bw.write(
                    """
    $str
    
    """.trimIndent()
                )
                bw.flush()
            }
            bw.close()
        } catch (e: IOException) {
            warn(e)
            dispatchLog(e)
        }
        return true
    }

    /**
     * @param fileName
     * @return File
     */
    fun getCacheFile(fileName: String): File {
        return File(
            ProgramResourceManager.PROGRAM_RESOURCE_FOLDER + ProgramResourceManager.FILE_SLASH
                    + ProgramResourceManager.RESOURCE_SUBFOLDERS[2] + ProgramResourceManager.FILE_SLASH + fileName
        )
    }

    /**
     * Creates a log file with a varargs of exceptions to be logged.
     * This is done asyncronously, however, the user may be alerted.
     *
     * @param ex The exceptions to be logged
     */
    @kotlin.jvm.JvmStatic
    @Synchronized
    fun dispatchLog(vararg ex: Exception) {
        val start = System.currentTimeMillis()
        val dispatch = Executors.newFixedThreadPool(1)
        val writableTask = dispatch.submit<Void?> {
            val sb = StringBuilder()
            for (e in ex) {
                sb.append(e.toString())
                val d = Date(System.currentTimeMillis())
                val df: DateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
                val sbt = StringBuilder()
                for (ste in e.stackTrace) {
                    sbt.append(
                        """
    $ste
    
    """.trimIndent()
                    )
                }
                writeLog(
                    "log",
                    """
                    Halcyon/MP4J - LOG EXCEPTION | PLEASE KNOW WHAT YOU ARE DOING
                    Exception caught time: ${df.format(d)}
                    ${e.javaClass}
                    $e
                    ${e.message}
                    LOCALIZED: ${e.localizedMessage}
                    ==BEGIN_STACK_TRACE==
                    $sbt
                    ==END_STACK_TRACE==
                    Submit an issue by making a PR to the file BUGS at ${DefaultManager.PROJECT_GITHUB_PAGE}
                    """.trimIndent()
                )
            }
            null
        }
        while (!writableTask.isDone) {
            try {
                Thread.sleep(100)
            } catch (e1: InterruptedException) {
                e1.printStackTrace()
            }
        }
        val end = System.currentTimeMillis()
        log("LOG > Log dispatch finished in " + (end - start) + "ms.")
    }
}