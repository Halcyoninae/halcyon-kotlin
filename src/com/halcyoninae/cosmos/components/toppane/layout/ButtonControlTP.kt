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
package com.halcyoninae.cosmos.components.toppane.layout

import com.halcyoninae.halcyon.cacher.MoosicCache.pingLikedTracks
import com.halcyoninae.halcyon.cacher.MoosicCache.pingSavedPlaylists
import com.halcyoninae.halcyon.connections.properties.PropertiesManager.get
import com.halcyoninae.halcyon.utils.ColorTool.nullColor
import com.halcyoninae.halcyon.utils.ColorTool.hexToRGBA
import com.halcyoninae.halcyon.connections.resource.ResourceDistributor.getFromAsImageIcon
import com.halcyoninae.tailwind.wrapper.Player.stream
import com.halcyoninae.halcyon.utils.TimeParser.fromSeconds
import com.halcyoninae.halcyon.debug.Debugger.info
import com.halcyoninae.halcyon.cacher.MoosicCache.pingExcludedTracks
import com.halcyoninae.halcyon.filesystem.PhysicalFolder.absolutePath
import com.halcyoninae.halcyon.runtime.Program.forceSaveUserConf
import com.halcyoninae.halcyon.connections.properties.ExternalResource.dispatchLog
import com.halcyoninae.halcyon.utils.DeImage.resizeImage
import com.halcyoninae.halcyon.utils.DeImage.createGradientVertical
import com.halcyoninae.halcyon.utils.DeImage.createGradient
import com.halcyoninae.halcyon.utils.DeImage.imageIconToBI
import com.halcyoninae.halcyon.utils.DeImage.resizeNoDistort
import com.halcyoninae.halcyon.utils.DeImage.createRoundedBorder
import com.halcyoninae.halcyon.debug.Debugger.warn
import com.halcyoninae.tailwind.wrapper.Player.setVolume
import com.halcyoninae.tailwind.wrapper.Player.convertVolume
import com.halcyoninae.tailwind.wrapper.Player.setFile
import com.halcyoninae.tailwind.wrapper.Player.play
import com.halcyoninae.tailwind.wrapper.Player.requestNextTrack
import com.halcyoninae.tailwind.wrapper.Player.requestPreviousTrack
import com.halcyoninae.halcyon.debug.Debugger.log
import com.halcyoninae.halcyon.connections.properties.Property.Companion.filterProperties
import com.halcyoninae.halcyon.connections.properties.PropertiesManager.save
import com.halcyoninae.halcyon.utils.Wrapper.async
import com.halcyoninae.halcyon.filesystem.FileParser.isEmptyFolder
import com.halcyoninae.halcyon.filesystem.FileParser.contains
import com.halcyoninae.halcyon.utils.DeImage.resize
import com.halcyoninae.halcyon.filesystem.PhysicalFolder.name
import com.halcyoninae.halcyon.filesystem.PhysicalFolder.getFiles
import com.halcyoninae.halcyon.cacher.MoosicCache.isExcluded
import com.halcyoninae.halcyon.runtime.Program.fetchLikedTracks
import com.halcyoninae.halcyon.filesystem.VirtualFolder.removeFile
import com.halcyoninae.halcyon.debug.Debugger.good
import com.halcyoninae.halcyon.filesystem.VirtualFolder.asListFiles
import com.halcyoninae.halcyon.filesystem.VirtualFolder.addFile
import com.halcyoninae.halcyon.filesystem.VirtualFolder.files
import com.halcyoninae.halcyon.cacher.MoosicCache.savedPlaylists
import com.halcyoninae.halcyon.utils.GUITools.getAllComponents
import com.halcyoninae.halcyon.constant.ColorManager.refreshColors
import com.halcyoninae.cosmos.components.bottompane.BottomPane
import java.lang.Runnable
import com.halcyoninae.cosmos.tasks.ConcurrentTiming
import java.lang.InterruptedException
import com.halcyoninae.halcyon.connections.properties.ExternalResource
import com.halcyoninae.halcyon.connections.properties.ProgramResourceManager
import java.io.PrintStream
import javax.swing.plaf.ColorUIResource
import com.halcyoninae.halcyon.constant.ColorManager
import com.halcyoninae.halcyon.utils.ColorTool
import java.util.logging.LogManager
import java.util.Enumeration
import javax.swing.plaf.FontUIResource
import com.halcyoninae.cosmos.tasks.PingFileView
import com.halcyoninae.cosmos.tasks.DefunctOptimizer
import com.halcyoninae.cosmos.theme.Theme
import javax.swing.plaf.basic.BasicLookAndFeel
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme
import com.halcyoninae.cosmos.theme.ThemeType
import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme
import com.halcyoninae.cosmos.theme.bundles.DarkGreen
import com.halcyoninae.cosmos.theme.bundles.DarkOrange
import com.halcyoninae.cosmos.theme.bundles.LightGreen
import com.halcyoninae.cosmos.theme.bundles.LightOrange
import com.halcyoninae.cosmos.theme.ThemeBundles
import com.halcyoninae.cosmos.dialog.ErrorWindow
import com.halcyoninae.cosmos.dialog.ConfirmWindow.ConfirmationListener
import com.halcyoninae.cosmos.dialog.ConfirmWindow
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import kotlin.jvm.JvmOverloads
import com.halcyoninae.tailwind.TailwindListener.StatusUpdateListener
import javax.swing.event.ChangeListener
import javax.swing.event.ChangeEvent
import com.halcyoninae.tailwind.TailwindEvent.TailwindStatus
import com.halcyoninae.tailwind.AudioInfo
import kotlin.jvm.Synchronized
import com.halcyoninae.cosmos.dialog.AudioInfoDialog
import java.lang.StringBuilder
import com.halcyoninae.halcyon.utils.TimeParser
import com.halcyoninae.halcyon.Halcyon
import com.halcyoninae.cosmos.dialog.SelectApplicableFolders.FolderSelectedListener
import javax.swing.filechooser.FileSystemView
import javax.swing.filechooser.FileView
import java.io.File
import com.halcyoninae.cosmos.inheritable.FSVDefault
import com.halcyoninae.cosmos.components.bottompane.filelist.TabTree
import com.halcyoninae.cosmos.events.FVRightClick.RightClickHideItemListener
import java.lang.NullPointerException
import com.halcyoninae.cosmos.components.bottompane.filelist.TabTree.TabTreeSortMethod
import com.halcyoninae.cosmos.dialog.StraightTextDialog
import com.halcyoninae.halcyon.constant.StringManager
import java.awt.event.ComponentListener
import java.awt.event.ComponentEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent
import javax.swing.plaf.LayerUI
import com.halcyoninae.cosmos.special.BlurLayer
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import java.awt.image.ConvolveOp
import java.awt.image.Kernel
import com.halcyoninae.cloudspin.CloudSpinFilters
import com.halcyoninae.cosmos.components.info.InformationTab
import com.halcyoninae.cosmos.components.bottompane.bbloc.buttons.LegalNoticeButton
import java.io.BufferedReader
import java.io.FileReader
import com.halcyoninae.cosmos.components.info.layout.FileFromSourceTab
import com.halcyoninae.cosmos.components.info.layout.SystemTab
import com.halcyoninae.cosmos.components.info.layout.DebuggerTab
import com.halcyoninae.cosmos.components.toppane.layout.ButtonControlTP
import com.halcyoninae.cosmos.components.toppane.TopPane
import com.halcyoninae.halcyon.utils.DeImage
import com.halcyoninae.cosmos.components.toppane.layout.buttoncontrol.TimeControlSubTP
import com.halcyoninae.cosmos.components.toppane.layout.InfoViewTP
import com.halcyoninae.cosmos.components.toppane.layout.InfoViewTP.InfoViewUpdateListener
import com.halcyoninae.cloudspin.CloudSpin
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import java.util.HashMap
import org.jaudiotagger.audio.exceptions.CannotReadException
import java.io.IOException
import org.jaudiotagger.tag.TagException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import com.halcyoninae.cosmos.inheritable.LikeButton
import java.awt.event.ComponentAdapter
import com.halcyoninae.cosmos.components.bottompane.filelist.FileList
import com.halcyoninae.cosmos.components.moreapps.MoreAppsManager
import com.halcyoninae.cosmos.components.moreapps.MoreApps
import java.awt.event.MouseMotionAdapter
import java.lang.UnsupportedOperationException
import com.halcyoninae.cosmos.components.settings.SettingsTabs
import com.halcyoninae.halcyon.connections.properties.Property.PropertyFilterType
import com.halcyoninae.cosmos.components.settings.SettingsPane
import com.halcyoninae.cosmos.components.settings.tabs.properties.AudioSettings
import com.halcyoninae.tailwind.TailwindListener.FrameBufferListener
import com.halcyoninae.cosmos.components.waveform.WaveFormManager
import com.halcyoninae.cosmos.components.waveform.WaveForm
import com.halcyoninae.cosmos.components.waveform.WaveFormClickMenu
import com.halcyoninae.cosmos.components.waveform.WaveFormPane
import com.halcyoninae.cosmos.components.bottompane.bbloc.BBlocButton
import com.halcyoninae.cosmos.dialog.SelectApplicableFolders
import com.halcyoninae.halcyon.filesystem.FileParser
import com.halcyoninae.cosmos.components.bottompane.bbloc.buttons.MoreButton
import com.halcyoninae.cosmos.components.minimizeplayer.MiniPlayer
import com.halcyoninae.cosmos.components.minimizeplayer.MiniPlayerListener
import com.halcyoninae.cosmos.dialog.SlidersDialog
import com.halcyoninae.cosmos.components.info.InformationDialog
import com.halcyoninae.cosmos.components.bottompane.bbloc.buttons.GenericWebsiteLinker.WebsitePage
import com.halcyoninae.halcyon.filesystem.PhysicalFolder
import com.halcyoninae.halcyon.filesystem.VirtualFolder
import javax.swing.tree.TreeSelectionModel
import javax.swing.tree.DefaultTreeCellRenderer
import com.halcyoninae.cosmos.events.FVRightClick
import java.lang.IllegalArgumentException
import java.util.Collections
import com.halcyoninae.cosmos.components.bottompane.filelist.LikeList
import java.util.Arrays
import com.halcyoninae.cosmos.inheritable.TabButton
import com.halcyoninae.cosmos.inheritable.TabButton.RemoveTabListener
import javax.swing.border.Border
import com.halcyoninae.cosmos.components.minimizeplayer.MiniContentPane
import com.halcyoninae.cosmos.components.minimizeplayer.MiniPlayerManager
import java.awt.geom.RoundRectangle2D
import com.halcyoninae.cosmos.components.minimizeplayer.MiniPlayerClickMenu
import com.halcyoninae.cloudspin.lib.blurhash.BlurHash
import com.halcyoninae.cosmos.components.minimizeplayer.MiniDeImage
import com.halcyoninae.halcyon.utils.DeImage.Directional
import java.beans.ConstructorProperties
import com.halcyoninae.cosmos.events.InstantClose
import kotlin.Throws
import java.lang.ClassNotFoundException
import java.lang.InstantiationException
import java.lang.IllegalAccessException
import com.halcyoninae.halcyon.utils.GUITools
import com.halcyoninae.cosmos.inheritable.TabButton.CloseTabButton
import com.halcyoninae.halcyon.constant.Global
import com.halcyoninae.halcyon.constant.Manager
import java.awt.*
import javax.swing.*
import javax.swing.plaf.basic.BasicLabelUI

