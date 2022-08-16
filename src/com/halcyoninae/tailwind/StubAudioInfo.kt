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

/**
 * Represents a faked AudioInfo object.
 *
 * @author Jack Meng
 * @since 3.3
 */
class StubAudioInfo(private val f: File) : AudioInfo() {
    private var tags: MutableMap<String, String>? = null

    init {
        initTags()
    }

    constructor(str: String?) : this(File(str)) {}

    override fun initTags() {
        tags = HashMap()
        (tags as HashMap<String, String>)[KEY_ABSOLUTE_FILE_PATH] = f.absolutePath
    }
}