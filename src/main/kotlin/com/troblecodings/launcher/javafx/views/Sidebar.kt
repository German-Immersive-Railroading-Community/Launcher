package com.troblecodings.launcher.javafx.views

import com.troblecodings.launcher.Launcher
import com.troblecodings.launcher.LauncherConstants
import com.troblecodings.launcher.LauncherView
import com.troblecodings.launcher.javafx.ViewManager
import com.troblecodings.launcher.services.UserService
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Label
import javafx.scene.control.OverrunStyle
import javafx.scene.control.Separator
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.util.Builder
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

data class SidebarItem(val name: String, val icon: FontIcon, val associatedView: LauncherView) {
}


class Sidebar(val viewManager: ViewManager, val userService: UserService) : Builder<StackPane> {
    private val userImgView: ImageView = ImageView()
    private val userBoxContent: HBox = HBox().apply {
        children += userImgView

        children += Label(userService.mcProfile?.name).apply {
            textFill = Color.LIGHTGRAY
            style = "-fx-font-size: 18px;"
            isUnderline = false
            textOverrun = OverrunStyle.ELLIPSIS
            translateY = 3.0
        }
    }

    private val userBox: HBox = HBox().apply {
        children += HBox().apply {
            spacing = 10.0
            padding = Insets(8.0, 16.0, 8.0, 16.0)

            styleClass += "sidebar-user"

            children += userBoxContent

            onMouseClicked = EventHandler { _ ->
                val url = userService.mcProfile?.skinUrl ?: return@EventHandler // If we don't have a skin url, just return lol
                Launcher.getInstance().hostServices.showDocument(url)
            }
        }
    }

    private val sidebarItems: Array<SidebarItem> = arrayOf(
        SidebarItem("Home", FontIcon(FontAwesomeSolid.HOME), LauncherView.HOME),
        SidebarItem("Mods", FontIcon(FontAwesomeSolid.COGS), LauncherView.MODS),
        SidebarItem("Settings", FontIcon(FontAwesomeSolid.SLIDERS_H), LauncherView.SETTINGS)
    )

    fun updateUserBox() {

    }

    // TODO: Make this change when user changes
//    init {
//        val img = Image(userService.mcProfile!!.skinUrl)
//        val newImg = getScaledRegion(img, 64.0, 64.0, 8.0, 8.0, 8.0, 8.0)
//
//        val newImgView = ImageView(newImg).apply {
//            fitWidth = 32.0
//            fitHeight = 32.0
//            isSmooth = false
//            isPreserveRatio = true
//            isPickOnBounds = true
//        }
//    }

    override fun build(): StackPane = StackPane().apply {
        stylesheets += Sidebar::class.java.getResource("sidebar.css")?.toExternalForm()
        style = "-fx-background-color: hsb(12.0, 10%, 19%);"

        prefWidth = 1280 * 0.20

        children += VBox().apply {
            alignment = Pos.CENTER
            spacing = 0.0

            children += userBox.apply {
                alignment = Pos.CENTER
                userService.isLoggedIn = true
            }

            for (item in sidebarItems) {
                children += HBox().apply {
                    alignment = Pos.CENTER
                    style = "-fx-border: solid; -fx-border-color: white; -fx-border-radius: 0px; -fx-border-width: 1.5px;"

                    styleClass += "sidebar-item"

                    children += item.icon.apply { style = "-fx-icon-color: lightgray; -fx-icon-size: 48px;" }
                    children += Label(item.name).apply {
                        padding = Insets(0.0, 0.0, 0.0, 5.0)
                        styleClass += "sidebar-item-name"
                    }

                    onMouseClicked = EventHandler { _ ->
                        viewManager.setCurrentView(item.associatedView)
                    }
                }
            }

            children += Region().apply { VBox.setVgrow(this, Priority.ALWAYS) }

            children += VBox().apply {
                alignment = Pos.CENTER
                spacing = 0.0
                padding = Insets(0.0, 0.0, 5.0, 0.0)

                children += Separator().apply {
                    translateY = 20.0
                    padding = Insets(0.0, 7.0, 0.0, 7.0)
                }

                children += ImageView(Image(Sidebar::class.java.getResourceAsStream("/com/troblecodings/launcher/girc_logo_w_1.png"))).apply {
                    translateY = 10.0
                }

                children += Label("GIRC Launcher v" + LauncherConstants.APP_VERSION).apply { style = "-fx-text-fill: gray;" }
            }
        }
    }

    /**
     * Extracts a region from an image and scales it by a factor using nearest-neighbor.
     *
     * @param src    The source Image
     * @param width  The width of the new Image
     * @param height The height of the new Image
     * @param x      The top-left X coordinate of the region
     * @param y      The top-left Y coordinate of the region
     * @param w      The width of the region
     * @param h      The height of the region
     * @return A new WritableImage containing the scaled region
     */
    fun getScaledRegion(src: Image?, width: Double, height: Double, x: Double, y: Double, w: Double, h: Double): WritableImage {
        val canvas = Canvas(width, height)
        val gc: GraphicsContext = canvas.getGraphicsContext2D()
        gc.isImageSmoothing = false
        gc.fill = Color.TRANSPARENT
        gc.drawImage(
            src,
            x, y, w, h,
            0.0, 0.0, width, height
        )

        val params: SnapshotParameters = SnapshotParameters().apply {
            fill = Color.TRANSPARENT
        }

        return canvas.snapshot(params, null)
    }
}