/**
 * This class represents the GUI component collection
 * class that maintains all of the buttons like:
 * play, forward, volume.
 *
 *
 * This component is located under the
 * [com.halcyoninae.cosmos.components.toppane.layout.InfoViewTP]
 * component.
 *
 * @author Jack Meng
 * @since 3.0
 */
class ButtonControlTP : JPanel(), InfoViewUpdateListener, ActionListener, ChangeListener, StatusUpdateListener {
    /// ButtonControl Config START
    val PLAY_PAUSE_ICON_SIZE = 40
    val OTHER_BUTTONS_SIZE = 24
    val BUTTONCTRL_PLAY_PAUSE_ICON = Manager.RSC_FOLDER_NAME + "/buttoncontrol/play_button.png"
    val BUTTONCTRL_PAUSE_PLAY_ICON = Manager.RSC_FOLDER_NAME + "/buttoncontrol/pause_button.png"
    val BUTTONCTRL_FWD_ICON = Manager.RSC_FOLDER_NAME + "/buttoncontrol/forward_button.png"
    val BUTTONCTRL_BWD_ICON = Manager.RSC_FOLDER_NAME + "/buttoncontrol/backward_button.png"
    val BUTTONCTRL_LOOP_ICON = Manager.RSC_FOLDER_NAME + "/buttoncontrol/loop_button.png"
    val BUTTONCTRL_SHUFFLE_ICON = Manager.RSC_FOLDER_NAME + "/buttoncontrol/shuffle_button.png"
    val BUTTONCTRL_MUTED_ICON = Manager.RSC_FOLDER_NAME + "/buttoncontrol/mute_button.png"
    val BUTTONCTRL_NOMUTED_ICON = Manager.RSC_FOLDER_NAME + "/buttoncontrol/nomute_button.png"
    val BUTTONCTRL_LIKE_ICON = Manager.RSC_FOLDER_NAME + "/buttoncontrol/like_button.png"
    val BUTTONCTRL_NOLIKE_ICON = Manager.RSC_FOLDER_NAME + "/buttoncontrol/nolike_button.png"
    val BUTTONCTRL_RESTART_ICON = Manager.RSC_FOLDER_NAME + "/buttoncontrol/restart_button.png"
    val BUTTONCONTROL_SHUFFLE_ICON_PRESSED = (Manager.RSC_FOLDER_NAME
            + "/buttoncontrol/shuffle_button_pressed.png")
    val BUTTONCONTROL_LOOP_ICON_PRESSED = (Manager.RSC_FOLDER_NAME
            + "/buttoncontrol/loop_button_pressed.png")
    val BUTTONCONTROL_MAX_WIDTH = Manager.MAX_WIDTH
    val BUTTONCONTROL_MAX_HEIGHT = Manager.MAX_HEIGHT / 4
    private val playButton: JButton
    private val nextButton: JButton
    private val previousButton: JButton
    private val loopButton: JButton
    private val shuffleButton: JButton
    private val restartButton: JButton
    private val likeButton: LikeButton
    private val progressSlider: JSlider
    private val volumeSlider: JSlider
    private val sliders: JPanel
    private val buttons: JPanel
    private var tsp: TimeControlSubTP

