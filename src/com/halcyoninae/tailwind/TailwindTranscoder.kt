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
package com.halcyoninae.tailwind

import java.io.File
import javax.sound.sampled.AudioFormat
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * @author Jack Meng
 * @since 3.2
 */
class TailwindTranscoder : Transcoder {

    companion object {
        /**
         * @param arr
         * @param shift_n
         * @return int[]
         */
        fun byteify(arr: ByteArray, shift_n: Int): IntArray {
            val temp = IntArray(arr.size / 2)
            for (i in 0 until arr.size / 2) {
                temp[i] = (arr[i * 2] and 0xFF.toByte() or (arr[i * 2 + 1] shl if (shift_n == 0) 8 else shift_n)).toInt()
            }
            return temp
        }

        /**
         * @param bps
         * @return int
         */
        @JvmStatic
        fun normalize(bps: Int): Int {
            return bps + 7 shr 3
        }

        /**
         * @param buffer
         * @param transfer
         * @param samples
         * @param b_
         * @param format
         * @return float[]
         */
        fun f_unpack(
            buffer: ByteArray,
            transfer: LongArray,
            samples: FloatArray,
            b_: Int,
            format: AudioFormat
        ): FloatArray {
            if (format.encoding !== AudioFormat.Encoding.PCM_SIGNED && format.encoding !== AudioFormat.Encoding.PCM_UNSIGNED) {
                return samples
            }
            val bps = format.sampleSizeInBits
            val nb = normalize(bps)
            if (format.isBigEndian) {
                var i = 0
                var k = 0
                var j: Int
                while (i < b_) {
                    transfer[k] = 0L
                    val minima = i + nb - 1
                    j = 0
                    while (j < nb) {
                        transfer[k] = transfer[k] or (buffer[minima - j] and 0xFFL.toByte() shl 8 * j)
                        j++
                    }
                    i += nb
                    k++
                }
            } else {
                var i = 0
                var k = 0
                var j: Int
                while (i < b_) {
                    transfer[k] = 0L
                    j = 0
                    while (j < nb) {
                        transfer[k] = transfer[k] or (buffer[i + j] and 0xFFL.toByte() shl 8 * j)
                        j++
                    }
                    i += nb
                    k++
                }
            }
            val scale = Math.pow(2.0, bps - 1.0).toLong()
            if (format.encoding === AudioFormat.Encoding.PCM_SIGNED) {
                val shift = 64L - bps
                for (i in transfer.indices) transfer[i] = transfer[i] shl shift.toInt() shr shift.toInt()
            } else {
                for (i in transfer.indices) {
                    transfer[i] -= scale
                }
            }
            for (i in transfer.indices) {
                samples[i] = transfer[i].toFloat() / scale
            }
            return samples
        }

        /**
         * @param samples
         * @param s_
         * @param format
         * @return
         */
        fun window_func(samples: FloatArray, s_: Int, format: AudioFormat): FloatArray {
            val chnls = format.channels
            val len = s_ / chnls
            var i = 0
            var k: Int
            var j: Int
            while (i < chnls) {
                j = i
                k = 0
                while (j < s_) {
                    samples[j] *= Math.sin(Math.PI * k++ / (len - 1)).toFloat()
                    j += chnls
                }
                i++
            }
            return samples
        }

        /**
         * @param format
         * @param time
         * @return int
         */
        fun msToByte(format: AudioFormat, time: Int): Int {
            return (time * (format.sampleRate * format.channels * format.sampleSizeInBits) / 8000f).toInt()
        }
    }

    override fun transcode(inFormat: Int, outFormat: Int, inLocale: File?, outLocale: File?) {
        return
    }
}

