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

import com.halcyoninae.halcyon.utils.Wrapper
import com.halcyoninae.tailwind.TailwindEvent.TailwindStatus
import com.halcyoninae.tailwind.TailwindListener.*

/**
 * A global scoped targeted towards managing multiple
 * Listeners for the BigContainer player all at the same time without
 * having to hold multiple Lists directly.
 *
 * @author Jack Meng
 * @since 3.1
 */
class TailwindEventManager {
    private val timeListeners: MutableList<TimeUpdateListener>
    private val statusUpdateListeners: MutableList<StatusUpdateListener>
    private val genericUpdateListeners: MutableList<GenericUpdateListener>

    /**
     * @return e
     */
    var frameBufferListeners: FrameBufferListener? = null
        private set

    init {
        timeListeners = ArrayList()
        statusUpdateListeners = ArrayList()
        genericUpdateListeners = ArrayList()
    }

    /**
     * @param e
     * @return boolean
     */
    fun addTimeListener(e: TimeUpdateListener): Boolean {
        return timeListeners.add(e)
    }

    /**
     * @param e
     * @return boolean
     */
    fun addStatusUpdateListener(e: StatusUpdateListener): Boolean {
        return statusUpdateListeners.add(e)
    }

    /**
     * @param e
     * @return boolean
     */
    fun addGenericUpdateListener(e: GenericUpdateListener): Boolean {
        return genericUpdateListeners.add(e)
    }

    /**
     * @param e
     */
    fun addFrameBufferListener(e: FrameBufferListener?) {
        frameBufferListeners = e
    }

    /**
     * @return e
     */
    fun getTimeListeners(): List<TimeUpdateListener> {
        return timeListeners
    }

    /**
     * @return e
     */
    fun getStatusUpdateListeners(): List<StatusUpdateListener> {
        return statusUpdateListeners
    }

    /**
     * @return e
     */
    fun getGenericUpdateListeners(): List<GenericUpdateListener> {
        return genericUpdateListeners
    }

    /**
     * @param time
     */
    @Synchronized
    fun dispatchTimeEvent(time: Long) {
        for (e in timeListeners) {
            e.trackCurrentTime(time)
        }
    }

    /**
     * @param status
     */
    @Synchronized
    fun dispatchStatusEvent(status: TailwindStatus?) {
        for (e in statusUpdateListeners) {
            e.statusUpdate(status)
        }
    }

    /**
     * @param event
     */
    @Synchronized
    fun dispatchGenericEvent(event: TailwindEvent?) {
        for (e in genericUpdateListeners) {
            e.genericUpdate(event)
        }
    }

    /**
     * @param samples
     * @param s_
     */
    @Synchronized
    fun dispatchNewBufferEvent(samples: FloatArray?, s_: Int) {
        Wrapper.threadedRun { frameBufferListeners!!.frameUpdate(samples, s_) }
    }
}