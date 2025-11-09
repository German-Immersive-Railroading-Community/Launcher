package com.troblecodings.launcher.javafx.views;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.LauncherConstants;
import com.troblecodings.launcher.LauncherView;
import com.troblecodings.launcher.javafx.ViewManager;
import com.troblecodings.launcher.services.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j // TODO: Maybe unneeded.
@RequiredArgsConstructor
public class Sidebar implements Builder<StackPane> {
    private final ViewManager viewManager;
    private final UserService userService;

    private final StringProperty userNameProperty = new SimpleStringProperty("");

    private final ImageView userImgView = new ImageView();

    private final HBox userBoxContent = new HBox();

    private final HBox userBox = new HBox();

    private final HBox innerUserBox = new HBox();

    private final List<SidebarItem> sidebarItems = new ArrayList<>(List.of(
            new SidebarItem("Home", new FontIcon(FontAwesomeSolid.HOME), LauncherView.HOME),
            new SidebarItem("Mods", new FontIcon(FontAwesomeSolid.COGS), LauncherView.MODS),
            new SidebarItem("Settings", new FontIcon(FontAwesomeSolid.SLIDERS_H), LauncherView.SETTINGS)
    ));

    private void updateUserBox() {
        if (userService.isLoggedIn()) {
            var profile = Objects.requireNonNull(userService.getMcProfile());
            var tempImg = new Image(profile.getSkinUrl());
            var finalImg = getScaledRegion(tempImg, 64.0, 64.0, 8.0, 8.0, 8.0, 8.0);
            userImgView.setImage(finalImg);
            userNameProperty.set(profile.getName());
        } else {
            userImgView.setImage(null);
            userNameProperty.set("Not logged in");
        }
    }

    private void navigateToView(LauncherView view) {
        if (!userService.isLoggedIn()) return;

        viewManager.setCurrentView(view);
    }

    @Override
    public StackPane build() {
        userService.loggedInProperty().addListener((_, _, _) -> updateUserBox());
        if (userService.isLoggedIn()) {
            updateUserBox();
        }

        userImgView.setFitWidth(32.0);
        userImgView.setFitHeight(32.0);
        userImgView.setSmooth(false);
        userImgView.setPreserveRatio(true);
        userImgView.setPickOnBounds(true);

        var userNameLabel = new Label();
        userNameLabel.setTextFill(Color.LIGHTGRAY);
        userNameLabel.setStyle("-fx-font-size: 18px");
        userNameLabel.setUnderline(false);
        userNameLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        userNameLabel.setTranslateY(3.0);
        userNameLabel.textProperty().bind(userNameProperty);

        userBoxContent.setSpacing(10.0);
        userBoxContent.getChildren().add(userImgView);
        userBoxContent.getChildren().add(userNameLabel);

        innerUserBox.setSpacing(10.0);
        innerUserBox.setPadding(new Insets(8.0, 16.0, 8.0, 16.0));
        innerUserBox.getStyleClass().add("sidebar-user");
        innerUserBox.getChildren().add(userBoxContent);
        innerUserBox.setOnMouseClicked(_ -> {
            var profile = userService.getMcProfile();
            if (profile == null) {
                return;
            }

            var url = profile.getSkinUrl();
            Launcher.getInstance().getHostServices().showDocument(url);
        });

        userBox.setAlignment(Pos.CENTER);
        userBox.setPadding(new Insets(8.0, 0.0, 8.0, 0.0));
        userBox.getChildren().add(innerUserBox);

        var items = new ArrayList<HBox>();
        for (var item : sidebarItems) {
            var box = new HBox();
            box.setAlignment(Pos.CENTER);
            box.setStyle("-fx-border: solid; -fx-border-color: white; -fx-border-radius: 0px; -fx-border-width: 1.5px;");
            box.getStyleClass().add("sidebar-item");
            box.setOnMouseClicked(_ -> navigateToView(item.getAssociatedView()));

            var icon = item.getIcon();
            icon.setStyle("-fx-icon-color: lightgray; -fx-icon-size: 48px;");
            box.getChildren().add(icon);

            var label = new Label(item.getName());
            label.setPadding(new Insets(0.0, 0.0, 0.0, 5.0));
            label.getStyleClass().add("sidebar-item-name");
            box.getChildren().add(label);

            items.add(box);
        }

        var footerSeparator = new Separator();
        footerSeparator.setTranslateY(20.0);
        footerSeparator.setPadding(new Insets(0.0, 7.0, 0.0, 7.0));

        var footerImgView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/troblecodings/launcher/girc_logo_w_1.png"))));
        footerImgView.setTranslateY(10.0);

        var footerLabel = new Label(LauncherConstants.APP_NAME + " v" + LauncherConstants.APP_VERSION);
        footerLabel.setTextFill(Color.GRAY);

        var footerBox = new VBox();
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setSpacing(0.0);
        footerBox.setPadding(new Insets(0.0, 0.0, 5.0, 0.0));
        footerBox.getChildren().add(footerSeparator);
        footerBox.getChildren().add(footerImgView);
        footerBox.getChildren().add(footerLabel);

        var spacerRegion = new Region();
        VBox.setVgrow(spacerRegion, Priority.ALWAYS);

        var innerVBox = new VBox();
        innerVBox.setAlignment(Pos.CENTER);
        innerVBox.setSpacing(0.0);
        innerVBox.getChildren().add(userBox);
        innerVBox.getChildren().addAll(items);
        innerVBox.getChildren().add(spacerRegion);
        innerVBox.getChildren().add(footerBox);

        var stackPane = new StackPane();
        stackPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("sidebar.css")).toExternalForm());
        stackPane.setStyle("-fx-background-color: hsb(12.0, 10%, 19%)");
        stackPane.setPrefWidth(1280 * 0.20);
        stackPane.getChildren().add(innerVBox);
        return stackPane;
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
    private WritableImage getScaledRegion(@Nullable Image src, double width, double height, double x, double y, double w, double h) {
        var canvas = new Canvas(width, height);
        var gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.setFill(Color.TRANSPARENT);
        gc.drawImage(
                src,
                x, y, w, h,
                0.0, 0.0, width, height
        );

        var params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        return canvas.snapshot(params, null);
    }
}
