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
package com.halcyoninae.halcyon.connections.discord

import com.halcyoninae.cosmos.components.toppane.layout.InfoViewTP.InfoViewUpdateListener
import com.halcyoninae.halcyon.debug.CLIStyles
import com.halcyoninae.halcyon.debug.Debugger.alert
import com.halcyoninae.halcyon.debug.TConstr
import com.halcyoninae.halcyon.utils.TextParser.parseAsPure
import com.halcyoninae.tailwind.AudioInfo
import net.arikia.dev.drpc.DiscordEventHandlers
import net.arikia.dev.drpc.DiscordRPC
import net.arikia.dev.drpc.DiscordRichPresence
import net.arikia.dev.drpc.DiscordUser

/**
 * Represents the Discord Rich Presence Interfacing.
 *
 *
 * This class was originally found in 2.0 but I later
 * decided to change this and it is what it is now.
 *
 *
 * It should not be called by any external processes, and
 * should remain independent. Due to this, this class is completely
 * "lonely" and must rely completely on listeners and process calls
 * to function and/or update it's own state.
 *
 * @author Jack Meng
 * @since 2.0
 */
class Discordo : InfoViewUpdateListener {
    /**
     * Starts the dispatch of the RPC
     */
    fun start() {
        val handlers = DiscordEventHandlers.Builder()
            .setReadyEventHandler { user: DiscordUser ->
                alert(
                    TConstr(
                        arrayOf(CLIStyles.BOLD, CLIStyles.MAGENTA_BG),
                        "Launching Discord for user: " + user.username + "#" + user.discriminator + " | ID: "
                                + user.userId
                    )
                )
            }
            .build()
        DiscordRPC.discordInitialize(PROJECT_ID, handlers, true)
        rpc = DiscordRichPresence.Builder(
            STATE
                    + NOTHING_MUSIC
        )
            .setBigImage("logo", STATE)
            .build()
        DiscordRPC.discordUpdatePresence(rpc)
    }

    /**
     * @param title A title to dispatch as
     */
    fun set(title: String?) {
        val handlers = DiscordEventHandlers.Builder()
            .setReadyEventHandler { user: DiscordUser ->
                alert(
                    TConstr(
                        arrayOf(CLIStyles.BOLD, CLIStyles.MAGENTA_BG),
                        "Launching Discord for user: " + user.username + "#" + user.discriminator + " | ID: "
                                + user.userId
                    )
                )
            }
            .build()
        DiscordRPC.discordInitialize(PROJECT_ID, handlers, true)
        rpc = DiscordRichPresence.Builder(parseAsPure(title!!))
            .setBigImage("logo", STATE)
            .build()
        DiscordRPC.discordUpdatePresence(rpc)
    }

    /**
     * @param info
     */
    override fun infoView(info: AudioInfo) {
        set(info.getTag(AudioInfo.KEY_MEDIA_TITLE))
    }

    companion object {
        protected const val PROJECT_ID = "989355331761086475"
        private const val STATE = "Halcyon\n "
        private const val NOTHING_MUSIC = "Nothing"
        protected var rpc: DiscordRichPresence? = null
    }
}