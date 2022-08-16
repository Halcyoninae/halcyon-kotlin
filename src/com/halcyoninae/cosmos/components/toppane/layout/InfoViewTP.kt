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
import javax.swing.UIManager
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
import javax.swing.JFrame
import com.halcyoninae.cosmos.dialog.ErrorWindow
import javax.swing.WindowConstants
import javax.swing.JTextArea
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants
import javax.swing.SwingUtilities
import com.halcyoninae.cosmos.dialog.ConfirmWindow.ConfirmationListener
import com.halcyoninae.cosmos.dialog.ConfirmWindow
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JPanel
import java.awt.event.ActionEvent
import javax.swing.JProgressBar
import javax.swing.BoxLayout
import javax.swing.BorderFactory
import javax.swing.JLabel
import kotlin.jvm.JvmOverloads
import com.halcyoninae.tailwind.TailwindListener.StatusUpdateListener
import javax.swing.JTabbedPane
import javax.swing.JSlider
import javax.swing.SwingConstants
import javax.swing.event.ChangeListener
import javax.swing.event.ChangeEvent
import com.halcyoninae.tailwind.TailwindEvent.TailwindStatus
import com.halcyoninae.tailwind.AudioInfo
import javax.swing.JSplitPane
import kotlin.jvm.Synchronized
import javax.swing.JEditorPane
import com.halcyoninae.cosmos.dialog.AudioInfoDialog
import java.lang.StringBuilder
import com.halcyoninae.halcyon.utils.TimeParser
import com.halcyoninae.halcyon.Halcyon
import javax.swing.JFileChooser
import com.halcyoninae.cosmos.dialog.SelectApplicableFolders.FolderSelectedListener
import javax.swing.filechooser.FileSystemView
import javax.swing.filechooser.FileView
import java.io.File
import javax.swing.Icon
import com.halcyoninae.cosmos.inheritable.FSVDefault
import com.halcyoninae.cosmos.components.bottompane.filelist.TabTree
import javax.swing.JTree
import com.halcyoninae.cosmos.events.FVRightClick.RightClickHideItemListener
import javax.swing.JPopupMenu
import javax.swing.JMenuItem
import java.lang.NullPointerException
import com.halcyoninae.cosmos.components.bottompane.filelist.TabTree.TabTreeSortMethod
import com.halcyoninae.cosmos.dialog.StraightTextDialog
import com.halcyoninae.halcyon.constant.StringManager
import java.awt.event.ComponentListener
import java.awt.event.ComponentEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent
import javax.swing.plaf.LayerUI
import javax.swing.JComponent
import com.halcyoninae.cosmos.special.BlurLayer
import javax.swing.JLayer
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
import javax.swing.ImageIcon
import com.halcyoninae.cosmos.components.toppane.layout.InfoViewTP
import com.halcyoninae.cosmos.components.toppane.layout.InfoViewTP.InfoViewUpdateListener
import com.halcyoninae.cloudspin.CloudSpin
import javax.swing.OverlayLayout
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
import javax.swing.JComboBox
import javax.swing.JTextField
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
import javax.swing.UnsupportedLookAndFeelException
import java.lang.ClassNotFoundException
import java.lang.InstantiationException
import java.lang.IllegalAccessException
import com.halcyoninae.halcyon.utils.GUITools
import com.halcyoninae.cosmos.inheritable.TabButton.CloseTabButton
import com.halcyoninae.halcyon.constant.Global
import com.halcyoninae.halcyon.constant.Manager
import java.awt.*
import java.util.ArrayList
import javax.swing.plaf.basic.BasicLabelUI

/**
 * This class sits on the most upper part of the GUI frame.
 * It displays a simple list of information regarding the current
 * stream and nothing else.
 *
 *
 * This panel does not show every information available, but only a specific
 * part.
 *
 *
 * If the user wants to know more about the audio file
 *
 * @author Jack Meng
 * @see com.halcyoninae.cosmos.dialog.AudioInfoDialog
 *
 * @since 3.0
 */
