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
package com.halcyoninae.halcyon.cacher

import com.halcyoninae.halcyon.debug.Debugger.log
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.SAXException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * @author Jack Meng
 * @since 3.2
 */
class Cacher(private val file: File) {
    private var doc: Document? = null

    init {
        val factory = DocumentBuilderFactory.newInstance()
        val builder: DocumentBuilder
        try {
            builder = factory.newDocumentBuilder()
            doc = builder.parse(file)
        } catch (e: ParserConfigurationException) {
            log<Exception>(e)
        } catch (e: SAXException) {
            log<Exception>(e)
        } catch (e: IOException) {
            log<Exception>(e)
        }
    }

    /**
     * @param nodeName
     * @return String[]
     */
    fun getContent(nodeName: String?): Array<String?> {
        val content: MutableList<String?> = ArrayList()
        val node = doc!!.getElementsByTagName(nodeName)
        for (i in 0 until node.length) {
            val n = node.item(i)
            if (n.nodeType == Node.ELEMENT_NODE) {
                val e = n as Element
                content.add(e.textContent)
            }
        }
        return content.toTypedArray()
    }

    /**
     * @param rootName
     * @param content
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    @Throws(TransformerException::class, ParserConfigurationException::class)
    fun build(rootName: String?, content: Map<String, String?>) {
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val newDoc = builder.newDocument()
        val root = newDoc.createElement(rootName)
        newDoc.appendChild(root)
        val keys = content.keys
        for (key in keys) {
            val child = newDoc.createElement(key)
            child.textContent = content[key]
            root.appendChild(child)
        }
        try {
            FileOutputStream(file).use { fos ->
                val transformerFactory = TransformerFactory.newInstance()
                val transformer = transformerFactory.newTransformer()
                val source = DOMSource(newDoc)
                val result = StreamResult(fos)
                transformer.transform(source, result)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}