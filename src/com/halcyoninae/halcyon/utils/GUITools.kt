package com.halcyoninae.halcyon.utils

import java.awt.Component
import java.awt.Container

/**
 * Helper tool methods to do with GUI
 *
 * @author Jack Meng
 * @since 3.3
 */
object GUITools {
    /**
     * Get all sub-components of a container;
     *
     * @param c The container to get the components from
     * @return A list of components
     */
    @JvmStatic
    fun getAllComponents(c: Container): List<Component> {
        val comps = c.components
        val compList = ArrayList<Component>()
        for (comp in comps) {
            compList.add(comp)
            if (comp is Container) compList.addAll(getAllComponents(comp))
        }
        return compList
    }
}