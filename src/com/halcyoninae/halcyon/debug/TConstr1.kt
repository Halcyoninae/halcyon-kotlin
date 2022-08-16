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
package com.halcyoninae.halcyon.debug

import com.halcyoninae.halcyon.global.TObject
import java.util.*
import java.util.function.Consumer

/**
 * Custom Alert Debugger TextConstructor
 *
 * This class is read only once all its
 * attributes has been set (via the constructor)
 *
 * @author Jack Meng
 * @since 3.3
 */
class TConstr {
    private var payload: Array<TObject<*>>
    private var start: Array<CLIStyles>

    @SafeVarargs
    constructor(start: Array<CLIStyles>, vararg payload: T?) {
        this.payload = arrayOfNulls(payload.size)
        for (i in payload.indices) {
            this.payload[i] = TObject<T?>(payload[i])
        }
        this.start = start
    }

    constructor(start: CLIStyles, payload: T) {
        this.payload = arrayOf(TObject<T>(payload))
        this.start = arrayOf(start)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        Arrays.asList(*start).forEach(Consumer { x: CLIStyles -> sb.append(x.color) })
        Arrays.asList(*payload).forEach(Consumer { x: TObject<*> -> sb.append(x.first.toString()) })
        return sb.toString()
    }
}