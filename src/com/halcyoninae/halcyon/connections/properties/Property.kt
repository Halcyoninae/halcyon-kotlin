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
package com.halcyoninae.halcyon.connections.properties

/**
 * A template class that holds
 * information on a single property.
 *
 * @author Jack Meng
 * @since 3.1
 */
class Property(propertyName: String, defaultProperty: String, pr: PropertyValidator, vararg commonProperties: String) {
    @kotlin.jvm.JvmField
    var propertyName = ""
    @kotlin.jvm.JvmField
    var defaultProperty = ""
    @kotlin.jvm.JvmField
    var pr: PropertyValidator
    @kotlin.jvm.JvmField
    var commonProperties: Array<String>

    init {
        this.propertyName = propertyName
        this.defaultProperty = defaultProperty
        this.pr = pr
        this.commonProperties = commonProperties as Array<*>
    }

    enum class PropertyFilterType {
        STARTS_WITH, ENDS_WITH, CONTAINS, EQUALS
    }

    companion object {
        /**
         * A method that filters a property based on the filter type.
         *
         * @param nameTag    The name tag to filter
         * @param type       The filter type
         * @param properties The properties to filter
         * @return An array of the allowed filtered content
         * @see Property.PropertyFilterType
         *
         * @since 3.2
         */
        @kotlin.jvm.JvmStatic
        fun filterProperties(nameTag: String, type: PropertyFilterType, vararg properties: Property): Array<Property?> {
            val filteredProperties = arrayOfNulls<Property>(properties.size)
            var i = 0
            // use separate to reduce overhead for constantly checking during looping | might change later ;P
            if (type == PropertyFilterType.STARTS_WITH) {
                for (property in properties) {
                    if (property.propertyName.startsWith(nameTag)) {
                        filteredProperties[i] = property
                        i++
                    }
                }
            } else if (type == PropertyFilterType.ENDS_WITH) {
                for (property in properties) {
                    if (property.propertyName.endsWith(nameTag)) {
                        filteredProperties[i] = property
                        i++
                    }
                }
            } else if (type == PropertyFilterType.CONTAINS) {
                for (property in properties) {
                    if (property.propertyName.contains(nameTag)) {
                        filteredProperties[i] = property
                        i++
                    }
                }
            } else if (type == PropertyFilterType.EQUALS) {
                for (property in properties) {
                    if (property.propertyName == nameTag) {
                        filteredProperties[i] = property
                        i++
                    }
                }
            }
            return filteredProperties
        }
    }
}