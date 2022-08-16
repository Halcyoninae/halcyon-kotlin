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
package com.halcyoninae.halcyon.debug

import com.halcyoninae.halcyon.DefaultManager
import com.halcyoninae.halcyon.utils.TextParser.strip
import com.halcyoninae.halcyon.utils.TimeParser.logCurrentTime
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.util.*
import javax.lang.model.type.NullType

/**
 * This is an external class that is called upon for when the
 * program needs something printed the Console.
 *
 *
 * However the standard console or System.out [java.lang.System].out can
 * be disabled for extraneous logging.
 *
 *
 * This means the program must use out.
 *
 * @author Jack Meng
 * @since 3.0
 */
object Debugger {
    /**
     * The designated output stream to be used by
     * the debugger with attached encoding.
     */
    var out = PrintStream(System.err, true, StandardCharsets.UTF_8)

    /**
     * A globally modifiable variable (should only be modified
     * programmatically) to either disable or enable during runtime.
     *
     *
     * Reminder: This does not clear the stream if it is called during
     * runtime; this boolean only stops any further asynced outputstream
     * that have not reached the stage of a check on the state of this
     * variable's equality.
     */
    var DISABLE_DEBUGGER = true

    /**
     * This function returns the default heder text to define itself as a
     * logging message instead of other extraneous messages.
     *
     * @param am
     * @return The Header Log text
     */
    fun getLogText(am: Class<*>): String {
        return "[LOG ~ MP4J@" + logCurrentTime + "@" + strip(am.simpleName, 5) + "] > "
    }

    /**
     * @param am
     * @return String Get's the default warning message header.
     */
    fun getWarnText(am: Class<*>): String {
        return "[WRN ~ MP4J@" + logCurrentTime + "@" + strip(am.simpleName, 5) + "] > "
    }

    /**
     * @param am
     * @return String Gets the default success/good message header
     */
    fun getGoodText(am: Class<*>): String {
        return "[SCS ~ MP4J@" + logCurrentTime + "@" + strip(am.simpleName, 5) + "] > "
    }

    /**
     * Default alert
     *
     * @param am
     * @return
     */
    fun getProgramText(am: Class<*>): String {
        return "[PGM ~ MP4J@" + logCurrentTime + "@" + strip(am.simpleName, 5) + "] > "
    }

    /**
     * @param am
     * @return String
     */
    fun getDefaultInfoText(am: Class<*>): String {
        return "[INF ~ MP4J@" + logCurrentTime + "@" + strip(am.simpleName, 5) + "] > "
    }

    /**
     * Prints the necessary Objects to System.err
     *
     * @param <T> The varargs of types as a generic
     * @param o   The objects to be printed to the stream
    </T> */
    @JvmStatic
    @SafeVarargs
    fun <T> log(vararg o: T) {
        if (!DISABLE_DEBUGGER) {
            Thread {
                for (t in o) {
                    if (t != null) {
                        out.println(getLogText(o.javaClass) + t + " ")
                    } else {
                        out.println(getLogText(o.javaClass) + "NULL_CONTENT" + " ")
                    }
                }
                out.println()
            }.start()
        } else {
            val s = arrayOfNulls<String>(o.size)
            var i = 0
            for (t in o) {
                s[i] = t.toString()
                i++
            }
        }
    }

    /**
     * Similar to [.log] but with custom
     * coloring and custom header to signify a warning log.
     *
     *
     * This is not specifically for debugging and is used to
     * signify a fault somewhere that needs major attention.
     *
     * @param <T> The varargs of types as a generic
     * @param o   The objects to be printed to the stream
    </T> */
    @JvmStatic
    @SafeVarargs
    fun <T> warn(vararg o: T) {
        if (!DefaultManager.DEBUG_PROGRAM) {
            for (t in o) {
                if (t != null) {
                    out.println(
                        CLIStyles.BOLD.color + getWarnText(t.javaClass)
                                + CLIStyles.RESET.color
                                + CLIStyles.YELLOW_TXT.color + t
                                + CLIStyles.RESET.color
                    )
                } else {
                    out.println(
                        CLIStyles.BOLD.color
                                + getWarnText(NullType::class.java)
                                + CLIStyles.RESET.color
                                + CLIStyles.YELLOW_TXT.color + "NULL_CONTENT"
                                + CLIStyles.RESET.color
                    )
                }
            }
        }
    }

    /**
     * Similar to [.log] and [.warn]
     * but instead is for when something goes good.
     *
     *
     * This is not specifically for debugging and is used
     * to signify a success message.
     *
     * @param <T> The varargs of types as a generic
     * @param o   The objects to be printed to the stream
    </T> */
    @JvmStatic
    @SafeVarargs
    fun <T> good(vararg o: T) {
        if (!DefaultManager.DEBUG_PROGRAM) {
            for (t in o) {
                if (t != null) {
                    out.println(
                        CLIStyles.BOLD.color + getGoodText(t.javaClass)
                                + CLIStyles.RESET.color
                                + CLIStyles.GREEN_TXT.color + t
                                + CLIStyles.RESET.color
                    )
                } else {
                    out.println(
                        CLIStyles.BOLD.color + getGoodText(
                            NullType::class.java
                        )
                                + CLIStyles.RESET.color
                                + CLIStyles.GREEN_TXT.color + "NULL_CONTENT"
                                + CLIStyles.RESET.color
                    )
                }
            }
        }
    }

    /**
     * @param e
     */
    fun byteLog(vararg e: Byte) {
        if (!DefaultManager.DEBUG_PROGRAM) {
            out.println(Arrays.toString(e))
        }
    }

    /**
     * This method is only used for programming purposes.
     *
     * @param <T>
     * @param o
    </T> */
    @JvmStatic
    @SafeVarargs
    fun <T> unsafeLog(vararg o: T) {
        if (!DefaultManager.DEBUG_PROGRAM) {
            for (t in o) {
                if (t != null) {
                    out.println(getLogText(o.javaClass) + t + " ")
                } else {
                    out.println(getLogText(o.javaClass) + "NULL_CONTENT" + " ")
                }
            }
        }
    }

    /**
     * @param <T>
     * @param o
     * @since 3.3
    </T> */
    @JvmStatic
    @SafeVarargs
    fun <T> info(vararg o: T) {
        if (!DefaultManager.DEBUG_PROGRAM) {
            for (t in o) {
                if (t != null) {
                    out.println(
                        CLIStyles.BOLD.color + getGoodText(t.javaClass)
                                + CLIStyles.RESET.color
                                + CLIStyles.BLUE_TXT.color + t
                                + CLIStyles.RESET.color
                    )
                } else {
                    out.println(
                        CLIStyles.BOLD.color + getGoodText(
                            NullType::class.java
                        )
                                + CLIStyles.RESET.color
                                + CLIStyles.BLUE_TXT.color + "NULL_CONTENT"
                                + CLIStyles.RESET.color
                    )
                }
            }
        }
    }

    /**
     *
     * @param t
     */
    @JvmStatic
    fun alert(vararg t: TConstr?) {
        if (!DefaultManager.DEBUG_PROGRAM) {
            for (x in t) {
                if (x != null) {
                    out.println(
                        CLIStyles.BOLD.color + getProgramText(x.javaClass) + CLIStyles.RESET.color
                                + x.toString() + CLIStyles.RESET.color
                    )
                } else {
                    out.println(
                        CLIStyles.BOLD.color + getProgramText(
                            NullType::class.java
                        )
                                + CLIStyles.RESET.color
                                + CLIStyles.BLUE_TXT.color + "NULL_CONTENT"
                                + CLIStyles.RESET.color
                    )
                }
            }
        }
    }
}