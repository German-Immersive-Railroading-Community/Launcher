package com.troblecodings.launcher.utils

import javafx.scene.Parent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object StylesheetUtils {
    private val logger: Logger = LogManager.getLogger(javaClass)

    fun addOptional(targetLayout: Parent, resourceAnchor: Class<*>, stylesheet: String) {
        val stylesheetUrl = resourceAnchor.getResource(stylesheet);

        if (stylesheetUrl == null) {
            logger.debug("Stylesheet {} not found", stylesheet);
            return;
        }

        if (targetLayout.stylesheets.contains(stylesheetUrl.toExternalForm())) {
            logger.debug("Stylesheet {} already present", stylesheet);
            return;
        }

        targetLayout.stylesheets.add(stylesheetUrl.toExternalForm());
        logger.debug("Stylesheet {} added to layout {}", stylesheet, targetLayout);
    }

    fun addRequired(target: Parent, resourceAnchor: Class<*>) {
        addRequired(target, resourceAnchor, resourceAnchor.getSimpleName() + ".css")
    }

    fun addRequired(target: Parent, resourceAnchor: Class<*>, resourceName: String) {
        val resource = resourceAnchor.getResource(resourceName)

        if (resource == null) {
            logger.debug("Required stylesheet not found: {}", resourceName)
            throw IllegalStateException("Required stylesheet not found: " + resourceName)
        }

        val stylesheet = resource.toExternalForm()

        if (!target.stylesheets.contains(stylesheet)) {
            target.stylesheets.add(stylesheet)
            logger.debug("Stylesheet {} added to layout {}", stylesheet, target)
        }
    }
}