    @Transient
    private var aif: AudioInfo? = null
    private var hasPlayed = false

    /// ButtonControl Config END
    init {
        preferredSize = Dimension(BUTTONCONTROL_MIN_WIDTH, BUTTONCONTROL_MIN_HEIGHT)
        minimumSize = Dimension(BUTTONCONTROL_MIN_WIDTH, BUTTONCONTROL_MIN_HEIGHT)
        isOpaque = false
        layout = GridLayout(2, 1)
        buttons = JPanel()
        buttons.preferredSize = Dimension(BUTTONCONTROL_MIN_WIDTH, BUTTONCONTROL_MIN_HEIGHT / 2)
        buttons.minimumSize = Dimension(BUTTONCONTROL_MIN_WIDTH, BUTTONCONTROL_MIN_HEIGHT / 2)
        buttons.layout = FlowLayout(FlowLayout.LEFT, 10, preferredSize.height / 6)
        playButton = JButton(
            resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCTRL_PLAY_PAUSE_ICON),
                PLAY_PAUSE_ICON_SIZE, PLAY_PAUSE_ICON_SIZE
            )
        )
        playButton.background = null
        playButton.border = null
        playButton.toolTipText = "Play/Pause"
        playButton.addActionListener(this)
        playButton.isContentAreaFilled = false
        playButton.isRolloverEnabled = false
        playButton.isBorderPainted = false
        nextButton = JButton(
            resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCTRL_FWD_ICON),
                OTHER_BUTTONS_SIZE, OTHER_BUTTONS_SIZE
            )
        )
        nextButton.background = null
        nextButton.border = null
        nextButton.isContentAreaFilled = false
        nextButton.toolTipText = "Next track"
        nextButton.isRolloverEnabled = false
        nextButton.isBorderPainted = false
        nextButton.addActionListener(this)
        previousButton = JButton(
            resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCTRL_BWD_ICON),
                OTHER_BUTTONS_SIZE, OTHER_BUTTONS_SIZE
            )
        )
        previousButton.background = null
        previousButton.border = null
        previousButton.toolTipText = "Previous track"
        previousButton.isContentAreaFilled = false
        previousButton.isRolloverEnabled = false
        previousButton.isBorderPainted = false
        previousButton.addActionListener(this)
        restartButton = JButton(
            resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCTRL_RESTART_ICON),
                OTHER_BUTTONS_SIZE, OTHER_BUTTONS_SIZE
            )
        )
        restartButton.background = null
        restartButton.border = null
        restartButton.isContentAreaFilled = false
        restartButton.toolTipText = "Restart"
        restartButton.isRolloverEnabled = false
        restartButton.isBorderPainted = false
        restartButton.addActionListener(this)
        loopButton = JButton(
            resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCTRL_LOOP_ICON),
                OTHER_BUTTONS_SIZE,
                OTHER_BUTTONS_SIZE
            )
        )
        loopButton.background = null
        loopButton.border = null
        loopButton.isContentAreaFilled = false
        loopButton.toolTipText = "Loop"
        loopButton.isRolloverEnabled = false
        loopButton.isBorderPainted = false
        loopButton.addActionListener(this)
        shuffleButton = JButton(
            resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCTRL_SHUFFLE_ICON),
                OTHER_BUTTONS_SIZE,
                OTHER_BUTTONS_SIZE
            )
        )
        shuffleButton.background = null
        shuffleButton.border = null
        shuffleButton.isContentAreaFilled = false
        shuffleButton.toolTipText = "Shuffle"
        shuffleButton.isRolloverEnabled = false
        shuffleButton.isBorderPainted = false
        shuffleButton.addActionListener(this)
        volumeSlider = JSlider(0, 100)
        volumeSlider.foreground = ColorManager.MAIN_FG_THEME
        volumeSlider.border = null
        volumeSlider.preferredSize = Dimension(BUTTONCONTROL_MIN_WIDTH / 4, 20)
        volumeSlider.minimumSize = volumeSlider.preferredSize
        volumeSlider.addChangeListener(this)
        volumeSlider.toolTipText = volumeSlider.value.toString() + "%"
        likeButton = LikeButton(
            resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCTRL_NOLIKE_ICON),
                OTHER_BUTTONS_SIZE,
                OTHER_BUTTONS_SIZE
            ),
            resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCTRL_LIKE_ICON),
                OTHER_BUTTONS_SIZE,
                OTHER_BUTTONS_SIZE
            )
        )
        likeButton.background = null
        likeButton.border = null
        likeButton.isEnabled = false
        likeButton.isContentAreaFilled = false
        buttons.add(volumeSlider)
        buttons.add(restartButton)
        buttons.add(shuffleButton)
        buttons.add(previousButton)
        buttons.add(playButton)
        buttons.add(nextButton)
        buttons.add(loopButton)
        buttons.add(likeButton)
        sliders = JPanel()
        sliders.layout = BoxLayout(sliders, BoxLayout.Y_AXIS)
        sliders.preferredSize = Dimension(BUTTONCONTROL_MIN_WIDTH, BUTTONCONTROL_MIN_HEIGHT / 2)
        sliders.minimumSize = Dimension(BUTTONCONTROL_MIN_WIDTH, BUTTONCONTROL_MIN_HEIGHT / 2)
        progressSlider = JSlider(0, 100)
        progressSlider.value = 0
        progressSlider.isFocusable = false
        progressSlider.foreground = ColorManager.MAIN_FG_THEME
        progressSlider.background = ColorManager.MAIN_BG_THEME
        progressSlider.border = null
        progressSlider.putClientProperty("Slider.thumbSize", Dimension(0, 0))
        progressSlider.putClientProperty("Slider.trackWidth", 15)
        progressSlider.putClientProperty("Slider.thumBorderWidth", 0)
        progressSlider.alignmentX = CENTER_ALIGNMENT
        progressSlider.addChangeListener(this)
        Thread {
            while (true) {
                if (Global.player.stream!!.isPlaying) {
                    if (Global.player.stream!!.length > 0) {
                        progressSlider.value = (Global.player.stream!!.position * progressSlider.maximum
                                / Global.player.stream!!.length).toInt()
                        progressSlider.toolTipText = String.format(
                            "%d:%02d / %d:%02d",
                            (Global.player.stream!!.position / 60000).toInt(),
                            (Global.player.stream!!.position % 60000).toInt() / 1000,
                            (Global.player.stream!!.length / 60000).toInt(),
                            (Global.player.stream!!.length % 60000).toInt() / 1000
                        )
                    } else {
                        progressSlider.value = 0
                        progressSlider.toolTipText = "0:00 / 0:00"
                    }
                }
                try {
                    Thread.sleep(30)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
        tsp = TimeControlSubTP()
        sliders.add(Box.createVerticalStrut(BUTTONCONTROL_BOTTOM_TOP_BUDGET / 2))
        sliders.add(progressSlider)
        sliders.add(Box.createVerticalStrut(BUTTONCONTROL_BOTTOM_TOP_BUDGET / 2))
        sliders.add(tsp)
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                progressSlider.maximum = width - 10
                volumeSlider.preferredSize = Dimension(preferredSize.width / 4, 20)
                buttons.layout = FlowLayout(FlowLayout.CENTER, e.component.width / 35, preferredSize.height / 6)
                buttons.revalidate()
                volumeSlider.revalidate()
            }
        })
        tsp = TimeControlSubTP()
        add(buttons)
        add(sliders)
    }

    /**
     * Sets the volume to the current slider's volume
     * if the stream is reset.
     */
    private fun assertVolume() {
        Global.player.setVolume(Global.player.convertVolume(volumeSlider.value.toFloat()))
    }

    /**
     * @param info
     */
    override fun infoView(info: AudioInfo) {
        if (aif != null
            && aif!!.getTag(AudioInfo.KEY_ABSOLUTE_FILE_PATH) != info.getTag(AudioInfo.KEY_ABSOLUTE_FILE_PATH)
        ) {
            Global.player.setFile(aif!!.getTag(AudioInfo.KEY_ABSOLUTE_FILE_PATH))
        }
        aif = info
        if (!likeButton.isEnabled) likeButton.isEnabled = true
        if (Global.ll.isLiked(aif!!.getTag(AudioInfo.KEY_ABSOLUTE_FILE_PATH))) {
            likeButton.like()
        } else {
            likeButton.noLike()
        }
        if (Global.player.stream!!.isPlaying) {
            Global.player.stream!!.stop()
            Global.player.stream!!.close()
        }
        if (hasPlayed) {
            hasPlayed = false
        }
        progressSlider.value = 0
    }

    /**
     * @param isLooping
     */
    fun callLoopFeatures(isLooping: Boolean) {
        if (isLooping) {
            loopButton.icon = resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCONTROL_LOOP_ICON_PRESSED),
                OTHER_BUTTONS_SIZE,
                OTHER_BUTTONS_SIZE
            )
        } else {
            loopButton.icon = resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCTRL_LOOP_ICON),
                OTHER_BUTTONS_SIZE,
                OTHER_BUTTONS_SIZE
            )
        }
    }

    /**
     * @param isShuffling
     */
    fun callShuffleFeatures(isShuffling: Boolean) {
        if (isShuffling) {
            shuffleButton.icon = resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCONTROL_SHUFFLE_ICON_PRESSED),
                OTHER_BUTTONS_SIZE, OTHER_BUTTONS_SIZE
            )
        } else {
            shuffleButton.icon = resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCTRL_SHUFFLE_ICON),
                OTHER_BUTTONS_SIZE, OTHER_BUTTONS_SIZE
            )
        }
    }

    /**
     * @param isLoop
     */
    private fun loopVShuffleDuel(isLoop: Boolean) {
        if (isLoop && Global.player.isShuffling()) {
            Global.player.setShuffling(false)
            Global.player.setLooping(true)
            callShuffleFeatures(Global.player.isShuffling())
            callLoopFeatures(Global.player.isLooping())
        } else if (!isLoop && Global.player.isLooping()) {
            Global.player.setLooping(false)
            Global.player.setShuffling(true)
            callLoopFeatures(Global.player.isLooping())
            callShuffleFeatures(Global.player.isShuffling())
        } else if (isLoop) {
            Global.player.setLooping(!Global.player.isLooping())
            callLoopFeatures(Global.player.isLooping())
        } else if (!isLoop) {
            Global.player.setShuffling(!Global.player.isShuffling())
            callShuffleFeatures(Global.player.isShuffling())
        }
        warn<Any>("Status: " + Global.player.isLooping().toString() + " " + Global.player.isShuffling())
    }

    /**
     * @param e
     */
    override fun actionPerformed(e: ActionEvent) {
        if (e.source == playButton) {
            if (aif != null) {
                if (!Global.player.stream!!.isPlaying) {
                    if (!hasPlayed) {
                        Global.player.setFile(aif!!.getTag(AudioInfo.KEY_ABSOLUTE_FILE_PATH))
                        Global.player.play()
                        hasPlayed = true
                    } else {
                        Global.player.stream!!.resume()
                    }
                    assertVolume()
                } else {
                    Global.player.stream!!.pause()
                }
            }
            /*
            if (!flip) {
                playButton
                        .setIcon(DeImage.resizeImage(Global.rd.getFromAsImageIcon(BUTTONCTRL_PAUSE_PLAY_ICON),
                                PLAY_PAUSE_ICON_SIZE, PLAY_PAUSE_ICON_SIZE));
            } else {
                playButton
                        .setIcon(DeImage.resizeImage(Global.rd.getFromAsImageIcon(BUTTONCTRL_PLAY_PAUSE_ICON),
                                PLAY_PAUSE_ICON_SIZE, PLAY_PAUSE_ICON_SIZE));
            }
            flip = !flip;
            */
        } else if (e.source == restartButton) {
            Global.player.stream!!.reset()
            Global.player.play()
            assertVolume()
        } else if (e.source == nextButton) {
            Global.player.requestNextTrack()
        } else if (e.source == previousButton) {
            Global.player.requestPreviousTrack()
        } else if (e.source == loopButton) {
            loopVShuffleDuel(true)
        } else if (e.source == shuffleButton) {
            loopVShuffleDuel(false)
        }
        loopButton.repaint(200)
        shuffleButton.repaint(200)
    }

    /**
     * @param e
     */
    @Synchronized
    override fun stateChanged(e: ChangeEvent) {
        if (e.source == volumeSlider) {
            Thread {
                try {
                    Global.player.setVolume(Global.player.convertVolume(volumeSlider.value.toFloat()))
                    volumeSlider.toolTipText = volumeSlider.value.toString() + "%"
                } catch (ex: NullPointerException) {
                    log(ex)
                }
            }.start()
        }
    }

    override fun statusUpdate(status: TailwindStatus?) {
        warn("Got ButtonCtrl: " + status!!.name)
        if (status == TailwindStatus.PLAYING || status == TailwindStatus.RESUMED) {
            playButton.icon = resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCTRL_PAUSE_PLAY_ICON),
                PLAY_PAUSE_ICON_SIZE, PLAY_PAUSE_ICON_SIZE
            )
        } else if (status == TailwindStatus.PAUSED || status == TailwindStatus.CLOSED || status == TailwindStatus.END) {
            playButton.icon = resizeImage(
                Global.rd.getFromAsImageIcon(BUTTONCTRL_PLAY_PAUSE_ICON),
                PLAY_PAUSE_ICON_SIZE, PLAY_PAUSE_ICON_SIZE
            )
        }
    }

    companion object {
        const val BUTTONCONTROL_MIN_WIDTH = Manager.MIN_WIDTH
        const val BUTTONCONTROL_MIN_HEIGHT = Manager.MIN_HEIGHT / 4
        const val BUTTONCONTROL_BOTTOM_TOP_BUDGET = 12
    }
}