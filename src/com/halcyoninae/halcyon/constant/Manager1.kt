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

/**
 * A global scope project manager that contains
 * constants to most used objects and values throughout the
 * program.
 *
 * @author Jack Meng
 * @since 3.0
 */
interface Manager {
    companion object {
        const val BUTTON_STD_ICON_WIDTH_N_HEIGHT = 16

        /// BIGCONTAINER Size Config START ///
        const val MIN_WIDTH = 460
        const val MIN_HEIGHT = 500
        const val MAX_WIDTH = MIN_HEIGHT + 40
        const val MAX_HEIGHT = MIN_HEIGHT + 40

        /// BIGCONTAINER Size Config END ///
        /// GENERAL RESOURCE LOCATION START
        const val RSC_FOLDER_NAME = "resources"

        /// GENERAL RESOURCE LOCATION END
        @kotlin.jvm.JvmField
        val ALLOWED_FORMATS = arrayOf("wav", "mp3", "aiff", "aif", "aifc", "ogg", "oga")

        /// BEGIN RESOURCE LOCATION FOR ICONS
        const val GITHUB_LOGO_LIGHT = RSC_FOLDER_NAME + "/external/github_light.png"
        const val BBLOC_MINIMIZED_PLAYER = RSC_FOLDER_NAME + "/bbloc/minimize.png"
        const val BBLOC_REFRESH_FILEVIEW_ICON = RSC_FOLDER_NAME + "/bbloc/refresh_icon.png"
        const val BBLOC_REFRESH_FILEVIEW_ICON_PRESSED = RSC_FOLDER_NAME + "/bbloc/refresh_icon_pressed.png"
        const val PROGRAM_ICON_LOGO = RSC_FOLDER_NAME + "/app/logo2.png"
        const val PROGRAM_GREEN_LOGO = RSC_FOLDER_NAME + "/app/logo_green_ping.png"
        const val PROGRAM_BLUE_LOGO = RSC_FOLDER_NAME + "/app/logo_blue_ping.png"
        const val PROGRAM_RED_LOGO = RSC_FOLDER_NAME + "/app/logo_red_ping.png"
        const val PROGRAM_ORANGE_LOGO = RSC_FOLDER_NAME + "/app/logo_orange_ping.png"
        const val PROGRAM_PINK_LOGO = RSC_FOLDER_NAME + "/app/logo_pink_ping.png"

        /// END RESOURCE LOCATION FOR ICONS
        /// TABBED View Config Start
        const val PLAYLIST_TAB_ICON = RSC_FOLDER_NAME + "/tabsview/playlist_tab.png"
        const val SLIDERS_TAB_ICON = RSC_FOLDER_NAME + "/tabsview/slider_tab.png"
        const val FILEVIEW_DEFAULT_TAB_NAME = "Playlist"
        const val SETTINGS_DEFAULT_TAB_NAME = "Settings"
        const val SLIDERS_DEFAULT_TAB_NAME = "Controls"
        const val FILEVIEW_DEFAULT_TAB_TOOLTIP = "View your selected playlist(s) here."
        const val TAB_VIEW_MIN_TEXT_STRIP_LENGTH = 10

        /// TABBED View Config End
        /// BBLOC BUTTONS Config START
        const val ADDFOLDER_BUTTON_TEXT = "+"
        const val ADDFOLDER_BUTTON_TOOLTIP = "Add a new folder to the playlist."
        const val ADDFOLDER_BUTTON_DEFAULT_ICON = RSC_FOLDER_NAME + "/bbloc/add_folder.png"
        const val ADDFOLDER_BUTTON_PRESSED_ICON = RSC_FOLDER_NAME + "/bbloc/add_folder_pressed.png"
        const val SETTINGS_BUTTON_DEFAULT_ICON = RSC_FOLDER_NAME + "/bbloc/settings_normal.png"
        const val SETTINGS_BUTTON_PRESSED_ICON = RSC_FOLDER_NAME + "/bbloc/settings_pressed.png"
        const val SLIDERS_BUTTON_DEFAULT_ICON = RSC_FOLDER_NAME + "/bbloc/sliders.png"
        const val SETTINGS_BUTTON_TOOLTIP = "Open the settings dialog."
        const val PROJECTPAGE_BUTTON_TOOLTIP = "Visit this project's GitHub page."
        const val REFRESH_BUTTON_TOOLTIP = "Refresh the playlist."

        /// BBLOC BUTTONS Config END
        /// SETTINGS DIALOG Config START
        const val SETTINGS_MIN_WIDTH = 500
        const val SETTINGS_MIN_HEIGHT = 400 /// SETTINGS DIALOG Config END
    }
}