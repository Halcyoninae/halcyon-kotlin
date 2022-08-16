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
 * A class that represents a virtual folder,
 * this virtual folder does not exist on the user's
 * actual hard drive and is used to represent a virtual
 * folder that may be used to hold files from different
 * stuffs.
 *
 *
 * The entirety of the methods from FolderInfo are overriden
 * in order to completely encompass a "virtual" folder
 *
 * @author Jack Meng
 * @since 3.1
 */
class VirtualFolder(name: String, vararg files: File?) : PhysicalFolder(name) {
    private val list: MutableList<File>

    /**
     * A VirtualFolder takes a varargs of file objects
     * in order to represent the folder with files.
     *
     *
     * The absolute path of the folder is replaced
     * by a generic name.
     *
     * @param name  The name of the virtual folder.
     * @param files The files in the virtual folder.
     */
    init {
        list = ArrayList(Arrays.asList(*files))
    }

    /**
     * @return String[] A list of files of their absolute paths in string form
     */
    override val filesAsStr: Array<String?>
        get() {
            val str = arrayOfNulls<String>(files!!.size)
            for (i in str.indices) {
                str[i] = files!![i]!!.absolutePath
            }
            return str
        }

    /**
     * @return File[] A list of files of their File object instances (created on
     * call)
     */
    override val files: Array<File>
        get() = list.toTypedArray()

    /**
     * Applies a check against the list of avaliable files.
     *
     * @param rules A set of rules to be used to compare the ending of the file
     * (endsWith)
     * @return String[] A list of file of their absolute paths in string form
     */
    override fun getFilesAsStr(vararg rules: String?): Array<String?> {
        val buff: MutableList<String?> = ArrayList()
        for (f in list) {
            for (rule in rules) {
                if (f.absolutePath.endsWith(rule!!)) {
                    buff.add(f.name)
                }
            }
        }
        return buff.toTypedArray()
    }

    /**
     * Applies a check against the list of avaliable files.
     *
     *
     * This uses the method [.getFilesAsStr] method
     * for validation.
     *
     * @param rules A set of rules to be used to comapre the ending of the file
     * (endsWith)
     * @return File[] A list of file of their respective absolute paths
     */
    override fun getFiles(vararg rules: String?): Array<File?> {
        val files: MutableList<File?> = ArrayList()
        for (str in files) {
            for (r in rules) {
                if (str != null) {
                    if (str.absolutePath.endsWith(r!!)) {
                        files.add(str)
                    }
                }
            }
        }
        return files.toTypedArray()
    }

    /**
     * Insert a file into this
     * virtual folder instance.
     *
     *
     * This method does not handle if
     * this file instance exists or not.
     *
     *
     * The implementation must handle this by
     * themselves.
     *
     * @param f A file to insert.
     */
    @Synchronized
    fun addFile(f: File) {
        list.add(f)
    }

    /**
     * Returns the absolute list.
     *
     * @return A List
     */
    val asListFiles: List<File>
        get() = list

    /**
     * Removes a file from the
     * absolute list.
     *
     * @param f The file instance to remove.
     * @return (true | | false) if the action was a success
     */
    @Synchronized
    fun removeFile(f: File): Boolean {
        return list.remove(f)
    }

    /**
     * @return String
     */
    override fun toString(): String {
        return "[ VIRTUAL FOLDER @ $name-$list ]"
    }
}