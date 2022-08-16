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
package com.halcyoninae.cosmos.components.bottompane.filelist

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
import com.halcyoninae.halcyon.runtime.Program
import java.awt.*
import java.util.ArrayList
import java.util.Comparator
import javax.swing.plaf.basic.BasicLabelUI
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

/**
 * Represents a Pane containing a list of files for only
 * one directory. It will not contain any sub-directories.
 *
 *
 * This file list can contain any file type, but it will be decided
 * beforehand.
 *
 *
 * This mechanism suggested by FEATURES#8 and deprecated
 * the original tabs mechanism of 3.0.
 *
 * @author Jack Meng
 * @since 3.1
 */
open class FileList : JScrollPane, TabTree {
    /**
     * @return The JTree representing this viewport.
     */
    val tree: JTree

    /**
     * Represents a list of collected files throughout the
     * current selected folder for this instance of a FileList.
     *
     *
     * Parameter 1: [java.io.File] A file object representing a file in the
     * folder.
     * Parameter 2: [javax.swing.tree.DefaultMutableTreeNode] The node
     * instance of the file as represented on the JTree.
     */
    private val fileMap: MutableMap<File, DefaultMutableTreeNode>

    /**
     * @return A FolderInfo object representing this FileList
     */
    @Transient
    val folderInfo: PhysicalFolder

    /**
     * @return A Node that represents the root node.
     */
    val root: DefaultMutableTreeNode

    /**
     * @return boolean
     */
    override var isVirtual: Boolean