class InfoViewTP : JPanel(), ComponentListener {
    val INFOVIEW_DISK_NO_FILE_LOADED_ICON_ICON = Global.rd.getFromAsImageIcon(
        INFOVIEW_DISK_NO_FILE_LOADED_ICON
    )
    val INFOVIEW_MIN_WIDTH = Manager.MIN_WIDTH
    val INFOVIEW_MIN_HEIGHT = Manager.MIN_HEIGHT / 4
    val INFOVIEW_MAX_WIDTH = Manager.MAX_WIDTH
    val INFOVIEW_MAX_HEIGHT = Manager.MAX_HEIGHT / 4
    val INFOVIEW_INFODISPLAY_MAX_CHARS = 22
    val INFOVIEW_ARTWORK_RESIZE_TO_HEIGHT = 108
    val INFOVIEW_FLOWLAYOUT_HGAP = 30
    val INFOVIEW_FLOWLAYOUT_VGAP_DIVIDEN = 6
    private val topPanel: JPanel
    private val backPanel: JPanel
    private val infoDisplay: JLabel
    private val artWork: JLabel

    @Transient
    private val listeners: ArrayList<InfoViewUpdateListener>

    /**
     * @return AudioInfo
     */
    @Transient
    var info: AudioInfo
        private set
    private var infoTitle: String
    private var artWorkIsDefault = true

