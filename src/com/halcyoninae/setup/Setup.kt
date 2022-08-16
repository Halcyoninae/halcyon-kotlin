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
package com.halcyoninae.setup

import com.halcyoninae.cloudspin.enums.SpeedStyle
import com.halcyoninae.cloudspin.lib.gradient.GradientGenerator
import com.halcyoninae.cosmos.Cosmos
import com.halcyoninae.cosmos.dialog.ErrorWindow
import com.halcyoninae.cosmos.events.ForceMaxSize
import com.halcyoninae.cosmos.identifier.AttributableButton
import com.halcyoninae.cosmos.theme.ThemeBundles
import com.halcyoninae.halcyon.connections.properties.ExternalResource
import com.halcyoninae.halcyon.connections.properties.ProgramResourceManager
import com.halcyoninae.halcyon.constant.ColorManager
import com.halcyoninae.halcyon.constant.Global
import com.halcyoninae.halcyon.constant.Manager
import com.halcyoninae.halcyon.debug.Debugger
import com.halcyoninae.halcyon.utils.ColorTool
import com.halcyoninae.halcyon.utils.DeImage
import com.halcyoninae.halcyon.utils.DeImage.Directional
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.util.function.Consumer
import javax.swing.*
import kotlin.system.exitProcess

/**
 * This class runs a setup routine everytime
 * the main program is run. The setup routine helps
 * to make sure certain things are setup before the main
 * program is run; for example: making sure the correct
 * assets are present etc.. It also helps to setup
 * the program to a first time user.
 *
 *
 * To run this class, simply call the static main() instead
 * of creating a new object. Creating a new object will
 * only instantiate the GUI part and not the file access part.
 * Particularly it will be setup to handle some basic tasks, such as
 * determining what theme the user wants to use, etc.. All of this are run
 * after the threaded task schedulers have been started and the resource
 * properties have been loaded.
 *
 * @author Jack Meng
 * @since 3.3
 */
class Setup() : JFrame(), Runnable {
    private val content: JPanel

    // Java.AWT is stupid and its own
    // List class??!?!??!!?
    private val welcomeLabel: JLabel
    private val themeButtons: Array<AttributableButton?>

