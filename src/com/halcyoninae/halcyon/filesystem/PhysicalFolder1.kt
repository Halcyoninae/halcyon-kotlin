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

import java.io.File
import java.util.*

/**
 * A simple class that inits and holds
 * a folder information.
 *
 * @author Jack Meng
 * @since 3.1
 */
open class PhysicalFolder(absolutePath: String) {
    /**
 * @return String
 *//**
     * Returns the absolute path of the folder-info object.
     *
     * @return The absolute path of the folder-info object.
     */
    var path = "."
public get() {
        return folderInfo.absolutePath
    }

    /**
     * Constructs the folder-info object instance with
     * the specified path.
     *
     *
     * Note: this path does not assert that the entered instance is
     * a folder that exists or the file system has access/permission to.
     *
     * @param absolutePath The path to construct the folder-info object with.
     */
    init {
        this.absolutePath = absolutePath
    }

    /**
     * Returns the Files (not sub-folders) in the folder-info object
     * or the folder as the file's names.
     *
     * @return A string array
     */
    open val filesAsStr: Array<String?>
        get() {
            val f = File(absolutePath).listFiles()!!
            val s = arrayOfNulls<String>(f.size)
            for (i in f.indices) {
                s[i] = f[i].name
            }
            return s
        }

    /**
     * Returns all of the files within the folder
     *
     * @return File array
     */
    open val files: Array<File>
        get() = File(absolutePath).listFiles()

    /**
     * Get this folder's name
     *
     * @return A String
     */
    val name: String
        get() = File(absolutePath).name

    /**
     * Returns an array of String in which each String
     * represents a file inside the folder (absolute path).
     *
     * @param rules An array of extensions to search for and compare to.
     * @return An array of String
     */
    open fun getFilesAsStr(vararg rules: String?): Array<String?> {
        val f = File(absolutePath).listFiles()!!
        val s = arrayOfNulls<String>(f.size)
        for (i in f.indices) {
            if (f[i].isFile) {
                val curr = f[i].absolutePath
                for (r in rules) {
                    if (curr.endsWith(r!!)) {
                        s[i] = curr
                        break
                    }
                }
            }
        }
        return s
    }

    /**
     * @param rules An array of extensions to search for and compare to.
     * @return An array of Files with the specified extension.
     */
    open fun getFiles(vararg rules: String?): Array<File?> {
        val f = File(absolutePath).listFiles()
        val s = arrayOfNulls<File>(f.size)
        if (f != null) {
            for (i in f.indices) {
                if (f[i].isFile) {
                    val curr = f.get(i)?.absolutePath
                    for (r in rules) {
                        if (curr != null) {
                            if (curr.endsWith(r!!)) {
                                s[i] = f[i]
                                break
                            }
                        }
                    }
                }
            }
        }
        return s
    }

    /**
     * Represents the folder-info object as a string.
     */
    override fun toString(): String {
        return absolutePath + "[" + Arrays.toString(filesAsStr) + "]"
    }
}