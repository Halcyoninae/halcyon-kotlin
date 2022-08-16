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

import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*

/**
 * This class is localized meaning
 * that one should not use it for anything besides
 * this program.
 *
 *
 * This program provides a simple Replace On Demand
 * properties reader, instead of the default [java.util.Properties]
 * class.
 *
 *
 * A Replace On Demand means that it will not let the
 * the receiver decide what to do with the properties, but will
 * go by rules. If a value is deemed unacceptable, it will be
 * be replaced by the default value, and the original value in
 * the file will be overwritten. Another thing to note is that,
 * on demand means only checking the current value it is being called upon
 * [.get].
 * This class does not do a pre-check of all of the properties, unless the
 * properties file does not
 * exist or is empty, then everything will be replaced
 * [.createWithDefaultVals].
 *
 * @author Jack Meng
 * @since 2.1
 */
class PropertiesManager(
    private var map: Map<String?, String?>?, private val allowedProperties: Map<String?, PropertyValidator?>?,
    /**
     * Returns the current referenced properties file.
     *
     * @return The current referenced properties file.
     */
    val location: String
) {
    private val util: Properties
    private var fr: FileReader? = null

    /**
     * Creates a new PropertiesManager instance with the defined rules and
     * allowed-properties for the
     * given properties file.
     *
     * @param defaultProperties Contains a key and a value with the value being the
     * fallback value for
     * that key if the key's value in the properties is not
     * allowed. This is not an optional parameter to set as
     * null or anything that makes it not have a value.
     * @param allowedProperties Contains a key and an array of allowed properties as
     * rules, if the value from file's key does not match
     * any of the given rules, the PropertiesManager will
     * return the default property and alter the file. This
     * is an optional parameter, which can be that the
     * array can be empty (NOT NULL).
     * @param location          The location of the properties file
     */
    init {
        util = Properties()
    }
    /// BEGIN PRIVATE METHODS
    /**
     * Checks if the file has all the necessary properties' keys.
     *
     *
     * As previously stated, this is an Replace On Demand method, meaning that
     * this method does not care about the value of the properties, it only cares
     * if the key exists.
     *
     *
     * If a key does not exist, it will be created with the default value.
     */
    fun checkAllPropertiesExistence() {
        try {
            if (!File(location).exists() || !File(location).isFile) {
                File(location).createNewFile()
                createWithDefaultVals()
            }
            util.load(FileReader(location))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        for (key in allowedProperties!!.keys) {
            if (!util.containsKey(key)) {
                util.setProperty(key, map!![key])
            }
        }
        save()
    }

    /**
     * Creates an empty file with the location given in the constructor.
     */
    private fun wipeContents() {
        val f = File(location)
        if (f.isFile && f.exists()) {
            try {
                val writer = FileWriter(location)
                writer.write("")
                writer.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Writes to the file with the default values of all the properties.
     *
     *
     * It will also first wipe the contents of the file in order to prevent
     * overwrite.
     */
    private fun createWithDefaultVals() {
        wipeContents()
        try {
            fr = FileReader(location)
            util.load(fr)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        for ((key, value) in map!!) {
            util.setProperty(key, value)
        }
        save()
    }
    /// END PRIVATE METHODS
    /// BEGIN PUBLIC METHODS
    /**
     * Returns the Map of default properties provided in the constructor.
     *
     * @return The Map of default properties provided in the constructor.
     */
    /**
     * UNSAFE: Resets the default properties to something else.
     *
     *
     * It is unadvised to use this method, as it does not do a
     * pre-check of the properties of the file for all the properties after,
     * forcing the receiver to check for all the properties.
     *
     * @param map The new default properties.
     */
    var defaultProperties: Map<String?, String?>?
        get() = map
        set(map) {
            this.map = map
            createWithDefaultVals()
        }

    /**
     * Returns (true || false) based ont he existence of a key in the properties
     * file.
     *
     * @param key A key to check for
     * @return (true | | false) based on the existence of a key in the properties
     * file.
     */
    operator fun contains(key: String?): Boolean {
        return util.containsKey(key)
    }

    /**
     * Consults if the given key and value are allowed to be paired or
     * is allowed in general based on the rules provided in the constructor.
     *
     *
     * If the allowed-properties' string array is empty, then it will always return
     * true.
     *
     * @param key   The key to check for
     * @param value The value to check for
     * @return (true | | false) based the allowance of the value upon the key
     */
    fun allowed(key: String?, value: String): Boolean {
        return if (allowedProperties!!.containsKey(key)) {
            if (allowedProperties[key] == null) {
                true
            } else allowedProperties[key]!!.isValid(value)
        } else false
    }

    /**
     * Returns the value of the key in the properties file.
     *
     * @param key The key to get the value of
     * @return The value of the key in the properties file.
     */
    operator fun get(key: String?): String? {
        if (!File(location).exists() || !File(location).isFile) {
            try {
                File(location).createNewFile()
            } catch (ignored: IOException) {
                // IGNORED
            }
            createWithDefaultVals()
        }
        if (fr == null) {
            try {
                if (!File(location).exists() || !File(location).isFile) {
                    File(location).createNewFile()
                    createWithDefaultVals()
                }
                fr = FileReader(location)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            fr = FileReader(location)
            util.load(fr)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (util.getProperty(key) == null) {
            util.setProperty(key, map!![key])
            save()
        }
        return if (util.getProperty(key) == null || !allowed(
                key,
                util.getProperty(key)
            )
        ) map!![key] else util.getProperty(key)
    }

    /**
     * Sets the value of the key in the properties file.
     *
     * @param key      The key to set the value of
     * @param value    The value to set the key to
     * @param comments The comments to add to the file
     */
    operator fun set(key: String?, value: String?, comments: String?) {
        try {
            util.load(fr)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        util.setProperty(key, value)
        try {
            val writer = FileWriter(location)
            util.store(writer, comments)
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /**
     * Saves the properties file with comments.
     *
     * @param comments The comments to add to the top of the file.
     */
    /**
     * Save with no comments parameter
     */
    @JvmOverloads
    fun save(comments: String? = "") {
        try {
            util.store(FileWriter(location), comments)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Opens the properties file for editing and consulting.
     *
     * @return (true | | false) based on if the file was able to be opened.
     */
    fun open(): Boolean {
        if (!File(location).exists()) {
            val f = File(location)
            try {
                f.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
            createWithDefaultVals()
        } else {
            try {
                fr = FileReader(location)
                util.load(fr)
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }
        return true
    } /// END PUBLIC METHODS
}