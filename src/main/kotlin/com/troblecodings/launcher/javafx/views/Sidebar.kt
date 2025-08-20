package com.troblecodings.launcher.javafx.views

import com.troblecodings.launcher.LauncherConstants
import com.troblecodings.launcher.services.UserService
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Label
import javafx.scene.control.OverrunStyle
import javafx.scene.control.Separator
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.util.Builder
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon


class Sidebar(val userService: UserService) : Builder<StackPane> {
    private val userBox: HBox

    // TODO: Make this change when user changes
    init {
        val img = Image(userService.mcProfile!!.skinUrl)
        val newImg = getScaledRegion(img, 64.0, 64.0, 8.0, 8.0, 8.0, 8.0)

        val newImgView = ImageView(newImg).apply {
            fitWidth = 32.0
            fitHeight = 32.0
            isSmooth = false
            isPreserveRatio = true
            isPickOnBounds = true
        }

        userBox = HBox().apply {
            spacing = 4.0
            padding = Insets(8.0, 0.0, 0.0, 0.0)
            children += newImgView.apply {
                style = "-fx-border-color: transparent; -fx-border-radius: 5px;"
            }
            children += Label(userService.mcProfile?.name).apply {
                textFill = Color.LIGHTGRAY
                style = "-fx-font-size: 18px;"
                isUnderline = true
                textOverrun = OverrunStyle.ELLIPSIS
            }
        }
    }

    override fun build(): StackPane = StackPane().apply {
        stylesheets += Sidebar::class.java.getResource("sidebar.css")?.toExternalForm()

        prefWidth = 1280 * 0.20

        background = Background(
            BackgroundFill(
                Color.hsb(12.0, 0.1, 0.19), null, null
            )
        )

        children += VBox().apply {
            alignment = Pos.CENTER
            spacing = 0.0

            children += userBox.apply {
                alignment = Pos.CENTER

            }

            children += Region().apply { VBox.setVgrow(this, Priority.ALWAYS) }

            children += HBox().apply {
                alignment = Pos.CENTER
                border = Border(BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii(0.0), BorderWidths(1.5)))

                onMouseEntered = EventHandler { _: MouseEvent ->
                    background = Background(BackgroundFill(Color.hsb(12.0, 0.1, 0.25), null, null))

                    scene.cursor = Cursor.HAND
                }

                onMouseClicked = EventHandler { event: MouseEvent ->
                }

                onMouseExited = EventHandler { event: MouseEvent ->
                    background = Background(
                        BackgroundFill(
                            Color.hsb(12.0, 0.1, 0.19), null, null
                        )
                    )
                    scene.cursor = Cursor.DEFAULT
                }

                children += FontIcon(FontAwesomeSolid.SLIDERS_H).apply { style = "-fx-icon-color: lightgray; -fx-icon-size: 48px;" }
                children += Label("Settings").apply {
                    padding = Insets(0.0, 0.0, 0.0, 5.0)
                    styleClass += "sidebar-item"
                }
            }

            children += VBox().apply {
                alignment = Pos.CENTER
                spacing = 0.0
                padding = Insets(0.0, 0.0, 5.0, 0.0)

//                setStyle("-fx-border-color: gray; -fx-border-style: none none solid none; -fx-border-width: 2px; ")

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