    /// FileView Config END
    constructor(
        info: PhysicalFolder, closed: Icon?, open: Icon?, leaf: Icon?, rightClickHideString: String,
        hideStringTask: RightClickHideItemListener?
    ) : super() {
        folderInfo = info
        fileMap = HashMap()
        root = DefaultMutableTreeNode(info.name)
        isVirtual = info is VirtualFolder
        autoscrolls = true
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED)
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED)
        preferredSize = Dimension(FILEVIEW_MIN_WIDTH, FILEVIEW_MIN_HEIGHT)
        getVerticalScrollBar().foreground = ColorManager.MAIN_FG_THEME
        getHorizontalScrollBar().foreground = ColorManager.MAIN_FG_THEME
        minimumSize = Dimension(FILEVIEW_MIN_WIDTH, FILEVIEW_MIN_HEIGHT)
        border = null
        warn(info)
        for (f in info.getFiles(*Manager.ALLOWED_FORMATS)) {
            if (f != null) {
                val node = DefaultMutableTreeNode(if (isVirtual) f.absolutePath else f.name)
                fileMap[f] = node
                root.add(node)
                node.setParent(root)
            }
        }
        tree = JTree(root)
        tree.isRootVisible = true
        tree.showsRootHandles = true
        tree.expandsSelectedPaths = true
        tree.isEditable = false
        tree.isRequestFocusEnabled = false
        tree.scrollsOnExpand = true
        tree.autoscrolls = true
        tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        val renderer = tree.cellRenderer as DefaultTreeCellRenderer
        renderer.closedIcon = resizeImage((closed as ImageIcon?)!!, 16, 16)
        renderer.openIcon = resizeImage((open as ImageIcon?)!!, 16, 16)
        renderer.leafIcon = resizeImage((leaf as ImageIcon?)!!, 16, 16)
        tree.addMouseListener(FVRightClick(this, rightClickHideString, hideStringTask))
        tree.cellRenderer = renderer
        getViewport().add(tree)
    }

    constructor(info: PhysicalFolder) {
        folderInfo = info
        fileMap = HashMap()
        root = DefaultMutableTreeNode(info.name)
        isVirtual = info is VirtualFolder
        autoscrolls = true
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED)
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED)
        preferredSize = Dimension(FILEVIEW_MIN_WIDTH, FILEVIEW_MIN_HEIGHT)
        minimumSize = Dimension(FILEVIEW_MIN_WIDTH, FILEVIEW_MIN_HEIGHT)
        getVerticalScrollBar().foreground = ColorManager.MAIN_FG_THEME
        getHorizontalScrollBar().foreground = ColorManager.MAIN_FG_THEME
        for (f in info.getFiles(*Manager.ALLOWED_FORMATS)) {
            if (f != null && !Program.cacher.isExcluded(f.absolutePath)) {
                val node = DefaultMutableTreeNode(f.name)
                fileMap[f] = node
                root.add(node)
                node.setParent(root)
            }
        }
        tree = JTree(root)
        tree.isRootVisible = true
        tree.showsRootHandles = true
        tree.expandsSelectedPaths = true
        tree.scrollsOnExpand = true
        tree.isEditable = false
        tree.isRequestFocusEnabled = false
        tree.scrollsOnExpand = true
        tree.autoscrolls = true
        tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        border = null
        val renderer = tree.cellRenderer as DefaultTreeCellRenderer
        val closedIcon = Global.rd.getFromAsImageIcon(FILEVIEW_ICON_FOLDER_CLOSED)
        val openIcon = Global.rd.getFromAsImageIcon(FILEVIEW_ICON_FOLDER_OPEN)
        val leafIcon = Global.rd.getFromAsImageIcon(FILEVIEW_ICON_FILE)
        renderer.closedIcon = resizeImage(closedIcon, 16, 16)
        renderer.openIcon = resizeImage(openIcon, 16, 16)
        renderer.leafIcon = resizeImage(leafIcon, 16, 16)
        tree.addMouseListener(FVRightClick(this))
        tree.cellRenderer = renderer
        getViewport().add(tree)
    }

    /**
     * @return Returns the default file map with each File object having a node.
     */
    fun getFileMap(): Map<File, DefaultMutableTreeNode> {
        return fileMap
    }

    /**
     * This function facilitates reloading the current
     * folder:
     *
     *
     * 1. If a file doesn't exist anymore, it will be removed
     * 2. If a new file has been added, it will be added into the Tree
     *
     *
     * The detection on if a folder exists or not is up to the parent
     * BottomPane [com.halcyoninae.cosmos.components.bottompane.BottomPane].
     */
    open fun revalidateFiles() {
        for (f in folderInfo.getFiles(*Manager.ALLOWED_FORMATS)) {
            if (f != null && !fileMap.containsKey(f) && !Program.cacher.isExcluded(f.absolutePath)) {
                val node = DefaultMutableTreeNode(f.name)
                fileMap[f] = node
                root.add(node)
                (tree.model as DefaultTreeModel).reload()
            }
        }
        val toRemove: MutableList<File> = ArrayList()
        for (f in fileMap.keys) {
            if (!f.exists() || !f.isFile
                || Program.cacher.isExcluded(f.absolutePath) && fileMap[f]!!.parent != null
            ) {
                (tree.model as DefaultTreeModel).removeNodeFromParent(fileMap[f])
                toRemove.add(f)
            }
        }
        for (f in toRemove) {
            fileMap.remove(f)
        }
    }

    /**
     * @param nodeName
     */
    override fun remove(nodeName: String) {
        try {
            warn(nodeName)
            for (f in fileMap.keys) {
                if (f.name == nodeName) {
                    val model = tree.model as DefaultTreeModel
                    model.removeNodeFromParent(fileMap[f])
                    model.reload()
                    fileMap.remove(f)
                }
            }
        } catch (e: IllegalArgumentException) {
            // IGNORE
        }
    }

    /**
     * @param node
     * @return String
     */
    override fun getSelectedNode(node: DefaultMutableTreeNode): String {
        for (f in fileMap.keys) {
            if (fileMap[f] == node) {
                return f.absolutePath
            }
        }
        return ""
    }

    override fun sort(e: TabTreeSortMethod) {
        synchronized(fileMap) {
            if ((e == TabTreeSortMethod.ALPHABETICAL)) {
                val nodes: MutableList<DefaultMutableTreeNode?> = ArrayList()
                for (f: File in fileMap.keys) {
                    nodes.add(fileMap[f])
                }
                Collections.sort(
                    nodes,
                    Comparator { o1, o2 -> (o1.userObject as String).compareTo(((o2.userObject as String))) })
                for (node: DefaultMutableTreeNode? in nodes) {
                    root.remove(node)
                    root.add(node)
                }
                (tree.model as DefaultTreeModel).reload()
            } else if ((e == TabTreeSortMethod.REV_ALPHABETICAL)) {
                val nodes: MutableList<DefaultMutableTreeNode?> = ArrayList()
                for (f: File in fileMap.keys) {
                    nodes.add(fileMap[f])
                }
                Collections.sort(nodes, object : Comparator<DefaultMutableTreeNode> {
                    override fun compare(o1: DefaultMutableTreeNode, o2: DefaultMutableTreeNode): Int {
                        return (o2.userObject as String).compareTo(((o1.userObject as String)))
                    }
                })
                for (node: DefaultMutableTreeNode? in nodes) {
                    root.remove(node)
                    root.add(node)
                }
                (tree.model as DefaultTreeModel).reload()
            } else if ((e == TabTreeSortMethod.SHUFFLE)) {
                val nodes: MutableList<DefaultMutableTreeNode?> = ArrayList()
                for (f: File in fileMap.keys) {
                    nodes.add(fileMap[f])
                }
                Collections.shuffle(nodes)
                for (node: DefaultMutableTreeNode? in nodes) {
                    root.remove(node)
                    root.add(node)
                }
                (tree.model as DefaultTreeModel).reload()
            }
        }
    }

    override val fileList: FileList
        get() = this

    companion object {
        /// FileView Config START
        const val FILEVIEW_ICON_FOLDER_OPEN = Manager.RSC_FOLDER_NAME + "/fileview/folder_icon.png"
        const val FILEVIEW_ICON_FOLDER_CLOSED = Manager.RSC_FOLDER_NAME + "/fileview/folder_icon.png"
        const val FILEVIEW_ICON_FILE = Manager.RSC_FOLDER_NAME + "/fileview/leaf.png"
        const val FILEVIEW_DEFAULT_FOLDER_ICON = Manager.RSC_FOLDER_NAME + "/fileview/folder_icon.png"
        const val FILEVIEW_ICON_LIKED_FILE = Manager.RSC_FOLDER_NAME + "/fileview/leaf_like.png"
        const val FILEVIEW_MIN_WIDTH = Manager.MIN_WIDTH - 70
        const val FILEVIEW_MIN_HEIGHT = Manager.MIN_HEIGHT - 50 / 2
        const val FILEVIEW_MAX_WIDTH = Manager.MAX_WIDTH - 50
        const val FILEVIEW_MAX_HEIGHT = Manager.MAX_HEIGHT + 50 / 2 - 40
    }
}