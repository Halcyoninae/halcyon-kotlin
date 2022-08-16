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
package com.halcyoninae.halcyon.constant

import com.halcyoninae.cosmos.components.bottompane.BottomPane
import com.halcyoninae.cosmos.components.bottompane.filelist.LikeList
import com.halcyoninae.cosmos.components.moreapps.MoreApps
import com.halcyoninae.cosmos.components.toppane.layout.ButtonControlTP
import com.halcyoninae.cosmos.components.toppane.layout.InfoViewTP
import com.halcyoninae.cosmos.components.waveform.WaveForm
import com.halcyoninae.halcyon.connections.resource.ResourceDistributor
import com.halcyoninae.tailwind.wrapper.Player

/**
 * This class holds any public scoped Objects that may be used throughout
 * the program.
 *
 *
 * This class eliminates different classes having to hot potato pass
 * difference object instances to each other.
 *
 * @author Jack Meng
 * @since 3.0
 */
object Global {
    @kotlin.jvm.JvmField
    val rd = ResourceDistributor()
    @kotlin.jvm.JvmField
    var bp = BottomPane()
    var bctp = ButtonControlTP()
    @kotlin.jvm.JvmField
    var ifp = InfoViewTP()
    @kotlin.jvm.JvmField
    var player = Player()
    @kotlin.jvm.JvmField
    var ll = LikeList()
    @kotlin.jvm.JvmField
    var waveForm: WaveForm? = null // SHOULD NOT BE USED AT THE MOMENT, this feature currently needs a lot more

    // optimizations
    @kotlin.jvm.JvmField
    var moreApps = MoreApps()

    init {
        player.stream!!.addStatusUpdateListener(bctp)
    }
}