    init {
        listeners = ArrayList()
        preferredSize = Dimension(INFOVIEW_MIN_WIDTH, INFOVIEW_MIN_HEIGHT)
        minimumSize = Dimension(INFOVIEW_MIN_WIDTH, INFOVIEW_MIN_HEIGHT)
        isOpaque = false
        topPanel = JPanel()
        topPanel.preferredSize = Dimension(INFOVIEW_MIN_WIDTH, INFOVIEW_MIN_HEIGHT)
        topPanel.minimumSize = Dimension(INFOVIEW_MIN_WIDTH, INFOVIEW_MIN_HEIGHT)
        topPanel.isOpaque = false
        backPanel = object : JPanel() {
            public override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                if (Halcyon.bgt.getFrame().isVisible && Halcyon.bgt.getFrame().isShowing) {
                    val g2d = g as Graphics2D
                    var compositeAlpha = 0.5f
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED)
                    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED)
                    compositeAlpha =
                        if (ExternalResource.pm[ProgramResourceManager.KEY_INFOVIEW_BACKDROP_GRADIENT_STYLE] == "top") {
                            0.2f
                        } else {
                            0.6f
                        }
                    g2d.composite = AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        compositeAlpha
                    )
                    var original = Global.ifp.info.artwork
                    original = CloudSpin.grabCrop(original, backPanel.visibleRect)
                    if (ExternalResource.pm[ProgramResourceManager.KEY_INFOVIEW_BACKDROP_USE_GRADIENT] == "true") {
                        original = createGradientVertical(original, 255, 0)
                        when (ExternalResource.pm[ProgramResourceManager.KEY_INFOVIEW_BACKDROP_GRADIENT_STYLE]) {
                            "focused" -> original = createGradient(
                                original!!, 255, 0,
                                Directional.BOTTOM
                            )
                            "left" -> original = createGradient(
                                original!!, 255, 0,
                                Directional.LEFT
                            )
                            "right" -> original = createGradient(
                                original!!, 255, 0,
                                Directional.RIGHT
                            )
                        }
                    }
                    if (ExternalResource.pm[ProgramResourceManager.KEY_INFOVIEW_BACKDROP_USE_GREYSCALE] == "true") {
                        original = CloudSpinFilters.filters[CloudSpinFilters.AFF_GREY].filter(original, original)
                        g2d.drawImage(original, 0, 0, backPanel.width, backPanel.height, null)
                    } else {
                        g2d.drawImage(
                            original, 0, 0, backPanel.width, backPanel.height,
                            0, 0, original!!.width, original.height, null
                        )
                    }
                }
            }
        }
        backPanel.setPreferredSize(
            preferredSize
        )
        backPanel.setOpaque(false)
        backPanel.setDoubleBuffered(true)
        info = AudioInfo()
        var bi = imageIconToBI(
            Global.rd.getFromAsImageIcon(INFOVIEW_DISK_NO_FILE_LOADED_ICON)
        )
        bi = resizeNoDistort(
            bi,
            INFOVIEW_ARTWORK_RESIZE_TO_HEIGHT,
            INFOVIEW_ARTWORK_RESIZE_TO_HEIGHT
        )
        bi = createRoundedBorder(bi, 20, ColorManager.BORDER_THEME)
        artWork = JLabel(ImageIcon(bi))
        artWork.border = null
        artWork.horizontalAlignment = SwingConstants.CENTER
        artWork.verticalAlignment = SwingConstants.CENTER
        infoTitle = if (ExternalResource.pm[ProgramResourceManager.KEY_USE_MEDIA_TITLE_AS_INFOVIEW_HEADER]
            == "true"
        ) info.getTag(AudioInfo.KEY_MEDIA_TITLE) else File(info.getTag(AudioInfo.KEY_ABSOLUTE_FILE_PATH)).name
        topPanel.layout = GridLayout(
            1, 3, 15,
            topPanel.preferredSize.height / 2
        )
        infoDisplay = JLabel(infoToString(info, infoTitle, false))
        infoDisplay.horizontalAlignment = SwingConstants.CENTER
        infoDisplay.verticalAlignment = SwingConstants.CENTER
        infoDisplay.toolTipText = infoToString(info, info.getTag(AudioInfo.KEY_MEDIA_TITLE), false)
        infoDisplay.horizontalTextPosition = SwingConstants.LEADING
        infoDisplay.horizontalAlignment = SwingConstants.CENTER
        infoDisplay.verticalAlignment = SwingConstants.CENTER
        topPanel.add(artWork)
        topPanel.add(infoDisplay)
        addComponentListener(this)
        layout = OverlayLayout(this)
        add(topPanel)
        add(backPanel)
        topPanel.isOpaque = false
    }
    /// InfoView Config END
    /**
     * This method is pinged whenever the information regarding
     * the current audio file needs updating.
     *
     *
     * Mostly when the user selects a new track to play.
     *
     * @param f The audio track to play [java.io.File]
     */
    fun setAssets(f: File) {
        if (f.exists() && f.isFile) {
            var beSmart = false
            try {
                info = AudioInfo(f, false)
            } catch (e: InvalidAudioFrameException) {
                beSmart = true
                val defaultMap: MutableMap<String, String> = HashMap()
                defaultMap[AudioInfo.KEY_ABSOLUTE_FILE_PATH] = f.absolutePath
                defaultMap[AudioInfo.KEY_FILE_NAME] = f.name
                defaultMap[AudioInfo.KEY_ALBUM] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_DURATION] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_TITLE] = "Unknown"
                defaultMap[AudioInfo.KEY_BITRATE] = "Unknown"
                defaultMap[AudioInfo.KEY_SAMPLE_RATE] = "Unknown"
                defaultMap[AudioInfo.KEY_ALBUM] = "Unknown"
                defaultMap[AudioInfo.KEY_GENRE] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_ARTIST] = "Unknown"
                defaultMap[AudioInfo.KEY_ARTWORK] = "Unknown"
                info.forceSet(defaultMap)
            } catch (e: CannotReadException) {
                beSmart = true
                val defaultMap: MutableMap<String, String> = HashMap()
                defaultMap[AudioInfo.KEY_ABSOLUTE_FILE_PATH] = f.absolutePath
                defaultMap[AudioInfo.KEY_FILE_NAME] = f.name
                defaultMap[AudioInfo.KEY_ALBUM] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_DURATION] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_TITLE] = "Unknown"
                defaultMap[AudioInfo.KEY_BITRATE] = "Unknown"
                defaultMap[AudioInfo.KEY_SAMPLE_RATE] = "Unknown"
                defaultMap[AudioInfo.KEY_ALBUM] = "Unknown"
                defaultMap[AudioInfo.KEY_GENRE] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_ARTIST] = "Unknown"
                defaultMap[AudioInfo.KEY_ARTWORK] = "Unknown"
                info.forceSet(defaultMap)
            } catch (e: IOException) {
                beSmart = true
                val defaultMap: MutableMap<String, String> = HashMap()
                defaultMap[AudioInfo.KEY_ABSOLUTE_FILE_PATH] = f.absolutePath
                defaultMap[AudioInfo.KEY_FILE_NAME] = f.name
                defaultMap[AudioInfo.KEY_ALBUM] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_DURATION] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_TITLE] = "Unknown"
                defaultMap[AudioInfo.KEY_BITRATE] = "Unknown"
                defaultMap[AudioInfo.KEY_SAMPLE_RATE] = "Unknown"
                defaultMap[AudioInfo.KEY_ALBUM] = "Unknown"
                defaultMap[AudioInfo.KEY_GENRE] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_ARTIST] = "Unknown"
                defaultMap[AudioInfo.KEY_ARTWORK] = "Unknown"
                info.forceSet(defaultMap)
            } catch (e: TagException) {
                beSmart = true
                val defaultMap: MutableMap<String, String> = HashMap()
                defaultMap[AudioInfo.KEY_ABSOLUTE_FILE_PATH] = f.absolutePath
                defaultMap[AudioInfo.KEY_FILE_NAME] = f.name
                defaultMap[AudioInfo.KEY_ALBUM] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_DURATION] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_TITLE] = "Unknown"
                defaultMap[AudioInfo.KEY_BITRATE] = "Unknown"
                defaultMap[AudioInfo.KEY_SAMPLE_RATE] = "Unknown"
                defaultMap[AudioInfo.KEY_ALBUM] = "Unknown"
                defaultMap[AudioInfo.KEY_GENRE] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_ARTIST] = "Unknown"
                defaultMap[AudioInfo.KEY_ARTWORK] = "Unknown"
                info.forceSet(defaultMap)
            } catch (e: ReadOnlyFileException) {
                beSmart = true
                val defaultMap: MutableMap<String, String> = HashMap()
                defaultMap[AudioInfo.KEY_ABSOLUTE_FILE_PATH] = f.absolutePath
                defaultMap[AudioInfo.KEY_FILE_NAME] = f.name
                defaultMap[AudioInfo.KEY_ALBUM] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_DURATION] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_TITLE] = "Unknown"
                defaultMap[AudioInfo.KEY_BITRATE] = "Unknown"
                defaultMap[AudioInfo.KEY_SAMPLE_RATE] = "Unknown"
                defaultMap[AudioInfo.KEY_ALBUM] = "Unknown"
                defaultMap[AudioInfo.KEY_GENRE] = "Unknown"
                defaultMap[AudioInfo.KEY_MEDIA_ARTIST] = "Unknown"
                defaultMap[AudioInfo.KEY_ARTWORK] = "Unknown"
                info.forceSet(defaultMap)
            }
            if (!beSmart) {
                infoTitle = if (ExternalResource.pm[ProgramResourceManager.KEY_USE_MEDIA_TITLE_AS_INFOVIEW_HEADER]
                    == "true"
                ) info.getTag(AudioInfo.KEY_MEDIA_TITLE) else File(info.getTag(AudioInfo.KEY_ABSOLUTE_FILE_PATH)).name
                infoDisplay.text = infoToString(info, infoTitle, false)
                infoDisplay.toolTipText = infoToString(info, info.getTag(AudioInfo.KEY_MEDIA_TITLE), false)
            } else {
                infoTitle = f.name
                warn(">>> $infoTitle")
                infoDisplay.text = infoToString(info, infoTitle, true)
                infoDisplay.toolTipText = infoToString(info, infoTitle, true)
            }
            if (infoDisplay.preferredSize.width >= preferredSize.width -
                artWork.preferredSize.width - INFOVIEW_FLOWLAYOUT_HGAP *
                2
                && Halcyon.bgt != null
            ) {
                Halcyon.bgt.getFrame().size = Dimension(
                    Manager.MAX_WIDTH,
                    Halcyon.bgt.getFrame().minimumSize.height
                )
            }
            if (info.hasArtwork()) {
                warn("Artwork found for drawing!")
                var bi: BufferedImage? = null
                bi = if (!beSmart) resizeNoDistort(
                    info.artwork, INFOVIEW_ARTWORK_RESIZE_TO_HEIGHT,
                    INFOVIEW_ARTWORK_RESIZE_TO_HEIGHT
                ) else resizeNoDistort(
                    bi!!,
                    INFOVIEW_ARTWORK_RESIZE_TO_HEIGHT,
                    INFOVIEW_ARTWORK_RESIZE_TO_HEIGHT
                )
                bi = createRoundedBorder(bi, 20, ColorManager.BORDER_THEME)
                artWork.icon = ImageIcon(bi)
                artWork.repaint(30)
                artWorkIsDefault = false
            } else {
                warn("Artwork reset!")
                var bi = resizeNoDistort(
                    imageIconToBI(INFOVIEW_DISK_NO_FILE_LOADED_ICON_ICON),
                    INFOVIEW_ARTWORK_RESIZE_TO_HEIGHT, INFOVIEW_ARTWORK_RESIZE_TO_HEIGHT
                )
                bi = createRoundedBorder(bi, 20, ColorManager.BORDER_THEME)
                artWork.icon = ImageIcon(bi)
                artWork.repaint(30)
                artWorkIsDefault = true
            }
            backPanel.repaint(100)
            dispatchEvents()
            System.gc()
        }
    }

    /**
     * This method alerts every linked listener about
     * the new info being updated
     *
     *
     * This method is threaded in order to blocking
     * other functionalities.
     */
    private fun dispatchEvents() {
        warn("InfoView Preparing a dispatch: " + info.getTag(AudioInfo.KEY_ABSOLUTE_FILE_PATH))
        SwingUtilities.invokeLater {
            for (l in listeners) {
                l.infoView(info)
            }
        }
    }

    /**
     * Adds a listener to this GUI component, if this listener
     * from that class wants information regarding any updates.
     *
     * @param l A listener that can be called
     */
    fun addInfoViewUpdateListener(l: InfoViewUpdateListener) {
        listeners.add(l)
    }

    /**
     * This internal method converts the given audio info into
     * the string information. This string text displays the following
     * information;
     *
     *  * Title of the track
     *  * Artist of the track
     *  * Bitrate, SampleRate, and Duration
     *
     *
     * @param info    The track to generate the information off of
     * @param text    The title of the track
     * @param beSmart Tells the parser to be smart and guess certain details.
     * @return An HTML string that can be used by html supporting GUI Components to
     * display the information.
     */
    private fun infoToString(info: AudioInfo, text: String, beSmart: Boolean): String {
        return if (!beSmart) {
            ("<html><body style=\"font-family='Trebuchet MS';\"><p style=\"text-align: left;\"><span style=\"color: "
                    +
                    ColorManager.MAIN_FG_STR +
                    ";font-size: 12px;\"><strong>" +
                    text
                    +
                    "</strong></span></p><p style=\"text-align: left;\"><span style=\"color: #ffffff;font-size: 10px\">" +
                    info.getTag(AudioInfo.KEY_MEDIA_ARTIST) +
                    "</span></p><p style=\"text-align: left;\"><span style=\"color: #ffffff;font-size: 8px\">" +
                    info.getTag(AudioInfo.KEY_BITRATE) +
                    "kpbs," +
                    info.getTag(AudioInfo.KEY_SAMPLE_RATE) +
                    "kHz," +
                    fromSeconds(info.getTag(AudioInfo.KEY_MEDIA_DURATION).toInt())
                    +
                    "</span></p></body></html>")
        } else {
            val author = if (text.contains("-")) text.split("-".toRegex(), 2).toTypedArray()[0] else "Unknown"
            ("<html><body style=\"font-family='Trebuchet MS';\"><p style=\"text-align: left;\"><span style=\"color: "
                    +
                    ColorManager.MAIN_FG_STR +
                    ";font-size: 12px;\"><strong>" +
                    text
                    +
                    "</strong></span></p><p style=\"text-align: left;\"><span style=\"color: #ffffff;font-size: 10px\">" +
                    author +
                    "</span></p><p style=\"text-align: left;\"><span style=\"color: #ffffff;font-size: 8px\">" +
                    "Unknown" +
                    "kpbs," +
                    "Unknown" +
                    "kHz," +
                    "N:N:N"
                    +
                    "</span></p></body></html>")
        }
    }

    /**
     * @param e
     */
    override fun componentResized(e: ComponentEvent) {
        // FOR FUTURE IMPLEMENTATIONS
    }

    /**
     * @param e
     */
    override fun componentMoved(e: ComponentEvent) {
        // IGNORED
    }

    /**
     * @param e
     */
    override fun componentShown(e: ComponentEvent) {
        // IGNORED
    }

    /**
     * @param e
     */
    override fun componentHidden(e: ComponentEvent) {
        // IGNORED
    }

    /**
     * An extended listener for any classes that want
     * to get events regarding any info changes.
     *
     * @author Jack Meng
     * @since 3.0
     */
    interface InfoViewUpdateListener {
        fun infoView(info: AudioInfo)
    }

    companion object {
        /// InfoView Config START
        const val INFOVIEW_DISK_NO_FILE_LOADED_ICON = Manager.RSC_FOLDER_NAME + "/infoview/disk.png"
    }
}