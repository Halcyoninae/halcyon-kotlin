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
package com.halcyoninae.cosmos.tasks

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
import java.awt.*
import java.io.*
import javax.swing.plaf.basic.BasicLabelUI

object ThreadedScheduler {
    /*
     * Anything that needs initialization should be done here.
     * <br>
     * This init process is done before anything is displayed or run in the program
     * itself. This is done mostly to configure the current host's system to be
     * fitting the program.
     * <br>
     * Along with this, this process also sets up any Threads that must be running
     * throughout the program either for: user comfort or program functionality.
     * <br>
     * On demand tasks such as those that need to be run on a separate thread must
     * be init by themselves not here.
     */
    init {
        System.setOut(PrintStream(OutputStream.nullOutputStream()))
        System.setProperty("flatlaf.useWindowDecorations", "false")
        System.setProperty("flatlaf.uiScale.enabled", "false")
        System.setProperty("flatlaf.useJetBrainsCustomDecorations", "false")
        UIManager.put("ScrollBar.thumbArc", 999)
        UIManager.put("ScrollBar.trackArc", 999)
        UIManager.put("ScrollBar.background", null)
        UIManager.put("ScrollBar.thumb", ColorUIResource(ColorManager.MAIN_FG_FADED_THEME))
        UIManager.put("Scrollbar.pressedThumbColor", ColorUIResource(ColorManager.MAIN_FG_FADED_THEME))
        UIManager.put("ScrollBar.hoverThumbColor", ColorUIResource(ColorManager.MAIN_FG_FADED_THEME))
        UIManager.put("TabbedPane.showTabSeparators", true)
        UIManager.put("ScrollBar.showButtons", false)
        UIManager.put("TitlePane.closeHoverBackground", ColorUIResource(ColorManager.MAIN_FG_THEME))
        UIManager.put("TitlePane.closePressedBackground", ColorUIResource(ColorManager.MAIN_FG_THEME))
        UIManager.put("TitlePane.buttonHoverBackground", ColorUIResource(ColorManager.MAIN_FG_THEME))
        UIManager.put("TitlePane.buttonPressedBackground", ColorUIResource(ColorManager.MAIN_FG_THEME))
        UIManager.put("TitlePane.closeHoverForeground", ColorUIResource(ColorManager.MAIN_BG_THEME))
        UIManager.put("TitlePane.closePressedForeground", ColorUIResource(ColorManager.MAIN_BG_THEME))
        UIManager.put("TitlePane.buttonHoverForeground", ColorUIResource(ColorManager.MAIN_BG_THEME))
        UIManager.put("TitlePane.buttonPressedForeground", ColorUIResource(ColorManager.MAIN_BG_THEME))
        UIManager.put("Component.focusedBorderColor", nullColor) // takes a generic java.awt.Color object
        UIManager.put("Component.focusColor", nullColor) // takes a generic java.awt.Color object
        UIManager.put("TitlePane.centerTitle", true)
        UIManager.put("TitlePane.buttonSize", Dimension(25, 20))
        UIManager.put("TitlePane.unifiedBackground", true)
        UIManager.put("SplitPaneDivider.gripDotCount", 0)
        UIManager.put("FileChooser.readOnly", false)
        LogManager.getLogManager().reset()
        // fix blurriness on high-DPI screens
        System.setProperty("sun.java2d.opengl", "true")
        System.setProperty("sun.java2d.uiScale", "0.9")

        // set program font
        val e: Enumeration<*> = UIManager.getDefaults().keys()
        while (com.halcyoninae.cosmos.tasks.e.hasMoreElements()) {
            val key: Any = com.halcyoninae.cosmos.tasks.e.nextElement()
            val value = UIManager.get(com.halcyoninae.cosmos.tasks.key)
            if (com.halcyoninae.cosmos.tasks.value is FontUIResource) {
                UIManager.put(com.halcyoninae.cosmos.tasks.key, FontUIResource("Roboto", Font.PLAIN, 13))
            }
        }

        // PROGRAMMABLE THREADS
        val tasks = arrayOf(
            PingFileView(Global.bp),
            DefunctOptimizer()
        )
        for (t in com.halcyoninae.cosmos.tasks.tasks) {
            Thread(com.halcyoninae.cosmos.tasks.t).start()
        }
    }
}