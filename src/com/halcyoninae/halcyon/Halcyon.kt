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
package com.halcyoninae.halcyon

import com.halcyoninae.cosmos.Cosmos
import com.halcyoninae.cosmos.components.bottompane.bbloc.BBlocButton
import com.halcyoninae.cosmos.components.bottompane.bbloc.BBlocView
import com.halcyoninae.cosmos.components.bottompane.bbloc.buttons.*
import com.halcyoninae.cosmos.components.toppane.TopPane
import com.halcyoninae.cosmos.dialog.ConfirmWindow
import com.halcyoninae.cosmos.dialog.ConfirmWindow.ConfirmationListener
import com.halcyoninae.cosmos.dialog.ErrorWindow
import com.halcyoninae.cosmos.tasks.ThreadedScheduler
import com.halcyoninae.halcyon.connections.discord.Discordo
import com.halcyoninae.halcyon.connections.properties.ExternalResource
import com.halcyoninae.halcyon.connections.properties.ProgramResourceManager
import com.halcyoninae.halcyon.constant.ColorManager
import com.halcyoninae.halcyon.constant.Global
import com.halcyoninae.halcyon.constant.Manager
import com.halcyoninae.halcyon.debug.CLIStyles
import com.halcyoninae.halcyon.debug.Debugger
import com.halcyoninae.halcyon.debug.TConstr
import com.halcyoninae.halcyon.runtime.Program
import com.halcyoninae.halcyon.utils.TextParser
import com.halcyoninae.setup.Setup
import com.halcyoninae.setup.Setup.Companion.addSetupListener
import com.halcyoninae.setup.SetupListener
import com.halcyoninae.setup.SetupStatus
import java.awt.Dimension
import java.io.File
import java.util.*
import javax.swing.JSplitPane
import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException

/**
 *
 *
 * Halcyon Music Player Application main
 * entry point class.
 * <br></br>
 * The Halcyon Music Player is a 3.0 iteration
 * of the original MP4J project, which you can find
 * here: https://github.com/Exoad4JVM/mp4j.
 * However this program iteration is designed to be much
 * more optimized and have a better audio engine with less
 * restrictive licensing towards any end users, including,
 * me being the author of this program.
 * <br></br>
 * This program is licensed under the GPL-2.0 license:
 * https://www.gnu.org/licenses/gpl-2.0.html
 * <br></br>
 * A copy of the license must be attached to all distributions
 * and copies of this program, source code, and associated linked
 * libraries. If you have not received a copy of the license, please
 * contact me at: mailto://jackmeng0814@gmail.com
 *
 *
 * Any external libraries used by this program including the audio
 * engine are licensed separately and are included in this program,
 * and also any other subsequent programs that are distributed with
 * or utilizing this library.
 *
 * <br></br>
 * NOTICE: This program is made in the purpose for educational and private use,
 * the original author, Jack Meng and any other contributors, cannot be held
 * responsible for any damage, loss, or anything else that may occur due to the
 * usage
 * of this program. Nor, can such contributors can be held responsible for any
 * illegal activities, of those that voids a country's copyright, and/or
 * international
 * copyright laws.
 * <br></br>
 *
 *
 * This is the main class that starts the program.
 *
 *
 * It manages setting up the UI and reading any external
 * resources.
 *
 *
 * Besides this, this class should not pass any references nor
 * should this class be extended or instantiated in any way.
 *
 *
 * If there needs to be any objects that needs to be passed down,
 * the programmer must specify that as a global scope object in
 * [com.halcyoninae.halcyon.constant.Global].
 *
 *
 * @author Jack Meng
 * @see com.halcyoninae.halcyon.constant.Global
 *
 * @since 3.0
 */
object Halcyon {
    @JvmField
    var bgt: Cosmos? = null
    private fun boot_kick_mainUI() {
        try {
            Cosmos.refreshUI(ColorManager.programTheme)
        } catch (e: UnsupportedLookAndFeelException) {
            e.printStackTrace()
        }
        val tp = TopPane(Global.ifp, Global.bctp)
        Global.ifp.addInfoViewUpdateListener(Global.bctp)
        val bottom = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
        bottom.minimumSize = Dimension(Manager.MIN_WIDTH, Manager.MIN_HEIGHT / 2)
        bottom.preferredSize = Dimension(Manager.MIN_WIDTH, Manager.MIN_HEIGHT / 2)
        val bb = ArrayList<BBlocButton>()
        bb.add(AddFolder())
        bb.add(RefreshFileView())
        bb.add(SlidersControl())
        bb.add(MinimizePlayer())
        bb.add(Settings())
        bb.add(LegalNoticeButton())
        val b = BBlocView()
        b.addBBlockButtons(*bb.toTypedArray())
        bottom.add(b)
        bottom.add(Global.bp)
        val m = JSplitPane(JSplitPane.VERTICAL_SPLIT, tp, bottom)
        bgt = Cosmos(m)
        Global.bp.pokeNewFileListTab(Global.ll)
        val fi = Program.fetchSavedPlayLists()
        Debugger.warn("SPL_REC: " + Arrays.toString(Program.fetchLikedTracks()))
        if (fi.isNotEmpty()) {
            for (f in fi) {
                if (File(f.absolutePath).exists() && File(f.absolutePath).isDirectory) {
                    Global.bp.pokeNewFileListTab(f.absolutePath)
                    Debugger.good("Added playlist: " + f.absolutePath)
                } else {
                    Debugger.warn("Could not add playlist: " + f.absolutePath)
                }
            }
        }
        bgt!!.run()
    }

    private fun run() {
        try {
            addSetupListener(object : SetupListener {
                override fun updateStatus(e: SetupStatus?) {
                    boot_kick_mainUI()
                    if (ExternalResource.pm[ProgramResourceManager.KEY_USER_USE_DISCORD_RPC] == "true") {
                        Debugger.alert(TConstr(CLIStyles.CYAN_TXT, "Loading the almighty Discord RPC ;3"))
                        val dp = Discordo()
                        Global.ifp.addInfoViewUpdateListener(dp)
                        dp.start()
                    } else {
                        Debugger.warn("Amogus")
                    }
                }
            })
            Setup.main(null as Array<String?>?)
        } catch (e: Exception) {
            ExternalResource.dispatchLog(e)
        }
    }

    /**
     * No arguments are taken from the entry point
     *
     * @param args Null arguments
     */
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isNotEmpty()) {
            if (args[0] == "-debug") {
                DefaultManager.DEBUG_PROGRAM = true
                Debugger.DISABLE_DEBUGGER = false
            }
        }
        try {
            UIManager.setLookAndFeel(ColorManager.programTheme.laf.name)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            ExternalResource.checkResourceFolder(
                ProgramResourceManager.PROGRAM_RESOURCE_FOLDER
            )
            for (str in ProgramResourceManager.RESOURCE_SUBFOLDERS) {
                ExternalResource.createFolder(str)
            }
            ExternalResource.pm.checkAllPropertiesExistence()
            Debugger.good("Loading encoding as: " + TextParser.propertyTextEncodingName)
            ThreadedScheduler()
            if (ExternalResource.pm[ProgramResourceManager.KEY_PROGRAM_FORCE_OPTIMIZATION] == "false") {
                ConfirmWindow(
                    "You seemed to have turned off Forced Optimization, this can result in increased performance loss. It is best to keep it on!",
                    ConfirmationListener { status ->
                        if (status) {
                            run()
                        } else System.exit(0)
                    }).run()
            } else {
                run()
            }
        } catch (ex: Exception) {
            ErrorWindow(ex.toString()).run()
            ExternalResource.dispatchLog(ex)
        }
    }
}