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
package com.halcyoninae.halcyon.utils

import com.halcyoninae.cosmos.components.bottompane.filelist.TabTree
import com.halcyoninae.halcyon.connections.properties.ExternalResource
import com.halcyoninae.halcyon.connections.properties.ProgramResourceManager
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import javax.swing.SwingUtilities

/**
 * This class provides a globalized way
 * for certain tasks to be executed
 * without making the code too complex.
 *
 * @author Jack Meng
 * @author 3.1
 */
object Wrapper {
    /**
     * Launches a runnable in an async pool.
     *
     * @param runnable The runnable to be launched
     */
    @JvmStatic
    fun async(runnable: Runnable?) {
        if (ExternalResource.pm[ProgramResourceManager.KEY_PROGRAM_FORCE_OPTIMIZATION] == "true") {
            val threadpool = Executors.newFixedThreadPool(1)
            threadpool.submit(runnable)
        } else {
            CompletableFuture.runAsync(runnable)
        }
    }

    fun sort(tree: TabTree) {
        val f = File(tree.path).listFiles()
        Arrays.sort(f) { o1, o2 -> o1.name.compareTo(o2.name) }
    }

    /**
     * @param runnable
     */
    fun asyncSwingUtil(runnable: Runnable?) {
        SwingUtilities.invokeLater(runnable)
    }

    /**
     * Launches a Runnable in a precatched
     * Exception handler.
     *
     * @param runnable The task to run safely on.
     */
    fun safeLog(runnable: Runnable, isAsync: Boolean) {
        try {
            if (isAsync) async(runnable) else runnable.run()
        } catch (e: Exception) {
            ExternalResource.dispatchLog(e)
        }
    }

    /**
     * @param run
     */
    fun threadedRun(run: Runnable?) {
        val es = Executors.newWorkStealingPool()
        es.submit(run)
    }
}