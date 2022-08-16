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
package com.halcyoninae.halcyon.filesystem

import com.halcyoninae.halcyon.runtime.TextBOM
import java.io.File
import java.io.IOException
import java.io.OutputStream

/**
 * A simply utility that helps
 * with parsing information regarding files or a folder.
 *
 * @author Jack Meng
 * @see java.io.File
 *
 * @since 3.0
 */
object FileParser {
    /**
     * This method parses files with a certain extension.
     *
     * @param folder The folder to search for, however this parameter must be
     * guaranteed to be a folder and must exist and accessible.
     * @param rules  An array of extensions to search for and compare to.
     * @return An array of File objects that will be returned of all of the files
     * that match the rules for the specified file extensions.
     */
    fun parseOnlyAudioFiles(folder: File, rules: Array<String>): Array<File> {
        val files = folder.listFiles()
        val audioFiles = ArrayList<File>()
        assert(files != null)
        for (f in files!!) {
            if (f.isFile) {
                for (rule in rules) {
                    if (f.absolutePath.endsWith(".$rule")) {
                        audioFiles.add(f)
                    }
                }
            }
        }
        return audioFiles.toTypedArray()
    }

    /**
     * Returns if a folder contains in any way a file with the specified extension.
     *
     * @param folder The folder to search for, however this parameter must be
     * guaranteed to be a folder and must exist and accessible.
     * @param rules  An array of extensions to search for and compare to.
     * @return True if the folder contains a file with the specified extension,
     * false otherwise.
     */
    @JvmStatic
    fun contains(folder: File, rules: Array<String>): Boolean {
        val files = folder.listFiles()!!
        for (f in files) {
            if (f.isFile) {
                for (rule in rules) {
                    if (f.absolutePath.endsWith(".$rule")) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * This method parses a folder's name
     *
     * @param f The folder to parse
     * @return The name of the folder
     *
     *
     * Very useless method
     */
    fun folderName(f: String?): String {
        return File(f).name
    }

    /**
     * This method returns a string array of file names from the
     * given varargs of files to parse.
     *
     * @param files A varargs of files to parse
     * @return A string array of file names
     */
    fun parseFileNames(vararg files: File?): Array<String?> {
        val fileNames = arrayOfNulls<String>(files.size)
        for (i in files.indices) {
            if (files[i] != null && files[i]!!.isFile) {
                fileNames[i] = files[i]!!.name
            }
        }
        return fileNames
    }

    /**
     * Checks if the folder is empty (void of any files)
     *
     * @param folder The folder to check
     * @return True if the folder is empty, false otherwise
     */
    @JvmStatic
    fun isEmptyFolder(folder: File): Boolean {
        val files = folder.listFiles()!!
        return files.size == 0
    }

    /**
     * @param os
     * @param encoding
     * @throws IOException
     */
    @Throws(IOException::class)
    fun writeBOM(os: OutputStream, encoding: String) {
        os.write(if (encoding == "UTF-8") TextBOM.Companion.UTF8_BOM else if (encoding == "UTF-16LE") TextBOM.Companion.UTF16_LE_BOM else TextBOM.Companion.UTF16_BE_BOM)
    }
}