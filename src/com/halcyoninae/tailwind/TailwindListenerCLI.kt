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

import com.halcyoninae.halcyon.debug.Debugger
import com.halcyoninae.tailwind.TailwindEvent.TailwindStatus
import com.halcyoninae.tailwind.TailwindListener.GenericUpdateListener
import com.halcyoninae.tailwind.TailwindListener.StatusUpdateListener

/**
 * @author Jack Meng
 * @since 3.1
 */
class TailwindListenerCLI : StatusUpdateListener, GenericUpdateListener {
    /**
     * @param status
     */
    override fun statusUpdate(status: TailwindStatus) {
        Debugger.warn("TailwindPLAYER> $status")
    }

    /**
     * @param event
     */
    override fun genericUpdate(event: TailwindEvent) {
        Debugger.warn("TailwindPLAYER> " + event.currentAudioInfo.getTag(AudioInfo.KEY_ABSOLUTE_FILE_PATH))
    }
}