    init {
        title = "Setup Routine"
        iconImage = Global.rd.getFromAsImageIcon(Manager.PROGRAM_ICON_LOGO).image
        preferredSize = Dimension(350, 400)
        isResizable = true
        addComponentListener(
            ForceMaxSize(
                preferredSize.width, preferredSize.height,
                preferredSize.width, preferredSize.height
            )
        )
        content = JPanel()
        content.preferredSize = preferredSize
        content.isOpaque = false
        content.layout = BoxLayout(content, BoxLayout.Y_AXIS)
        val welcomePanel = JPanel()
        welcomePanel.layout = OverlayLayout(welcomePanel)
        welcomePanel.preferredSize = Dimension(350, 100)
        val welcomePanelBack: JPanel = object : JPanel() {
            override fun paint(g: Graphics) {
                super.paintComponent(g)
                val g2 = g as Graphics2D
                g2.composite = AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER,
                    0.5f
                )
                g2.drawImage(
                    DeImage.createGradient(
                        GradientGenerator.make(
                            SpeedStyle.QUALITY, welcomePanel.preferredSize,
                            ColorTool.rndColor(), ColorTool.rndColor(), false
                        ),
                        200, 0, Directional.TOP
                    ),
                    0, 0, null
                )
                g2.dispose()
            }
        }
        welcomePanelBack.preferredSize = welcomePanel.preferredSize
        welcomePanel.isOpaque = true
        welcomePanel.ignoreRepaint = true
        welcomePanel.addMouseListener(object : MouseListener {
            override fun mouseClicked(arg0: MouseEvent) {
                welcomePanelBack.repaint(150)
                welcomePanel.repaint(20)
                Runtime.getRuntime().gc()
            }

            override fun mouseEntered(arg0: MouseEvent) {}
            override fun mouseExited(arg0: MouseEvent) {}
            override fun mousePressed(arg0: MouseEvent) {}
            override fun mouseReleased(arg0: MouseEvent) {}
        })
        welcomeLabel = JLabel(
            "<html><p style=\"font-size:18px;\"><strong>Welcome :3</strong></p><p style=\"font-size:10px;color:#a6a6a6;\">Let's get started</p></html>"
        )
        welcomeLabel.preferredSize = welcomePanel.preferredSize
        welcomeLabel.foreground = ColorManager.MAIN_FG_THEME
        welcomeLabel.alignmentY = CENTER_ALIGNMENT
        welcomePanel.add(welcomeLabel)
        welcomePanel.add(welcomePanelBack)
        val buttonsCtrl = JPanel()
        buttonsCtrl.layout = FlowLayout(FlowLayout.RIGHT)
        val okButton = JButton("Moosic!")
        okButton.addActionListener({
            fireListeners()
            dispose()
        })
        val cancel = JButton("Quit")
        cancel.addActionListener {
            Debugger.warn("Failing setup... [0]")
            exitProcess(0)
        }
        buttonsCtrl.add(okButton)
        buttonsCtrl.add(cancel)
        val contentPanel = JScrollPane()
        contentPanel.border = BorderFactory.createEmptyBorder()
        contentPanel.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        contentPanel.preferredSize = Dimension(330, 250)
        contentPanel.minimumSize = contentPanel.preferredSize
        val contentPanelBack = JPanel()
        contentPanelBack.preferredSize = contentPanel.preferredSize
        contentPanelBack.layout = FlowLayout(FlowLayout.CENTER, 0, 0)
        val themeLabel = JLabel(
            "<html><p style=\"font-size:9px;\"><strong>Preferred Color Theme:</strong></p></html>"
        )
        val themes = ThemeBundles.themes
        val themePanel = JPanel()
        themePanel.preferredSize = contentPanelBack.preferredSize
        themeButtons = arrayOfNulls(themes.size)
        themePanel.layout = GridLayout(themes.size + 1, 1)
        themePanel.add(themeLabel)
        for (i in themeButtons.indices) {
            themeButtons[i] = AttributableButton(themes[i].themeName)
            themeButtons[i]!!.attribute = themes[i].canonicalName
            themePanel.add(themeButtons[i])
            val t = themeButtons[i]
            t!!.addActionListener {
                try {
                    Cosmos.refreshUI(ThemeBundles.searchFor(t.attribute))
                } catch (e1: UnsupportedLookAndFeelException) {
                    ErrorWindow(e1.message).run()
                    ExternalResource.dispatchLog(e1)
                }
            }
        }
        contentPanelBack.add(themePanel)
        contentPanel.viewport.add(contentPanelBack)
        content.add(welcomePanel)
        content.add(contentPanel)
        content.add(buttonsCtrl)
        defaultCloseOperation = DISPOSE_ON_CLOSE
        contentPane.add(content)
    }

    /**
     * Is called upon to send a notice to the file service
     * writer that we need to place the setup lock file signifying
     * that the user has performed a setup
     */
    fun dispatchSetupYay() {
        if (!MAIN_LOCK_USER_SETUP.exists() || !MAIN_LOCK_USER_SETUP.isFile) {
            try {
                MAIN_LOCK_USER_SETUP.createNewFile()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            try {
                PrintWriter(MAIN_LOCK_USER_SETUP).use { bos ->
                    bos.write("Hello! This is just a file to keep track of whether you have setup the program or not :)")
                    bos.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                ErrorWindow(e.message).run()
            }
        }
    }

    /**
     * Any components to be added to the GUI
     * part of the program.
     *
     * @param components The components to be added to the GUI
     */
    fun addContent(vararg components: JComponent?) {
        for (c: JComponent? in components) {
            content.add(c)
            content.revalidate()
        }
    }

    override fun run() {
        pack()
        setLocation(
            (Toolkit.getDefaultToolkit().screenSize.width - this.preferredSize.width) / 2,
            (Toolkit.getDefaultToolkit().screenSize.height - this.preferredSize.height) / 2
        )
        isVisible = true
        Debugger.warn("Running setup routine...")
    }

    companion object {
        val MAIN_LOCK_USER_SETUP = File(
            (ProgramResourceManager.PROGRAM_RESOURCE_FOLDER + ProgramResourceManager.FILE_SLASH
                    + ProgramResourceManager.RESOURCE_SUBFOLDERS[2]
                    + ProgramResourceManager.FILE_SLASH + "__user.lock")
        )
        const val KILL_ARG = "kill"

        @Transient
        private val listener: MutableList<SetupListener> = ArrayList() // must specify java.util

        // cuz
        var SETUP_EXISTS = false
        private fun fireListeners() {
            listener.forEach(Consumer { x: SetupListener -> x.updateStatus(if (SETUP_EXISTS) SetupStatus.PASSED else SetupStatus.NEW) })
        }

        @JvmStatic
        fun addSetupListener(vararg listeners: SetupListener) {
            for (e: SetupListener in listeners) {
                listener.add(e)
            }
        }

        /**
         * Any applications that wants to the run a setup routine
         * SHOULD ONLY CALL THE MAIN METHOD.
         * Creating a new Setup object will only gurantee a launch
         * of the GUI part and can confuse any potential users.
         *
         * @param args No arguments are accepted / used in the setup routine
         */
        @JvmStatic
        fun main(args: Array<String?>?) {
            if (args != null && args.isNotEmpty()) {
                if ((args[0] == KILL_ARG) && MAIN_LOCK_USER_SETUP.isFile && MAIN_LOCK_USER_SETUP.exists()) {
                    MAIN_LOCK_USER_SETUP.delete()
                }
            }
            if (MAIN_LOCK_USER_SETUP.isFile && MAIN_LOCK_USER_SETUP.exists()) {
                SETUP_EXISTS = true
                Debugger.info("Found a setup to be already run. Not deploying GUI")
                fireListeners()
            } else {
                SETUP_EXISTS = false
                Debugger.info("Failed to find a lock. Deploying GUI")
                val s = Setup()
                s.run()
                s.dispatchSetupYay()
            }
        }
    }
}